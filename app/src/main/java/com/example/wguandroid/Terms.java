package com.example.wguandroid;

import android.app.DatePickerDialog;
import android.app.LoaderManager;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CursorAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class Terms extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {


    public CursorAdapter cursorAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_terms);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        cursorAdapter = new CustomCursorAdapter(this, null , 0);

        ListView list = (ListView) findViewById(R.id.terms);
        list.setAdapter(cursorAdapter);

        getLoaderManager().initLoader(0, null, this);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LayoutInflater layoutInflater = LayoutInflater.from(Terms.this);
                View promptView = layoutInflater.inflate(R.layout.add_term_dialog, null);

                final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(Terms.this);

                alertDialogBuilder.setView(promptView);

                final EditText termName = (EditText) promptView.findViewById(R.id.termName);
                final Button startDate = (Button) promptView.findViewById(R.id.start_date);
                final Button endDate = (Button) promptView.findViewById(R.id.end_date);
                final Calendar c1 = Calendar.getInstance();
                final Calendar c2 = Calendar.getInstance();
                endDate.setEnabled(false);


                startDate.setOnClickListener(new View.OnClickListener() {
                                                 @Override
                                                 public void onClick(View v) {

                                                     Calendar cal = Calendar.getInstance();

                                                     DatePickerDialog dpd = new DatePickerDialog(Terms.this,
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

                                                   DatePickerDialog dpd = new DatePickerDialog(Terms.this,
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

                                setPositiveButton("Save Term", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int id) {
                                                String name = termName.getText().toString();

                                                if (name.isEmpty()) {
                                                    Toast.makeText(Terms.this, "All information needs to be filled out!", Toast.LENGTH_LONG).show();
                                                    return;
                                                } else {
                                                    insertTerm(name, c1, c2);
                                                    dialog.dismiss();
                                                    refreshAdapter();
                                                    Toast.makeText(Terms.this, " New Term added ! \n ", Toast.LENGTH_LONG).show();
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

    private void insertTerm(String termName, Calendar c1, Calendar c2) {
        SimpleDateFormat ft = new SimpleDateFormat("MM-dd-YYYY");
        ContentValues values = new ContentValues();
        values.put(DBOpenHelper.TERM_NAME, termName);
        values.put(DBOpenHelper.TERM_START, ft.format(c1.getTime()));
        values.put(DBOpenHelper.TERM_END, ft.format(c2.getTime()));
        Uri termsUri = getContentResolver().insert(TermsProvider.CONTENT_URI, values);

        Log.d("TermActivity", "Inserted term " + (termsUri != null ? termsUri.getLastPathSegment() : null));
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new CursorLoader(this, TermsProvider.CONTENT_URI,
                null, null, null, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        cursorAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        cursorAdapter.swapCursor(null);
    }

}
