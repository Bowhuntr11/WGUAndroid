package com.example.wguandroid;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class AssessmentInfo extends AppCompatActivity {

    private String assessmentFilter;
    private String assessmentName;
    private String goalDate;
    public static int ASSESSMENT_ID = 1001;

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

        TextView goalDateView = (TextView) findViewById(R.id.date_assessment_info);
        goalDateView.setText(goalDate);

        getSupportActionBar().setTitle(assessmentName);

        Button editAssessmentBtn = (Button) findViewById(R.id.editAssessmentBtn);
        editAssessmentBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Intent intent = new Intent(AssessmentInfo.this, TermCourses.class);
//                intent.putExtra("ID", termFilter);
//                intent.putExtra("termName", termName);
//                startActivityForResult(intent, TERM_ID);
            }
        });

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


    }
}
