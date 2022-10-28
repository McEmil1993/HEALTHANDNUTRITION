package com.example.healthandnutrition;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import cn.pedant.SweetAlert.SweetAlertDialog;
import android.graphics.Color;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MainActivity extends AppCompatActivity {
    private TextView btn_submit;
    private EditText txt_input_bp,txt_fullname, txt_age;
    private ImageView iv_back;
    DatabaseHelper dh;
    SQLiteDatabase db;
    AlertDialog.Builder builder;
    MediaPlayer media;
    SharedPreferences sharedpreferences;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        dh = new DatabaseHelper(this);

        sharedpreferences = getSharedPreferences(Result.SESSION, Context.MODE_PRIVATE);

        txt_fullname = findViewById(R.id.txt_fullname);
        txt_age = findViewById(R.id.txt_age);
        txt_input_bp = findViewById(R.id.txt_input_bp);
        btn_submit = findViewById(R.id.btn_submit);
        iv_back = findViewById(R.id.iv_back);
        check_logs();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                media = MediaPlayer.create(MainActivity.this,R.raw.intro_5);
                media.start();
            }
        },1500);


//        media.start();
//        btn_submit.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
////                startActivity(new Intent(getApplicationContext(),Result.class));
//                message("Success","Salamat");
//            }
//        });

    }
    public void click_submit(View view){
        String fullname = txt_fullname.getText().toString();
        String age = txt_age.getText().toString();
        String bp = txt_input_bp.getText().toString();

        String regexpattern = "(\\d+)(/(\\d+))?";
        Pattern pattern = Pattern.compile(regexpattern);
        Matcher matcher = pattern.matcher(bp);
        if (matcher.find()){
            if (matcher.group(3) != null){

                int syastolic_result = Integer.valueOf(matcher.group(1));
                int diastolic_result = Integer.valueOf(matcher.group(3));

                if(syastolic_result < 129 && diastolic_result < 79){

                    datas(fullname,age,bp,"1");

                }else if (syastolic_result >= 130 && diastolic_result >= 80 && syastolic_result <= 139 && diastolic_result <= 89){

                    datas(fullname,age,bp,"2");

                }else if (syastolic_result >= 140 && diastolic_result >= 90 && syastolic_result <= 179 && diastolic_result <= 119){

                    datas(fullname,age,bp,"3");

                }else if (syastolic_result >= 180 && diastolic_result >= 120){

                    datas(fullname,age,bp,"4");

                }else{
                    message("Error", "Please check input bp!.");
                }


            }

        }

    }

    private void datas(String fullname ,String age, String bp, String result){
        media.stop();
        Intent i = new Intent(this, Result.class);
        Bundle bundle = new Bundle();
        bundle.putString("fullname", fullname);
        bundle.putString("age", age);
        bundle.putString("bp", bp);
        bundle.putString("result", result);
        i.putExtras(bundle);
        startActivity(i);
        finish();

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
                    finish();
                }
            }
        }catch (Exception e){
            message("Error",e.toString());
        }

    }
    public void message(String title, String message){
        final AlertDialog.Builder builder= new AlertDialog.Builder(MainActivity.this);
        final View customLayout = LayoutInflater.from(MainActivity.this).inflate(R.layout.alert_layout, null);
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

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        media.stop();
        Intent intent = new Intent(this, Slide_Three.class);
        startActivity(intent);
        overridePendingTransition(R.anim.slide_in_left,
                R.anim.slide_out_right);
        finishAffinity();
    }
    public void onBack(View view){
        media.stop();
        Intent intent = new Intent(this, Slide_Three.class);
        startActivity(intent);
        overridePendingTransition(R.anim.slide_in_left,
                R.anim.slide_out_right);
        finishAffinity();
    }

    @Override
    protected void onResume() {
        super.onResume();

        txt_fullname.setText(sharedpreferences.getString("txt_fullname",""));
        txt_age.setText(sharedpreferences.getString("txt_age",""));
        txt_input_bp.setText(sharedpreferences.getString("txt_bp",""));


    }

    @Override
    protected void onPause() {
        super.onPause();

        SharedPreferences.Editor editor = sharedpreferences.edit();

        editor.putString("txt_fullname", txt_fullname.getText().toString());
        editor.putString("txt_age", txt_age.getText().toString());
        editor.putString("txt_bp", txt_input_bp.getText().toString());
        editor.apply();
    }
}