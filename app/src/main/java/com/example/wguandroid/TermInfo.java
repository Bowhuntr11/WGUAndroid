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
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class TermInfo extends AppCompatActivity {

    private String termFilter;
    private String termName;
    private String startDate;
    private String endDate;
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
        startDate = intent.getStringExtra("startDate");
        endDate = intent.getStringExtra("endDate");

        TextView startDateView = (TextView) findViewById(R.id.start_date_term_info);
        startDateView.setText(startDate);
        TextView endDateView = (TextView) findViewById(R.id.end_date_term_info);
        endDateView.setText(endDate);

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
                getContentResolver().delete(
                        TermsProvider.CONTENT_URI, "_id = " + termFilter, null
                );
                finish();
                Toast.makeText(TermInfo.this,
                        getString(R.string.term_deleted),
                        Toast.LENGTH_SHORT).show();
            }
        });


    }
}
