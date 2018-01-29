package com.example.wguandroid;

import android.annotation.SuppressLint;
import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

public class CustomCursorAdapter extends CursorAdapter {
    private LayoutInflater mInflater;
    private Context contextType;

    public CustomCursorAdapter(Context context, Cursor c, int flags) {
        super(context, c, flags);
        mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        this.contextType = context;
        if (this.contextType instanceof Terms) {
            return mInflater.inflate(R.layout.termlist, parent, false);
        } else if (this.contextType instanceof TermCourses) {
            return mInflater.inflate(R.layout.courselist, parent, false);
        } else {
            return mInflater.inflate(R.layout.assessmentlist, parent, false);
        }
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        this.contextType = context;
        if ( this.contextType instanceof Terms ) {
            TextView content = (TextView) view.findViewById(R.id.term_text);
            content.setText(cursor.getString(cursor.getColumnIndex(DBOpenHelper.TERM_NAME)));
            TextView termDate = (TextView) view.findViewById(R.id.term_date);
            termDate.setText(cursor.getString(cursor.getColumnIndex(DBOpenHelper.TERM_START)) + " to " +
                    cursor.getString(cursor.getColumnIndex(DBOpenHelper.TERM_END)));
        } else if ( this.contextType instanceof TermCourses){
            TextView content = (TextView) view.findViewById(R.id.course_text);
            content.setText(cursor.getString(cursor.getColumnIndex(DBOpenHelper.COURSE_NAME)));
            TextView courseDate = (TextView) view.findViewById(R.id.course_date);
            courseDate.setText(cursor.getString(cursor.getColumnIndex(DBOpenHelper.COURSE_START)) + " to " +
                    cursor.getString(cursor.getColumnIndex(DBOpenHelper.COURSE_END)));
        } else {
            TextView content = (TextView) view.findViewById(R.id.assessment_text);
            content.setText(cursor.getString(cursor.getColumnIndex(DBOpenHelper.ASSESSMENT_NAME)));
            TextView courseDate = (TextView) view.findViewById(R.id.assessment_date);
            courseDate.setText(cursor.getString(cursor.getColumnIndex(DBOpenHelper.ASSESSMENT_DATE)));
        }
    }

}
