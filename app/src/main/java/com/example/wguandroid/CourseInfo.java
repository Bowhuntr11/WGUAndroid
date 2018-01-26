package com.example.wguandroid;

import android.app.DatePickerDialog;
import android.app.LoaderManager;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
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
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class CourseInfo extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    private String courseFilter;
    public CursorAdapter cursorAdapter;
    private SharedPreferences shaPref;
    private SharedPreferences.Editor shaPrefEdit;
    private String note;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_course_info);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Intent intent = getIntent();
        courseFilter = intent.getStringExtra("ID");
        String courseName = intent.getStringExtra("courseName");
        String courseStart = intent.getStringExtra("courseStart");
        String courseEnd = intent.getStringExtra("courseEnd");
        String courseMentor = intent.getStringExtra("courseMentor");
        String mentorNumber = intent.getStringExtra("mentorNumber");
        String mentorEmail = intent.getStringExtra("mentorEmail");
        String courseProgress = intent.getStringExtra("courseProgress");

        getSupportActionBar().setTitle(courseName);

        TextView cStart = (TextView) findViewById(R.id.start_date_course);
        cStart.setText(courseStart);
        TextView cEnd = (TextView) findViewById(R.id.end_date_course);
        cEnd.setText(courseEnd);
        TextView cMentor = (TextView) findViewById(R.id.mentor_name_course);
        cMentor.setText(courseMentor);
        TextView mNumber = (TextView) findViewById(R.id.mentor_number_course);
        mNumber.setText(mentorNumber);
        TextView mEmail = (TextView) findViewById(R.id.mentor_email_course);
        mEmail.setText(mentorEmail);
        TextView cProgress = (TextView) findViewById(R.id.progress_course);
        cProgress.setText(courseProgress);

        cursorAdapter = new CustomCursorAdapter(this, null, 0);
        ListView list = (ListView) findViewById(R.id.assessmentsListView);
        list.setAdapter(cursorAdapter);

        getLoaderManager().initLoader(0, null, this);

        shaPref = PreferenceManager.getDefaultSharedPreferences(this);
        shaPrefEdit = shaPref.edit();
        note = shaPref.getString(courseFilter, "");


        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LayoutInflater layoutInflater = LayoutInflater.from(CourseInfo.this);
                View promptView = layoutInflater.inflate(R.layout.add_assessment_dialog, null);

                final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(CourseInfo.this);

                alertDialogBuilder.setView(promptView);

                final EditText assessmentName = (EditText) promptView.findViewById(R.id.assessmentName);
                final Button goalDate = (Button) promptView.findViewById(R.id.goal_date);
                final Calendar c1 = Calendar.getInstance();
                c1.set(1900, 1, 1);
                final Calendar c2 = Calendar.getInstance();


                goalDate.setOnClickListener(new View.OnClickListener() {
                                                @Override
                                                public void onClick(View v) {
                                                    DatePickerDialog dpd = new DatePickerDialog(CourseInfo.this,
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
                            Toast.makeText(CourseInfo.this, "You didn't fill out all the information, or the date is too far in the past", Toast.LENGTH_LONG).show();
                        } else {
                            insertAssessment(aName, c1);
                            alertD.dismiss();
                            refreshAdapter();
                            Toast.makeText(CourseInfo.this, " New Assessment added ! ", Toast.LENGTH_LONG).show();
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
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_assessment, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_notes) {
            LayoutInflater layoutInflater = LayoutInflater.from(CourseInfo.this);
            View promptView = layoutInflater.inflate(R.layout.note_dialog, null);
            final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(CourseInfo.this);
            alertDialogBuilder.setView(promptView);
            alertDialogBuilder
                    .setCancelable(false)
                    .
                            setPositiveButton("Save Note", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {
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

            final EditText notesTaken = (EditText) alertD.findViewById(R.id.notes_added);
            notesTaken.setText(note);
            notesTaken.setSelection(notesTaken.getText().length());

            // Override of onClick so that when user selects Save button it checks to make sure fields are filled out
            alertD.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    note = notesTaken.getText().toString();
                    if (note.isEmpty()) {
                        Toast.makeText(CourseInfo.this, "You didn't put in a note!", Toast.LENGTH_LONG).show();
                    } else {
                        shaPrefEdit.putString(courseFilter, note);
                        shaPrefEdit.commit();
                        alertD.dismiss();
                        refreshAdapter();
                        Toast.makeText(CourseInfo.this, " New Note added ! ", Toast.LENGTH_LONG).show();
                    }
                }
            });

            Button share = (Button) alertD.findViewById(R.id.share_btn);
            share.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    note = notesTaken.getText().toString();
                    Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
                    sharingIntent.setType("text/plain");
                    String shareBody = note;
                    String shareSub = "Sharing note!";
                    sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, shareSub);
                    sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, shareBody);
                    startActivity(Intent.createChooser(sharingIntent, "Share using"));
                }
            });
        }
        //noinspection SimplifiableIfStatement
        if (id == R.id.action_delete) {
            DialogInterface.OnClickListener dialogClickListener =
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int button) {
                            if (button == DialogInterface.BUTTON_POSITIVE) {
                                //Insert Data management code here
                                getContentResolver().delete(
                                        CoursesProvider.CONTENT_URI, "_id = " + courseFilter, null
                                );
                                finish();
                                Toast.makeText(CourseInfo.this,
                                        getString(R.string.course_deleted),
                                        Toast.LENGTH_SHORT).show();
                            }
                        }
                    };

            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage(getString(R.string.are_you_sure_assessment))
                    .setPositiveButton(getString(android.R.string.yes), dialogClickListener)
                    .setNegativeButton(getString(android.R.string.no), dialogClickListener)
                    .show();
            return true;
        }

        if (id == R.id.action_edit) {
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onResume()
    {  // After a pause OR at startup
        super.onResume();
        refreshAdapter();
    }
}



