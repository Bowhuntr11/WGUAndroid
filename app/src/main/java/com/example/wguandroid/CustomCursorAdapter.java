package com.example.wguandroid;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

public class CustomCursorAdapter extends CursorAdapter {
    private LayoutInflater mInflater;

    public CustomCursorAdapter(Context context, Cursor c, int flags) {
        super(context, c, flags);
        mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return mInflater.inflate(R.layout.termlist, parent, false);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        TextView content = (TextView) view.findViewById(R.id.term_text);
        content.setText(cursor.getString(cursor.getColumnIndex(DBOpenHelper.TERM_NAME)));
        TextView termDate = (TextView) view.findViewById(R.id.term_date);
        termDate.setText(cursor.getString(cursor.getColumnIndex(DBOpenHelper.TERM_START)) + " to " +
                cursor.getString(cursor.getColumnIndex(DBOpenHelper.TERM_END)));
    }
}
