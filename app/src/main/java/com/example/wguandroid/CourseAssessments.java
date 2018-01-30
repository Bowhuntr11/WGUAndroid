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
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CursorAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class CourseAssessments extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    private String courseFilter;
    public CursorAdapter cursorAdapter;
    public static int ASSESSMENT_ID = 1001;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_course_assessments);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Intent intent = getIntent();
        courseFilter = intent.getStringExtra("ID");
        String courseName = intent.getStringExtra("courseName");

        getSupportActionBar().setTitle(courseName);

        cursorAdapter = new CustomCursorAdapter(this, null, 0);
        ListView list = (ListView) findViewById(R.id.assessmentsListView);
        list.setAdapter(cursorAdapter);

        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Uri.parse(AssessmentsProvider.CONTENT_URI + "/" + id);
                Cursor row = (Cursor) parent.getItemAtPosition(position);
                String _id = row.getString(row.getColumnIndexOrThrow("_id"));
                String assessmentName = row.getString(row.getColumnIndex("assessmentName"));
                String assessmentDate = row.getString(row.getColumnIndex("assessmentDate"));
                Intent intent = new Intent(CourseAssessments.this, AssessmentInfo.class);
                intent.putExtra("ID", _id);
                intent.putExtra("assessmentName", assessmentName);
                intent.putExtra("assessmentDate", assessmentDate);
                intent.putExtra("courseFilter", courseFilter);
                startActivityForResult(intent, ASSESSMENT_ID);
            }
        });

        getLoaderManager().initLoader(0, null, this);


        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LayoutInflater layoutInflater = LayoutInflater.from(CourseAssessments.this);
                View promptView = layoutInflater.inflate(R.layout.add_assessment_dialog, null);

                final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(CourseAssessments.this);

                alertDialogBuilder.setView(promptView);

                final EditText assessmentName = (EditText) promptView.findViewById(R.id.assessmentName);
                final Button goalDate = (Button) promptView.findViewById(R.id.goal_date);
                final Calendar c1 = Calendar.getInstance();
                c1.set(1900, 1, 1);
                final Calendar c2 = Calendar.getInstance();


                goalDate.setOnClickListener(new View.OnClickListener() {
                                                @Override
                                                public void onClick(View v) {
                                                    DatePickerDialog dpd = new DatePickerDialog(CourseAssessments.this,
                                                            new DatePickerDialog.OnDateSetListener() {

                                                                @Override
                                                                public void onDateSet(DatePicker view, int year,
                                                                                      int monthOfYear, int dayOfMonth) {
                                                                    goalDate.setText((monthOfYear + 1) + "/" + dayOfMonth + "/" + year);
                                                                    c1.set(year, monthOfYear, dayOfMonth);
                                                                }
                                                            }, c2.get(Calendar.YEAR), c2.get(Calendar.MONTH), c2.get(Calendar.DATE));
                                                    dpd.setTitle("Goal Date");
                                                    dpd.show();
                                                }

                                            }
                );

                alertDialogBuilder
                        .setCancelable(false)
                        .

                                setPositiveButton("Save Assessment", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int id) {
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
                final AlertDialog alertD = alertDialogBuilder.create();
                alertD.show();

                // Override of onClick so that when user selects Save button it checks to make sure fields are filled out
                alertD.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String aName = assessmentName.getText().toString();
                        Log.d("Assessment Dialog", String.valueOf(c1.get(Calendar.YEAR)));

                        if (aName.isEmpty() || c1.get(Calendar.YEAR) <= 1900) {
                            Toast.makeText(CourseAssessments.this, "You didn't fill out all the information, or the date is too far in the past", Toast.LENGTH_LONG).show();
                        } else {
                            insertAssessment(aName, c1);
                            alertD.dismiss();
                            refreshAdapter();
                            Toast.makeText(CourseAssessments.this, " New Assessment added ! ", Toast.LENGTH_LONG).show();
                        }
                    }
                });

            }
        });
    }


    private void refreshAdapter() {
        getLoaderManager().restartLoader(0, null, this);
    }

    private void insertAssessment(String assessmentName, Calendar c1) {
        SimpleDateFormat ft = new SimpleDateFormat("MM-dd-yyyy");
        ContentValues values = new ContentValues();
        values.put(DBOpenHelper.ASSESSMENT_NAME, assessmentName);
        values.put(DBOpenHelper.ASSESSMENT_DATE, ft.format(c1.getTime()));
        values.put(DBOpenHelper.COURSE_KEY, courseFilter);
        Uri termsUri = getContentResolver().insert(AssessmentsProvider.CONTENT_URI, values);

        Log.d("AssessmentActivity", "Inserted assessment " + (termsUri != null ? termsUri.getLastPathSegment() : null));
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String selection = DBOpenHelper.COURSE_KEY + "=";
        String[] selectionArgs = {
                String.valueOf(courseFilter)
        };
        Log.d("onCreateLoader", selection);
        for (String s : selectionArgs) {
            Log.d("onCreateLoader", s);
        }

        CursorLoader cl = new CursorLoader(this, AssessmentsProvider.CONTENT_URI,
                null, selection + "\"" + courseFilter + "\"", null, null);
        return cl;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        cursorAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        cursorAdapter.swapCursor(null);
    }

    @Override
    public void onResume() {  // After a pause OR at startup
        super.onResume();
        refreshAdapter();
    }
}



