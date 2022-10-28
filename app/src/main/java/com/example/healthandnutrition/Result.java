package com.example.healthandnutrition;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class Result extends AppCompatActivity {
    private TextView btn_proceed,txt_bp,txt_result,txt_result_description,btn_back;
    AlertDialog.Builder builder;
    String fullname = "";
    String age = "";
    String bp = "";
    String result = "";
    String id_ = "";
    DatabaseHelper dh;
    SQLiteDatabase db;
    public static final String SESSION = "session" ;
    SharedPreferences sharedpreferences;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);
        dh = new DatabaseHelper(this);
        txt_bp = findViewById(R.id.txt_bp);
        txt_result = findViewById(R.id.txt_result);
        txt_result_description = findViewById(R.id.txt_result_description);

        btn_proceed = findViewById(R.id.btn_proceed);
        btn_back = findViewById(R.id.btn_back);

        sharedpreferences = getSharedPreferences(SESSION, Context.MODE_PRIVATE);

        check_logs();
        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(Result.this, MainActivity.class));
                finish();
            }
        });
        btn_proceed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (result.equals("1")){
                    message("Warning","You cannot continue because your bp is normal.\n" +
                            "Please Pressed Back Button to Enter New BP.");
                }else{
                    new SweetAlertDialog(Result.this, SweetAlertDialog.WARNING_TYPE)
                            .setTitleText("Are you sure?")
                            .setContentText("You won't be able to continue!")
                            .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                @Override
                                public void onClick(SweetAlertDialog sDialog) {

                                    insertDatas();

                                    Intent i = new Intent(getApplicationContext(), Dashboard.class);
                                    Bundle bundle = new Bundle();
                                    bundle.putString("id_", id_);
                                    i.putExtras(bundle);
                                    startActivity(i);
                                    finish();
                                }
                            })
                            .setCancelButton("Cancel", new SweetAlertDialog.OnSweetClickListener() {
                                @Override
                                public void onClick(SweetAlertDialog sDialog) {
                                    sDialog.dismissWithAnimation();
                                }
                            })
                            .show();
                }

            }
        });
        getData();
    }

    public void getData(){
        Bundle bundle = getIntent().getExtras();
        if (bundle !=null) {
            try {
                 fullname = bundle.getString("fullname");
                 age = bundle.getString("age");
                 bp = bundle.getString("bp");
                 result = bundle.getString("result");

                if (result.equals("1")){
                    txt_result.setText("Normal");
                    txt_bp.setText(bp+" mmHg");
                    txt_result_description.setText("Hi! "+fullname+", You appear to have a normal blood pressure, and hypertension is not a danger for you (high blood pressure).");
                }else if (result.equals("2")){
                    txt_result.setText("Mild");
                    txt_bp.setText(bp+" mmHg");
                    txt_result_description.setText("Hi! "+fullname+",Your blood pressure value is over the recommended levels and is thus deemed to be long-term harmful.");
                }else if (result.equals("3")){
                    txt_result.setText("Moderate");
                    txt_bp.setText(bp+" mmHg");
                    txt_result_description.setText("Hi! "+fullname+",Your blood pressure result is rather high, and medication must be used to manage it.");
                }else if (result.equals("4")){
                    txt_result.setText("Severre");
                    txt_bp.setText(bp+" mmHg");
                    txt_result_description.setText("Hi! "+fullname+",Your blood pressure measurement is dangerously high, and you should visit your doctor right now.");
                }

            }  catch (Exception ex) {
                message("Error", ex.toString());
            }

        }
    }
    private void insertDatas(){

        try{
            Date today = new Date();
            SimpleDateFormat formatter = new SimpleDateFormat("MMMM dd, yyyy");

            Result.this.db = Result.this.dh.getWritableDatabase();
            Result.this.db.execSQL("INSERT INTO " + Result.this.dh.USERS + "(fullname , age , status) " +
                    "VALUES(?,?,?)", new String[]{fullname, age, "1"});

            Cursor cursor = Result.this.db.rawQuery("SELECT * FROM "+ Result.this.dh.USERS +" ",null);
            if (cursor.moveToNext()) {

                Result.this.db.execSQL("INSERT INTO " + Result.this.dh.BP_RESULT + "(user_id , bp , name, date_time, status) " +
                        "VALUES(?,?,?,?,?)", new String[]{cursor.getString(0), bp, result,formatter.format(today),"0"});

                Result.this.db.execSQL("INSERT INTO " + Result.this.dh.LOGS + "(user_id , status) " +
                        "VALUES(?,?)", new String[]{cursor.getString(0), "1"});

                id_ = cursor.getString(0);

                SharedPreferences.Editor editor = sharedpreferences.edit();

                editor.putString("fullname", fullname);
                editor.putString("age", age);
                editor.putString("user_id", id_);
                editor.putString("bp_result",result);
                editor.commit();
            }
        }catch (SQLiteException e){
            message("SQL Error",e.toString());
        }catch (Exception e){
            message("Error",e.toString());
        }



    }

    private void check_logs(){
        try {
            this.db = this.dh.getWritableDatabase();
            Cursor result = db.rawQuery("SELECT * FROM "+ this.dh.LOGS +" ",null);
            if (result.getCount() <= 0) {

            } else {
                if (result.moveToNext()){
                    Intent i = new Intent(this, Dashboard.class);
                    Bundle bundle = new Bundle();
                    bundle.putString("id_", result.getString(1));
                    i.putExtras(bundle);
                    startActivity(i);
                }
            }
        }catch (Exception e){
            message("Error",e.toString());
        }

    }

    public void message(String title, String message){
        final AlertDialog.Builder builder= new AlertDialog.Builder(Result.this);
        final View customLayout = LayoutInflater.from(Result.this).inflate(R.layout.alert_layout, null);
        builder.setView(customLayout);
        final TextView txt_title = customLayout.findViewById(R.id.txt_title);
        final TextView txt_message = customLayout.findViewById(R.id.txt_message);
        final TextView txt_close = customLayout.findViewById(R.id.txt_close);
        txt_title.setText(title);
        txt_message.setText(message);

        final AlertDialog dialog= builder.create();

        txt_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
        dialog.setCancelable(false);
        dialog.show();
    }
}