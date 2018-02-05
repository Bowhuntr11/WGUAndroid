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
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class AssessmentInfo extends AppCompatActivity {

    private String assessmentFilter;
    private String assessmentName;
    private String goalDate;
    private String courseFilter;
    public static int ASSESSMENT_ID = 1001;

    private AlarmManager alarmManager;
    private PendingIntent pendingIntent;
    private static AssessmentInfo inst;
    private TextView alarmTextView;
    private Calendar calendar;

    public static AssessmentInfo instance() {
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
        setContentView(R.layout.activity_assessment_info);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Intent intent = getIntent();
        assessmentFilter = intent.getStringExtra("ID");
        assessmentName = intent.getStringExtra("assessmentName");
        goalDate = intent.getStringExtra("assessmentDate");
        courseFilter = intent.getStringExtra("courseFilter");

        final TextView goalDateView = (TextView) findViewById(R.id.date_assessment_info);
        goalDateView.setText(goalDate);

        getSupportActionBar().setTitle(assessmentName);
        alarmTextView = (TextView) findViewById(R.id.alarmText);

        ToggleButton tg = (ToggleButton) findViewById(R.id.alarmToggle);
        SharedPreferences shaPref = PreferenceManager.getDefaultSharedPreferences(this);
        boolean assessmentToggle = shaPref.getBoolean(assessmentName + "assessmentToggle", false);
        if (assessmentToggle) {
            tg.setChecked(true);
        } else {
            tg.setChecked(false);
        }

        Button deleteAssessmentBtn = (Button) findViewById(R.id.deleteAssessmentBtn);
        deleteAssessmentBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getContentResolver().delete(
                        AssessmentsProvider.CONTENT_URI, "_id = " + assessmentFilter, null
                );
                finish();
                Toast.makeText(AssessmentInfo.this,
                        getString(R.string.assessment_deleted),
                        Toast.LENGTH_SHORT).show();
            }
        });

        Button editAssessmentBtn = (Button) findViewById(R.id.editAssessmentBtn);
        editAssessmentBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LayoutInflater layoutInflater = LayoutInflater.from(AssessmentInfo.this);
                View promptView = layoutInflater.inflate(R.layout.edit_assessment_dialog, null);

                final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(AssessmentInfo.this);

                alertDialogBuilder.setView(promptView);
                final EditText assessmentNameEdit = (EditText) promptView.findViewById(R.id.edit_assessmentName);
                assessmentNameEdit.setText(assessmentName);
                final Button editGoalDate = (Button) promptView.findViewById(R.id.edit_goal_date);
                editGoalDate.setText(goalDate);
                final Calendar c1 = Calendar.getInstance();

                SimpleDateFormat sdf = new SimpleDateFormat("MM-dd-yyyy");
                Date date = null;
                try {
                    date = sdf.parse(goalDate);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                c1.setTime(date);


                editGoalDate.setOnClickListener(new View.OnClickListener() {
                                                    @Override
                                                    public void onClick(View v) {
                                                        DatePickerDialog dpd = new DatePickerDialog(AssessmentInfo.this,
                                                                new DatePickerDialog.OnDateSetListener() {

                                                                    @Override
                                                                    public void onDateSet(DatePicker view, int year,
                                                                                          int monthOfYear, int dayOfMonth) {
                                                                        editGoalDate.setText((monthOfYear + 1) + "/" + dayOfMonth + "/" + year);
                                                                        c1.set(year, monthOfYear, dayOfMonth);
                                                                    }
                                                                }, c1.get(Calendar.YEAR), c1.get(Calendar.MONTH), c1.get(Calendar.DATE));
                                                        dpd.setTitle("Assessment Goal Date");
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
                        String name = assessmentNameEdit.getText().toString();

                        if (name.isEmpty() || c1.get(Calendar.YEAR) <= 1900) {
                            Toast.makeText(AssessmentInfo.this, "You need to name the Assessment or give it a date!", Toast.LENGTH_LONG).show();
                        } else {
                            updateAssessment(name, c1);
                            alertD.dismiss();
                            finish();
                            Toast.makeText(AssessmentInfo.this, "Assessment Updated! ", Toast.LENGTH_LONG).show();
                        }
                    }
                });

            }
        });

    }

    private void updateAssessment(String assessmentName, Calendar c1) {
        SimpleDateFormat ft = new SimpleDateFormat("MM-dd-yyyy");
        ContentValues values = new ContentValues();
        values.put(DBOpenHelper.ASSESSMENT_NAME, assessmentName);
        values.put(DBOpenHelper.ASSESSMENT_DATE, ft.format(c1.getTime()));
        values.put(DBOpenHelper.COURSE_KEY, courseFilter);
        int termsUri = getContentResolver().update(AssessmentsProvider.CONTENT_URI, values, "_id = " + assessmentFilter, null);

        Log.d("AssessmentActivity", "Updated assessments: " + String.valueOf(termsUri));
    }

    public void onClicked(View view) {
        SharedPreferences shaPref = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = shaPref.edit();

        alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);

        if (((ToggleButton) view).isChecked()) {
            editor.putBoolean(assessmentName + "assessmentToggle", true);
            editor.apply();
            Log.d("AsssessmentInfo", "Alarm On");

            Intent myIntent = new Intent(AssessmentInfo.this, AssessmentAlarmReceiver.class);
            pendingIntent = PendingIntent.getBroadcast(getBaseContext(), 0, myIntent, PendingIntent.FLAG_UPDATE_CURRENT);

            calendar = Calendar.getInstance();

            SimpleDateFormat sdf = new SimpleDateFormat("MM-dd-yyyy");
            Date date = null;
            try {
                date = sdf.parse(goalDate);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            calendar.setTime(date);
            alarmManager.setInexactRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(),
                    AlarmManager.INTERVAL_DAY, pendingIntent);

        } else {
            editor.putBoolean(assessmentName + "assessmentToggle", false);
            editor.apply();
            alarmManager.cancel(pendingIntent);
            setAlarmText("");
            Log.d("AssessmentInfo", "Alarm Off");
        }
    }

    public void setAlarmText(String alarmText) {
        alarmTextView.setText(alarmText);
    }
}
