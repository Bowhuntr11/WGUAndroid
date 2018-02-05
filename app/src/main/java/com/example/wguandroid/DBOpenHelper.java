package com.example.wguandroid;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;


public class DBOpenHelper extends SQLiteOpenHelper {


    private static final String LOG = "DBOpenHelper";

    //Constants for identifying table and columns
    public static final String TABLE_TERMS = "terms";
    public static final String TERM_ID = "_id";
    public static final String TERM_NAME = "termName";
    public static final String TERM_START = "termStart";
    public static final String TERM_END = "termEnd";
    public static final String TERM_CREATED = "termCreated";
    public static final String[] TERM_COLUMNS =
            {TERM_ID, TERM_NAME, TERM_START, TERM_END, TERM_CREATED};

    public static final String TABLE_COURSES = "courses";
    public static final String COURSE_ID = "_id";
    public static final String COURSE_NAME = "courseName";
    public static final String COURSE_START = "courseStart";
    public static final String COURSE_END = "courseEnd";
    public static final String COURSE_CREATED = "courseCreated";
    public static final String COURSE_MENTOR = "courseMentor";
    public static final String COURSE_STATUS = "courseStatus";
    public static final String MENTOR_NUMBER = "mentorNumber";
    public static final String MENTOR_EMAIL = "mentorEmail";
    public static final String TERM_KEY = "termKey";
    public static final String[] COURSE_COLUMNS =
            {COURSE_ID, COURSE_NAME, COURSE_START, COURSE_END, COURSE_CREATED, COURSE_MENTOR, COURSE_STATUS, MENTOR_NUMBER, MENTOR_EMAIL, TERM_KEY};

    public static final String TABLE_ASSESSMENTS = "assessments";
    public static final String ASSESSMENT_ID = "_id";
    public static final String ASSESSMENT_NAME = "assessmentName";
    public static final String ASSESSMENT_DATE = "assessmentDate";
    public static final String ASSESSMENT_CREATED = "assessmentCreated";
    public static final String COURSE_KEY = "courseKey";
    public static final String[] ASSESSMENT_COLUMNS =
            {ASSESSMENT_ID, ASSESSMENT_NAME, ASSESSMENT_DATE, ASSESSMENT_CREATED, COURSE_KEY};


    //Constants for db name and version
    private static final String DATABASE_NAME = "wguDatabase.db";
    private static final int DATABASE_VERSION = 3;

    //SQL to create table
    private static final String TERM_CREATE =
            "CREATE TABLE " + TABLE_TERMS + " (" +
                    TERM_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    TERM_NAME + " TEXT, " +
                    TERM_START + " TEXT, " +
                    TERM_END + " TEXT, " +
                    TERM_CREATED + " TEXT default CURRENT_TIMESTAMP" +
                    ")";

    private static final String COURSE_CREATE =
            "CREATE TABLE " + TABLE_COURSES + " (" +
                    COURSE_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    COURSE_NAME + " TEXT, " +
                    COURSE_START + " TEXT, " +
                    COURSE_END + " TEXT, " +
                    COURSE_STATUS + " TEXT, " +
                    COURSE_MENTOR + " TEXT, " +
                    MENTOR_NUMBER + " TEXT, " +
                    MENTOR_EMAIL + " TEXT, " +
                    TERM_KEY + " TEXT, " +
                    COURSE_CREATED + " TEXT default CURRENT_TIMESTAMP" +
                    ")";

    private static final String ASSESSMENT_CREATE =
            "CREATE TABLE " + TABLE_ASSESSMENTS + " (" +
                    ASSESSMENT_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    ASSESSMENT_NAME + " TEXT, " +
                    ASSESSMENT_DATE + " TEXT, " +
                    COURSE_KEY + " TEXT, " +
                    ASSESSMENT_CREATED + " TEXT default CURRENT_TIMESTAMP" +
                    ")";

    public DBOpenHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(TERM_CREATE);
        db.execSQL(COURSE_CREATE);
        db.execSQL(ASSESSMENT_CREATE);
        Log.d(LOG, "Database created");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_TERMS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_COURSES);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_ASSESSMENTS);
        onCreate(db);
        Log.d(LOG, "Database erased for Upgrade");
    }
}
