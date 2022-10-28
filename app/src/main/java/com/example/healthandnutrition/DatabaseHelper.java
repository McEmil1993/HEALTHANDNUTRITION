package com.example.healthandnutrition;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {
    public static final String DATABASE_NAME ="my_db";
    public static final String USERS = "users";
    public static final String  BP_RESULT = "bp_result";
    public static final String  RECOMMENDATIONS = "recommendations";
    public static final String  EXERCISES = "exercises";
    public static final String  LOGS = "logs";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, 1);
    }


    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + USERS + "(id INTEGER PRIMARY KEY AUTOINCREMENT ,  fullname TEXT," +
                "  age TEXT, status INTERGER)");

        db.execSQL("CREATE TABLE " + BP_RESULT + "(id INTEGER PRIMARY KEY AUTOINCREMENT ,user_id INTEGER," +
                " bp TEXT,name INTEGER, date_time TEXT, status INTERGER)");

        db.execSQL("CREATE TABLE " + RECOMMENDATIONS + "(id INTEGER PRIMARY KEY AUTOINCREMENT ,bp INTEGER, day TEXT, " +
                "description TEXT, date_time TEXT, status TEXT, bp_id INTEGER)");

        db.execSQL("CREATE TABLE " + EXERCISES + "(id INTEGER PRIMARY KEY AUTOINCREMENT ,bp INTEGER, name TEXT, " +
                "description TEXT, status TEXT, bp_id INTEGER)");

        db.execSQL("CREATE TABLE " + LOGS + "(id INTEGER PRIMARY KEY AUTOINCREMENT ," +
                "user_id TEXT, status INTERGER)");

//        insert_recommend(db);
//        insert_exercises(db);


    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS "+ USERS);
        db.execSQL("DROP TABLE IF EXISTS "+ BP_RESULT);
        db.execSQL("DROP TABLE IF EXISTS "+ RECOMMENDATIONS);
        db.execSQL("DROP TABLE IF EXISTS "+ LOGS);
        onCreate(db);
    }



    public void insert_ex_mild(SQLiteDatabase db,String bp_id){
        db.execSQL("INSERT INTO " + EXERCISES + "( bp , name , description , status, bp_id) " +
                "VALUES('2','basketball','30minutes (five days per week)','0','"+bp_id+"')");
        db.execSQL("INSERT INTO " + EXERCISES + "( bp , name , description , status, bp_id) " +
                "VALUES('2','tennis','30 minutes (five days per week)','0','"+bp_id+"')");
        db.execSQL("INSERT INTO " + EXERCISES + "( bp , name , description , status, bp_id) " +
                "VALUES('2','Bicycling','5 miles in 30 minutes','0','"+bp_id+"')");
        db.execSQL("INSERT INTO " + EXERCISES + "( bp , name , description , status, bp_id) " +
                "VALUES('2','Swimming','30 minutes (2.5 hours a week)','0','"+bp_id+"')");
        db.execSQL("INSERT INTO " + EXERCISES + "( bp , name , description , status, bp_id) " +
                "VALUES('2','Jumping rope','15 minutes (every day)','0','"+bp_id+"')");
        db.execSQL("INSERT INTO " + EXERCISES + "( bp , name , description , status, bp_id) " +
                "VALUES('2','Skating','30 minutes (five days per week)','0','"+bp_id+"')");
    }
    public void insert_ex_moderate(SQLiteDatabase db,String bp_id){
        db.execSQL("INSERT INTO " + EXERCISES + "( bp , name , description , status, bp_id) " +
                "VALUES('3','Treadmill','45 minutes','0','"+bp_id+"')");
        db.execSQL("INSERT INTO " + EXERCISES + "( bp , name , description , status, bp_id) " +
                "VALUES('3','Chair squat','1-minute','0','"+bp_id+"')");
        db.execSQL("INSERT INTO " + EXERCISES + "( bp , name , description , status, bp_id) " +
                "VALUES('3','Vertical bench press','1-minute','0','"+bp_id+"')");
        db.execSQL("INSERT INTO " + EXERCISES + "( bp , name , description , status, bp_id) " +
                "VALUES('3','Seated knee raise','1-minute','0','"+bp_id+"')");
        db.execSQL("INSERT INTO " + EXERCISES + "( bp , name , description , status, bp_id) " +
                "VALUES('3','Seated row','1-minute','0','"+bp_id+"')");
        db.execSQL("INSERT INTO " + EXERCISES + "( bp , name , description , status, bp_id) " +
                "VALUES('3','Dorsiflexion and plantar flexion','1-minute','0','"+bp_id+"')");
        db.execSQL("INSERT INTO " + EXERCISES + "( bp , name , description , status, bp_id) " +
                "VALUES('3','Shoulder abduction','1-minute','0','"+bp_id+"')");
    }
    public void insert_ex_severre(SQLiteDatabase db,String bp_id){
        db.execSQL("INSERT INTO " + EXERCISES + "( bp , name , description , status, bp_id) " +
                "VALUES('4','Walking','30 minutes (5 days a week)','0','"+bp_id+"')");
        db.execSQL("INSERT INTO " + EXERCISES + "( bp , name , description , status, bp_id) " +
                "VALUES('4','Gardening','150 minutes per week','0','"+bp_id+"')");
        db.execSQL("INSERT INTO " + EXERCISES + "( bp , name , description , status, bp_id) " +
                "VALUES('4',' Mowing the lawn and raking leaves','150 minutes per week','0','"+bp_id+"')");
        db.execSQL("INSERT INTO " + EXERCISES + "( bp , name , description , status, bp_id) " +
                "VALUES('4','Jogging','150 minutes per week','0','"+bp_id+"')");
        db.execSQL("INSERT INTO " + EXERCISES + "( bp , name , description , status, bp_id) " +
                "VALUES('4','Seated row','1-minute','0','"+bp_id+"')");
        db.execSQL("INSERT INTO " + EXERCISES + "( bp , name , description , status, bp_id) " +
                "VALUES('4','Climbing stairs','150 minutes per week','0','"+bp_id+"')");
        db.execSQL("INSERT INTO " + EXERCISES + "( bp , name , description , status, bp_id) " +
                "VALUES('4','Dancing','five days per week','0','"+bp_id+"')");
    }

}