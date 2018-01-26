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
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CursorAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class TermCourses extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    private String termFilter;
    private String termName;
    public CursorAdapter cursorAdapter;
    private ArrayAdapter<CharSequence> spinnerAdapter;
    private static final int COURSE_ID = 1001;
    String value;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_term_info);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Intent intent = getIntent();
        termFilter = intent.getStringExtra("ID");
        termName = intent.getStringExtra("termName");

        getSupportActionBar().setTitle(termName);

        cursorAdapter = new CustomCursorAdapter(this, null, 0);
        ListView list = (ListView) findViewById(R.id.courses);
        list.setAdapter(cursorAdapter);

        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Uri.parse(TermsProvider.CONTENT_URI + "/" + id);
                Cursor row = (Cursor) parent.getItemAtPosition(position);
                String _id = row.getString(row.getColumnIndexOrThrow("_id"));
                String courseName = row.getString(row.getColumnIndex("courseName"));
                String courseStart = row.getString(row.getColumnIndex("courseStart"));
                String courseEnd = row.getString(row.getColumnIndex("courseEnd"));
                String courseMentor = row.getString(row.getColumnIndex("courseMentor"));
                String mentorNumber = row.getString(row.getColumnIndex("mentorNumber"));
                String mentorEmail = row.getString(row.getColumnIndex("mentorEmail"));
                String courseProgress = row.getString(row.getColumnIndex("courseStatus"));
                Intent intent = new Intent(TermCourses.this, CourseInfo.class);
                intent.putExtra("ID", _id);
                intent.putExtra("courseName", courseName);
                intent.putExtra("courseStart", courseStart);
                intent.putExtra("courseEnd", courseEnd);
                intent.putExtra("courseMentor", courseMentor);
                intent.putExtra("mentorNumber", mentorNumber);
                intent.putExtra("mentorEmail", mentorEmail);
                intent.putExtra("courseProgress", courseProgress);
                startActivityForResult(intent, COURSE_ID);
            }
        });

        getLoaderManager().initLoader(0, null, this);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LayoutInflater layoutInflater = LayoutInflater.from(TermCourses.this);
                View promptView = layoutInflater.inflate(R.layout.add_course_dialog, null);

                final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(TermCourses.this);

                alertDialogBuilder.setView(promptView);

                final EditText courseName = (EditText) promptView.findViewById(R.id.courseName);
                final Button startDate = (Button) promptView.findViewById(R.id.start_date);
                final Button endDate = (Button) promptView.findViewById(R.id.end_date);
                final Calendar c1 = Calendar.getInstance();
                c1.set(1900, 1, 1);
                final Calendar c2 = Calendar.getInstance();
                c2.set(1900, 1, 1);
                final Calendar c3 = Calendar.getInstance();
                final EditText mentorName = (EditText) promptView.findViewById(R.id.mentorName);
                final EditText mentorNumber = (EditText) promptView.findViewById(R.id.mentorNumber);
                final EditText mentorEmail = (EditText) promptView.findViewById(R.id.mentorEmail);

                endDate.setEnabled(false);


                startDate.setOnClickListener(new View.OnClickListener() {
                                                 @Override
                                                 public void onClick(View v) {
                                                     DatePickerDialog dpd = new DatePickerDialog(TermCourses.this,
                                                             new DatePickerDialog.OnDateSetListener() {

                                                                 @Override
                                                                 public void onDateSet(DatePicker view, int year,
                                                                                       int monthOfYear, int dayOfMonth) {
                                                                     startDate.setText((monthOfYear + 1) + "/" + dayOfMonth + "/" + year);
                                                                     endDate.setEnabled(true);
                                                                     c1.set(year, monthOfYear, dayOfMonth);
                                                                 }
                                                             }, c3.get(Calendar.YEAR), c3.get(Calendar.MONTH), c3.get(Calendar.DATE));
                                                     dpd.setTitle("Start of Term Date");
                                                     dpd.show();
                                                 }

                                             }
                );

                endDate.setOnClickListener(new View.OnClickListener() {
                                               @Override
                                               public void onClick(View v) {
                                                   DatePickerDialog dpd = new DatePickerDialog(TermCourses.this,
                                                           new DatePickerDialog.OnDateSetListener() {

                                                               @Override
                                                               public void onDateSet(DatePicker view, int year,
                                                                                     int monthOfYear, int dayOfMonth) {
                                                                   endDate.setText((monthOfYear + 1) + "/" + dayOfMonth + "/" + year);
                                                                   c2.set(year, monthOfYear, dayOfMonth);
                                                               }
                                                           }, c3.get(Calendar.YEAR), c3.get(Calendar.MONTH), c3.get(Calendar.DATE));
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

                // Override of onClick so that when user selects Save button it checks to make sure fields are filled out
                alertD.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener()
                {
                    @Override
                    public void onClick(View v) {
                    String cName = courseName.getText().toString();
                    String cProgress = value;
                    String mName = mentorName.getText().toString();
                    String mNumber = mentorNumber.getText().toString();
                    String mEmail = mentorEmail.getText().toString();
                    Log.d("TermInfo Dialog", String.valueOf(c1.get(Calendar.YEAR)));
                    Log.d("TermInfo Dialog", String.valueOf(c2.get(Calendar.YEAR)));

                        if (cName.isEmpty() || cProgress.isEmpty() || mName.isEmpty() || mNumber.isEmpty() || mEmail.isEmpty()
                                                        || c1.get(Calendar.YEAR) <= 1900 || c2.get(Calendar.YEAR) <= 1900) {
                            Toast.makeText(TermCourses.this, "You didn't fill out all the information, or the date is too far in the past", Toast.LENGTH_LONG).show();
                        } else {
                            insertCourse(cName, cProgress, c1, c2, mName, mNumber, mEmail);
                            alertD.dismiss();
                            refreshAdapter();
                            Toast.makeText(TermCourses.this, " New Course added ! ", Toast.LENGTH_LONG).show();
                        }
                    }
                });


                // Applying adapter to Spinner on Course Dialog
                final Spinner spinner = (Spinner) alertD.findViewById(R.id.progressSpinner);
                spinnerAdapter = ArrayAdapter.createFromResource(TermCourses.this,
                        R.array.progress_array, android.R.layout.simple_spinner_item);
                spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinner.setAdapter(spinnerAdapter);


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

    private void refreshAdapter() {
        getLoaderManager().restartLoader(0, null, this);
    }

    private void insertCourse(String courseName, String cProgress, Calendar c1, Calendar c2, String mName, String mNumber, String mEmail) {
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
        Uri termsUri = getContentResolver().insert(CoursesProvider.CONTENT_URI, values);

        Log.d("CourseActivity", "Inserted course " + (termsUri != null ? termsUri.getLastPathSegment() : null));
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
        cursorAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        cursorAdapter.swapCursor(null);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_courses, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_delete) {
            DialogInterface.OnClickListener dialogClickListener =
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int button) {
                            if (button == DialogInterface.BUTTON_POSITIVE) {
                                //Insert Data management code here
                                getContentResolver().delete(
                                        TermsProvider.CONTENT_URI, "_id = " + termFilter, null
                                );
                                finish();
                                Toast.makeText(TermCourses.this,
                                        getString(R.string.term_deleted),
                                        Toast.LENGTH_SHORT).show();
                            }
                        }
                    };

            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage(getString(R.string.are_you_sure_term))
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
