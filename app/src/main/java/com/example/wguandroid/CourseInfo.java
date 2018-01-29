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

public class CourseInfo extends AppCompatActivity {

    private String courseFilter;
    private SharedPreferences.Editor shaPrefEdit;
    private String note;
    private static final int COURSE_ID = 1001;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_course_info);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Intent intent = getIntent();
        courseFilter = intent.getStringExtra("ID");
        final String courseName = intent.getStringExtra("courseName");
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

        SharedPreferences shaPref = PreferenceManager.getDefaultSharedPreferences(this);
        shaPrefEdit = shaPref.edit();
        note = shaPref.getString(courseFilter, "");

        Button assessmentBtn = (Button) findViewById(R.id.assessmentsBtn);
        assessmentBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(CourseInfo.this, CourseAssessments.class);
                intent.putExtra("ID", courseFilter);
                intent.putExtra("courseName", courseName);
                startActivityForResult(intent, COURSE_ID);
            }
        });

        Button deleteCourseBtn = (Button) findViewById(R.id.deleteCourseBtn);
        deleteCourseBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Insert Data management code here
                getContentResolver().delete(
                        CoursesProvider.CONTENT_URI, "_id = " + courseFilter, null
                );
                finish();
                Toast.makeText(CourseInfo.this,
                        getString(R.string.course_deleted),
                        Toast.LENGTH_SHORT).show();
            }
        });

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
        return super.onOptionsItemSelected(item);
    }
}



