package com.example.wguandroid;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

public class AssessmentsProvider extends ContentProvider {

    private static final String AUTHORITY = "com.example.wguandroid.assessmentsprovider";
    private static final String BASE_PATH = "assessments";
    public static final Uri CONTENT_URI =
            Uri.parse("content://" + AUTHORITY + "/" + BASE_PATH);

    // Constant to identify the requested operation
    private static final int ASSESSMENTS = 1;
    private static final int ASSESSMENT_ID = 2;

    public static final String CONTENT_ASSESSMENT_TYPE = "CourseInfo";

    private static final UriMatcher uriMatcher =
            new UriMatcher(UriMatcher.NO_MATCH);

    static {
        uriMatcher.addURI(AUTHORITY, BASE_PATH, ASSESSMENTS);
        uriMatcher.addURI(AUTHORITY, BASE_PATH + "/#", ASSESSMENT_ID);
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

        if (uriMatcher.match(uri) == ASSESSMENT_ID) {
            selection = DBOpenHelper.ASSESSMENT_ID + "=" + uri.getLastPathSegment();
        }

        return database.query(DBOpenHelper.TABLE_ASSESSMENTS, DBOpenHelper.ASSESSMENT_COLUMNS, selection, null, null, null,
                DBOpenHelper.ASSESSMENT_CREATED + " DESC");
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        return null;
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues values) {
        long id = database.insert(DBOpenHelper.TABLE_ASSESSMENTS, null, values);
        return Uri.parse(BASE_PATH + "/" + id);
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        return database.delete(DBOpenHelper.TABLE_ASSESSMENTS, selection, selectionArgs);
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues values, @Nullable String selection, @Nullable String[] selectionArgs) {
        return database.update(DBOpenHelper.TABLE_ASSESSMENTS, values, selection, selectionArgs);
    }
}
