package com.example.wguandroid;

import android.app.DatePickerDialog;
import android.app.LoaderManager;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.app.Activity;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CursorAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.FilterQueryProvider;
import android.widget.ListView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

public class Courses extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor>  {

    private String termFilter;
    private String termName;
    public CursorAdapter cursorAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_courses);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Intent intent = getIntent();
        termFilter = intent.getStringExtra("ID");
        termName = intent.getStringExtra("termName");

        getSupportActionBar().setTitle(termName);

        cursorAdapter = new CustomCursorAdapter(this, null , 0, termFilter);

        ListView list = (ListView) findViewById(R.id.courses);
        list.setAdapter(cursorAdapter);

        getLoaderManager().initLoader(0, null, this);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LayoutInflater layoutInflater = LayoutInflater.from(Courses.this);
                View promptView = layoutInflater.inflate(R.layout.add_course_dialog, null);

                final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(Courses.this);

                alertDialogBuilder.setView(promptView);

                final EditText courseName = (EditText) promptView.findViewById(R.id.courseName);
                final Button startDate = (Button) promptView.findViewById(R.id.start_date);
                final Button endDate = (Button) promptView.findViewById(R.id.end_date);
                final Calendar c1 = Calendar.getInstance();
                final Calendar c2 = Calendar.getInstance();
                endDate.setEnabled(false);


                startDate.setOnClickListener(new View.OnClickListener() {
                                                 @Override
                                                 public void onClick(View v) {

                                                     Calendar cal = Calendar.getInstance();

                                                     DatePickerDialog dpd = new DatePickerDialog(Courses.this,
                                                             new DatePickerDialog.OnDateSetListener() {

                                                                 @Override
                                                                 public void onDateSet(DatePicker view, int year,
                                                                                       int monthOfYear, int dayOfMonth) {
                                                                     startDate.setText((monthOfYear + 1) + "/" + dayOfMonth + "/" + year);
                                                                     endDate.setEnabled(true);
                                                                     c1.set(year, monthOfYear, dayOfMonth);
                                                                 }
                                                             }, cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DATE));
                                                     dpd.setTitle("Start of Term Date");
                                                     dpd.show();
                                                 }

                                             }
                );

                endDate.setOnClickListener(new View.OnClickListener() {
                                               @Override
                                               public void onClick(View v) {
                                                   Calendar cal = Calendar.getInstance();

                                                   DatePickerDialog dpd = new DatePickerDialog(Courses.this,
                                                           new DatePickerDialog.OnDateSetListener() {

                                                               @Override
                                                               public void onDateSet(DatePicker view, int year,
                                                                                     int monthOfYear, int dayOfMonth) {
                                                                   endDate.setText((monthOfYear + 1) + "/" + dayOfMonth + "/" + year);
                                                                   c2.set(year, monthOfYear, dayOfMonth);
                                                               }
                                                           }, cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DATE));
                                                   dpd.setTitle("End of Term Date");
                                                   DatePicker dp = dpd.getDatePicker();
                                                   dp.setMinDate(c1.getTimeInMillis());
                                                   dpd.show();

                                               }
                                           }

                );

                alertDialogBuilder
                        .setCancelable(false)
                        .

                                setPositiveButton("Save Course", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int id) {
                                                String name = courseName.getText().toString();

                                                if (name.isEmpty()) {
                                                    Toast.makeText(Courses.this, "You need to name the Course!", Toast.LENGTH_LONG).show();
                                                    return;
                                                } else {
                                                    insertCourse(name, c1, c2);
                                                    dialog.dismiss();
                                                    refreshAdapter();
                                                    Toast.makeText(Courses.this, " New Course added ! \n ", Toast.LENGTH_LONG).show();
                                                }
                                            }
                                        }

                                )
                        .

                                setNegativeButton("Cancel",
                                        new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int id) {
                                                dialog.cancel();
                                            }
                                        });

                // create an alert dialog
                AlertDialog alertD = alertDialogBuilder.create();
                alertD.show();


            }
        });
    }

    private void refreshAdapter() {
        getLoaderManager().restartLoader(0, null, this);
    }

    private void insertCourse(String courseName, Calendar c1, Calendar c2) {
        SimpleDateFormat ft = new SimpleDateFormat("MM-dd-yyyy");
        ContentValues values = new ContentValues();
        values.put(DBOpenHelper.COURSE_NAME, courseName);
        values.put(DBOpenHelper.COURSE_START, ft.format(c1.getTime()));
        values.put(DBOpenHelper.COURSE_END, ft.format(c2.getTime()));
        values.put(DBOpenHelper.TERM_KEY, termFilter);
        Uri termsUri = getContentResolver().insert(CoursesProvider.CONTENT_URI, values);

        Log.d("CourseActivity", "Inserted course " + (termsUri != null ? termsUri.getLastPathSegment() : null));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_delete, menu);
        return true;
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String selection = DBOpenHelper.TERM_KEY + "=";
        String[] selectionArgs = {
                String.valueOf(termFilter)
        };
        Log.d("onCreateLoader", selection);
        for (String s : selectionArgs) {
            Log.d("onCreateLoader", s);
        }

        CursorLoader cl = new CursorLoader(this, CoursesProvider.CONTENT_URI,
                null, selection + "\"" + termFilter + "\"", null, null);
        return cl;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        Log.d("onLoadFinished", termFilter);
        cursorAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        cursorAdapter.swapCursor(null);
    }
}
