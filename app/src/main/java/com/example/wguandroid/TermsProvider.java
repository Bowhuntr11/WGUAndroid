package com.example.wguandroid;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

public class TermsProvider extends ContentProvider {

    private static final String AUTHORITY = "com.example.wguandroid.termsprovider";
    private static final String BASE_PATH = "terms";
    public static final Uri CONTENT_URI =
            Uri.parse("content://" + AUTHORITY + "/" + BASE_PATH);

    // Constant to identify the requested operation
    private static final int TERMS = 1;
    private static final int TERMS_ID = 2;

    public static final String CONTENT_TERM_TYPE = "Term";

    private static final UriMatcher uriMatcher =
            new UriMatcher(UriMatcher.NO_MATCH);

    static {
        uriMatcher.addURI(AUTHORITY, BASE_PATH, TERMS);
        uriMatcher.addURI(AUTHORITY, BASE_PATH + "/#", TERMS_ID);
    }

    private SQLiteDatabase database;

    @Override
    public boolean onCreate() {
        DBOpenHelper helper = new DBOpenHelper(getContext());
        database = helper.getWritableDatabase();
        return true;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder) {

        if (uriMatcher.match(uri) == TERMS_ID) {
            selection = DBOpenHelper.TERM_ID + "=" + uri.getLastPathSegment();
        }

        return database.query(DBOpenHelper.TABLE_TERMS, DBOpenHelper.TERM_COLUMNS, selection, null, null, null,
                DBOpenHelper.TERM_CREATED + " DESC");
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        return null;
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues values) {
        long id = database.insert(DBOpenHelper.TABLE_TERMS, null, values);
        return Uri.parse(BASE_PATH + "/" + id);
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        return database.delete(DBOpenHelper.TABLE_TERMS, selection, selectionArgs);
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues values, @Nullable String selection, @Nullable String[] selectionArgs) {
        return database.update(DBOpenHelper.TABLE_TERMS, values, selection, selectionArgs);
    }
}
