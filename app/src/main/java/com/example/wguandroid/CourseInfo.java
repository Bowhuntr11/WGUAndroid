package com.example.wguandroid;

import android.annotation.TargetApi;
import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.PendingIntent;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class CourseInfo extends AppCompatActivity {

    private String courseFilter;
    private String termFilter;
    private SharedPreferences.Editor shaPrefEdit;
    private ArrayAdapter<CharSequence> spinnerAdapter;
    private String note;
    private static final int COURSE_ID = 1001;
    private String value;
    private String courseName;
    private String courseStart;
    private String courseEnd;

    private AlarmManager alarmManager;
    private PendingIntent pendingIntent;
    private static CourseInfo inst;
    private TextView alarmTextView;
    private Calendar calendar;

    public static CourseInfo instance() {
        return inst;
    }

    @Override
    public void onStart() {
        super.onStart();
        inst = this;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_course_info);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Intent intent = getIntent();
        courseFilter = intent.getStringExtra("ID");
        courseName = intent.getStringExtra("courseName");
        courseStart = intent.getStringExtra("courseStart");
        courseEnd = intent.getStringExtra("courseEnd");
        final String courseMentor = intent.getStringExtra("courseMentor");
        final String mentorNumber = intent.getStringExtra("mentorNumber");
        final String mentorEmail = intent.getStringExtra("mentorEmail");
        final String courseProgress = intent.getStringExtra("courseProgress");
        termFilter = intent.getStringExtra("termFilter");

        getSupportActionBar().setTitle(courseName);
        alarmTextView = (TextView) findViewById(R.id.courseAlarmText);

        ToggleButton tgStart = (ToggleButton) findViewById(R.id.courseStartAlarmToggle);
        SharedPreferences shaPref = PreferenceManager.getDefaultSharedPreferences(this);
        boolean tgStartCourse = shaPref.getBoolean(courseName + "tgStartCourse", false);
        if (tgStartCourse) {
            tgStart.setChecked(true);
        } else {
            tgStart.setChecked(false);
        }

        ToggleButton tgEnd = (ToggleButton) findViewById(R.id.courseEndAlarmToggle);
        boolean tgEndCourse = shaPref.getBoolean(courseName + "tgEndCourse", false);
        if (tgEndCourse) {
            tgEnd.setChecked(true);
        } else {
            tgEnd.setChecked(false);
        }

        final TextView cStart = (TextView) findViewById(R.id.start_date_course);
        cStart.setText(courseStart);
        TextView cEnd = (TextView) findViewById(R.id.end_date_course);
        cEnd.setText(courseEnd);
        TextView cMentor = (TextView) findViewById(R.id.mentor_name_course);
        cMentor.setText(courseMentor);
        TextView mNumber = (TextView) findViewById(R.id.mentor_number_course);
        mNumber.setText(mentorNumber);
        TextView mEmail = (TextView) findViewById(R.id.mentor_email_course);
        mEmail.setText(mentorEmail);
        final TextView cProgress = (TextView) findViewById(R.id.progress_course);
        cProgress.setText(courseProgress);

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
                getContentResolver().delete(
                        CoursesProvider.CONTENT_URI, "_id = " + courseFilter, null
                );
                finish();
                Toast.makeText(CourseInfo.this,
                        getString(R.string.course_deleted),
                        Toast.LENGTH_SHORT).show();
            }
        });

        Button editCourseBtn = (Button) findViewById(R.id.editCourseBtn);
        editCourseBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LayoutInflater layoutInflater = LayoutInflater.from(CourseInfo.this);
                View promptView = layoutInflater.inflate(R.layout.edit_course_dialog, null);

                final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(CourseInfo.this);

                alertDialogBuilder.setView(promptView);

                final EditText editcourseName = (EditText) promptView.findViewById(R.id.edit_courseName);
                editcourseName.setText(courseName);
                final Button editstartDate = (Button) promptView.findViewById(R.id.edit_course_start_date);
                editstartDate.setText(courseStart);
                final Button editendDate = (Button) promptView.findViewById(R.id.edit_course_end_date);
                editendDate.setText(courseEnd);
                final Calendar c1 = Calendar.getInstance();
                final Calendar c2 = Calendar.getInstance();
                final EditText editmentorName = (EditText) promptView.findViewById(R.id.edit_mentorName);
                editmentorName.setText(courseMentor);
                final EditText editmentorNumber = (EditText) promptView.findViewById(R.id.edit_mentorNumber);
                editmentorNumber.setText(mentorNumber);
                final EditText editmentorEmail = (EditText) promptView.findViewById(R.id.edit_mentorEmail);
                editmentorEmail.setText(mentorEmail);

                SimpleDateFormat sdf = new SimpleDateFormat("MM-dd-yyyy");
                Date date = null;
                Date date2 = null;
                try {
                    date = sdf.parse(courseStart);
                    date2 = sdf.parse(courseEnd);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                c1.setTime(date);
                c2.setTime(date2);

                editstartDate.setOnClickListener(new View.OnClickListener() {
                                                     @Override
                                                     public void onClick(View v) {
                                                         DatePickerDialog dpd = new DatePickerDialog(CourseInfo.this,
                                                                 new DatePickerDialog.OnDateSetListener() {

                                                                     @Override
                                                                     public void onDateSet(DatePicker view, int year,
                                                                                           int monthOfYear, int dayOfMonth) {
                                                                         editstartDate.setText((monthOfYear + 1) + "/" + dayOfMonth + "/" + year);
                                                                         c1.set(year, monthOfYear, dayOfMonth);
                                                                     }
                                                                 }, c1.get(Calendar.YEAR), c1.get(Calendar.MONTH), c1.get(Calendar.DATE));
                                                         dpd.setTitle("Start of Course Date");
                                                         dpd.show();
                                                     }

                                                 }
                );

                editendDate.setOnClickListener(new View.OnClickListener() {
                                                   @Override
                                                   public void onClick(View v) {
                                                       DatePickerDialog dpd = new DatePickerDialog(CourseInfo.this,
                                                               new DatePickerDialog.OnDateSetListener() {

                                                                   @Override
                                                                   public void onDateSet(DatePicker view, int year,
                                                                                         int monthOfYear, int dayOfMonth) {
                                                                       editendDate.setText((monthOfYear + 1) + "/" + dayOfMonth + "/" + year);
                                                                       c2.set(year, monthOfYear, dayOfMonth);
                                                                   }
                                                               }, c2.get(Calendar.YEAR), c2.get(Calendar.MONTH), c2.get(Calendar.DATE));
                                                       dpd.setTitle("End of Course Date");
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
                        String cName = editcourseName.getText().toString();
                        String cProgress = value;
                        String mName = editmentorName.getText().toString();
                        String mNumber = editmentorNumber.getText().toString();
                        String mEmail = editmentorEmail.getText().toString();

                        if (cName.isEmpty() || cProgress.isEmpty() || mName.isEmpty() || mNumber.isEmpty() || mEmail.isEmpty()
                                || c1.get(Calendar.YEAR) <= 1900 || c2.get(Calendar.YEAR) <= 1900) {
                            Toast.makeText(CourseInfo.this, "You need to fill out all the information", Toast.LENGTH_LONG).show();
                        } else {
                            editCourse(cName, cProgress, c1, c2, mName, mNumber, mEmail);
                            alertD.dismiss();
                            finish();
                            Toast.makeText(CourseInfo.this, "Course Updated! ", Toast.LENGTH_LONG).show();
                        }
                    }
                });

                // Applying adapter to Spinner on Course Dialog
                final Spinner spinner = (Spinner) alertD.findViewById(R.id.edit_progressSpinner);
                spinnerAdapter = ArrayAdapter.createFromResource(CourseInfo.this,
                        R.array.progress_array, android.R.layout.simple_spinner_item);
                spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinner.setAdapter(spinnerAdapter);
                int spinnerPosition = spinnerAdapter.getPosition(courseProgress);
                spinner.setSelection(spinnerPosition);


                spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
                        value = spinner.getSelectedItem().toString();
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> adapterView) {
                        value = spinner.getSelectedItem().toString();
                    }
                });
            }
        });

    }

    private void editCourse(String courseName, String cProgress, Calendar c1, Calendar c2, String mName, String mNumber, String mEmail) {
        SimpleDateFormat ft = new SimpleDateFormat("MM-dd-yyyy");
        ContentValues values = new ContentValues();
        values.put(DBOpenHelper.COURSE_NAME, courseName);
        values.put(DBOpenHelper.COURSE_START, ft.format(c1.getTime()));
        values.put(DBOpenHelper.COURSE_END, ft.format(c2.getTime()));
        values.put(DBOpenHelper.COURSE_STATUS, cProgress);
        values.put(DBOpenHelper.COURSE_MENTOR, mName);
        values.put(DBOpenHelper.MENTOR_NUMBER, mNumber);
        values.put(DBOpenHelper.MENTOR_EMAIL, mEmail);
        values.put(DBOpenHelper.TERM_KEY, termFilter);
        int courseUpdated = getContentResolver().update(CoursesProvider.CONTENT_URI, values, "_id = " + courseFilter, null);


        Log.d("CourseInfo", courseFilter);
        Log.d("CourseActivity", "Updated course: " + String.valueOf(courseUpdated));
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

    public void onClickedStart(View view) {
        SharedPreferences shaPref = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = shaPref.edit();

        alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);

        if (((ToggleButton) view).isChecked()) {
            editor.putBoolean(courseName + "tgStartCourse", true); // value to store
            editor.apply();
            Log.d("CourseInfo", "Course Start Alarm On");

            Intent myIntent = new Intent(CourseInfo.this, CourseAlarmReceiver.class);
            pendingIntent = PendingIntent.getBroadcast(getBaseContext(), 0, myIntent, PendingIntent.FLAG_UPDATE_CURRENT);

            calendar = Calendar.getInstance();

            SimpleDateFormat sdf = new SimpleDateFormat("MM-dd-yyyy");
            Date date = null;
            try {
                date = sdf.parse(courseStart);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            calendar.setTime(date);
            alarmManager.setInexactRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(),
                    AlarmManager.INTERVAL_DAY, pendingIntent);

        } else {
            editor.putBoolean(courseName + "tgStartCourse", false); // value to store
            editor.apply();
            alarmManager.cancel(pendingIntent);
            setAlarmText("");
            Log.d("CourseInfo", "Course Start Alarm Off");
        }
    }

    public void onClickedEnd(View view) {
        SharedPreferences shaPref = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = shaPref.edit();

        alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);

        if (((ToggleButton) view).isChecked()) {
            editor.putBoolean(courseName + "tgEndCourse", true);
            editor.apply();
            Log.d("CourseInfo", "Course End Alarm On");

            Intent myIntent = new Intent(CourseInfo.this, CourseAlarmReceiver.class);
            pendingIntent = PendingIntent.getBroadcast(getBaseContext(), 0, myIntent, PendingIntent.FLAG_UPDATE_CURRENT);

            calendar = Calendar.getInstance();

            SimpleDateFormat sdf = new SimpleDateFormat("MM-dd-yyyy");
            Date date = null;
            try {
                date = sdf.parse(courseEnd);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            calendar.setTime(date);
            alarmManager.setInexactRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(),
                    AlarmManager.INTERVAL_DAY, pendingIntent);

        } else {
            editor.putBoolean(courseName + "tgEndCourse", false);
            editor.apply();
            alarmManager.cancel(pendingIntent);
            setAlarmText("");
            Log.d("CourseInfo", "Course End Alarm Off");
        }
    }

    public void setAlarmText(String alarmText) {
        alarmTextView.setText(alarmText);
    }
}



