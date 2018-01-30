package com.example.wguandroid;

import android.app.DatePickerDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
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

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class TermInfo extends AppCompatActivity {

    private String termFilter;
    private String termName;
    private String startDateTerm;
    private String endDateTerm;
    public static int TERM_ID = 1001;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_term_info);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Intent intent = getIntent();
        termFilter = intent.getStringExtra("ID");
        termName = intent.getStringExtra("termName");
        startDateTerm = intent.getStringExtra("startDate");
        endDateTerm = intent.getStringExtra("endDate");

        TextView startDateView = (TextView) findViewById(R.id.start_date_term_info);
        startDateView.setText(startDateTerm);
        TextView endDateView = (TextView) findViewById(R.id.end_date_term_info);
        endDateView.setText(endDateTerm);

        getSupportActionBar().setTitle(termName);

        Button coursesBtn = (Button) findViewById(R.id.coursesBtn);
        coursesBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(TermInfo.this, TermCourses.class);
                intent.putExtra("ID", termFilter);
                intent.putExtra("termName", termName);
                startActivityForResult(intent, TERM_ID);
            }
        });

        Button deleteTermBtn = (Button) findViewById(R.id.deleteTermBtn);
        deleteTermBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Cursor row = getContentResolver().query(CoursesProvider.CONTENT_URI, null, "termKey = " + termFilter, null, null);
                Log.d("DeleteTerm", String.valueOf(row.getCount()));
                if (row.getCount() == 0) {
                    getContentResolver().delete(
                            TermsProvider.CONTENT_URI, "_id = " + termFilter, null);
                    finish();
                    Toast.makeText(TermInfo.this,
                            getString(R.string.term_deleted),
                            Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(TermInfo.this,
                            getString(R.string.cant_delete_term),
                            Toast.LENGTH_SHORT).show();
                }
            }
        });

        Button editTermBtn = (Button) findViewById(R.id.editTermBtn);
        editTermBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LayoutInflater layoutInflater = LayoutInflater.from(TermInfo.this);
                View promptView = layoutInflater.inflate(R.layout.edit_term_dialog, null);

                final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(TermInfo.this);

                alertDialogBuilder.setView(promptView);
                final EditText termNameEdit = (EditText) promptView.findViewById(R.id.editTermName);
                termNameEdit.setText(termName);
                final Button startDate = (Button) promptView.findViewById(R.id.edit_start_date);
                startDate.setText(startDateTerm);
                final Button endDate = (Button) promptView.findViewById(R.id.edit_end_date);
                endDate.setText(endDateTerm);
                final Calendar c1 = Calendar.getInstance();
                final Calendar c2 = Calendar.getInstance();

                SimpleDateFormat sdf = new SimpleDateFormat("MM-dd-yyyy");
                Date date = null;
                Date date2 = null;
                try {
                    date = sdf.parse(startDateTerm);
                    date2 = sdf.parse(endDateTerm);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                c1.setTime(date);
                c2.setTime(date2);

                startDate.setOnClickListener(new View.OnClickListener() {
                                                 @Override
                                                 public void onClick(View v) {
                                                     DatePickerDialog dpd = new DatePickerDialog(TermInfo.this,
                                                             new DatePickerDialog.OnDateSetListener() {

                                                                 @Override
                                                                 public void onDateSet(DatePicker view, int year,
                                                                                       int monthOfYear, int dayOfMonth) {
                                                                     startDate.setText((monthOfYear + 1) + "/" + dayOfMonth + "/" + year);
                                                                     c1.set(year, monthOfYear, dayOfMonth);
                                                                 }
                                                             }, c1.get(Calendar.YEAR), c1.get(Calendar.MONTH), c1.get(Calendar.DATE));
                                                     dpd.setTitle("Start of Term Date");
                                                     dpd.show();
                                                 }

                                             }
                );

                endDate.setOnClickListener(new View.OnClickListener() {
                                               @Override
                                               public void onClick(View v) {
                                                   DatePickerDialog dpd = new DatePickerDialog(TermInfo.this,
                                                           new DatePickerDialog.OnDateSetListener() {

                                                               @Override
                                                               public void onDateSet(DatePicker view, int year,
                                                                                     int monthOfYear, int dayOfMonth) {
                                                                   endDate.setText((monthOfYear + 1) + "/" + dayOfMonth + "/" + year);
                                                                   c2.set(year, monthOfYear, dayOfMonth);
                                                               }
                                                           }, c2.get(Calendar.YEAR), c2.get(Calendar.MONTH), c2.get(Calendar.DATE));
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

                                setPositiveButton("Save Term", new DialogInterface.OnClickListener() {
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
                        String name = termNameEdit.getText().toString();

                        if (name.isEmpty() || c1.get(Calendar.YEAR) <= 1900 || c2.get(Calendar.YEAR) <= 1900) {
                            Toast.makeText(TermInfo.this, "You need to name the Term or give it dates!", Toast.LENGTH_LONG).show();
                        } else {
                            editTerm(name, c1, c2);
                            alertD.dismiss();
                            finish();
                            Toast.makeText(TermInfo.this, "Term Updated! ", Toast.LENGTH_LONG).show();
                        }
                    }
                });
            }
        });


    }

    private void editTerm(String name, Calendar c1, Calendar c2) {
        SimpleDateFormat ft = new SimpleDateFormat("MM-dd-yyyy");
        ContentValues values = new ContentValues();
        values.put(DBOpenHelper.TERM_NAME, name);
        values.put(DBOpenHelper.TERM_START, ft.format(c1.getTime()));
        values.put(DBOpenHelper.TERM_END, ft.format(c2.getTime()));
        int termsUpdated = getContentResolver().update(TermsProvider.CONTENT_URI, values, "_id = " + termFilter, null);

        Log.d("TermInfo", "Updated terms " + String.valueOf(termsUpdated));
    }
}
