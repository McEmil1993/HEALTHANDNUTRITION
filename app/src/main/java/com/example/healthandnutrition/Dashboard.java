package com.example.healthandnutrition;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class Dashboard extends AppCompatActivity {

    DatabaseHelper dh;
    SQLiteDatabase db;
    Button btn_newbp;
    private String id_;
    String bp = "";

    LinearLayout bp_list;
    TextView user_fullname,user_age;
    TextToSpeech textS;
    Button play;
    Button btn_chart;
    SharedPreferences sharedpreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);
        dh = new DatabaseHelper(this);
        sharedpreferences = getSharedPreferences(Result.SESSION, Context.MODE_PRIVATE);

        user_fullname = findViewById(R.id.user_fullname);
        user_age = findViewById(R.id.user_age);
        btn_newbp = findViewById(R.id.btn_newbp);
        bp_list = (LinearLayout) findViewById(R.id.bp_list);
        play = findViewById(R.id.play);
        btn_chart = findViewById(R.id.btn_chart);
        btn_chart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ImageView image = new ImageView(Dashboard.this);
                image.setImageResource(R.drawable.chart);
                final AlertDialog.Builder builder= new AlertDialog.Builder(Dashboard.this);
                final View customLayout = LayoutInflater.from(Dashboard.this).inflate(R.layout.sw_dialog, null);
                builder.setView(customLayout);

                final AlertDialog dialog= builder.create();
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
                dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
                dialog.setCancelable(false);
                dialog.show();
                final  ImageView view_image = customLayout.findViewById(R.id.view_image);
                final TextView txtClose = customLayout.findViewById(R.id.txtClose);
                txtClose.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });
                view_image.setClipToOutline(true);


            }
        });


//        MediaPlayer media = MediaPlayer.create(this,R.raw.voice_1);
//        play.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
////                media.start();
//                startActivity(new Intent(Dashboard.this,Slide_One.class));
//            }
//        });
        user_fullname.setText(sharedpreferences.getString("fullname",""));
        user_age.setText(sharedpreferences.getString("age","")+ " years old");
        setId_(sharedpreferences.getString("user_id",""));

        display_all();

    }

    public void click_new(View view){

        this.db = this.dh.getWritableDatabase();


        Cursor cursor_2 = db.rawQuery("SELECT * FROM " + this.dh.BP_RESULT +" WHERE user_id = '"+getId_()+"' AND status = 0 ",null);
        if(cursor_2.getCount() >= 1 ){

            message("Warning","Before entering a new BP, I will let you know that it is best to review all the nutritional advice so that we can determine whether your blood pressure has improved.");

        }else{

            final AlertDialog.Builder builder= new AlertDialog.Builder(Dashboard.this);
            final View customLayout = LayoutInflater.from(Dashboard.this).inflate(R.layout.activity_new_bp, null);
            final TextView txtClose = customLayout.findViewById(R.id.txtClose);
            final TextView new_submit = customLayout.findViewById(R.id.new_submit);
            final EditText new_input_bp = customLayout.findViewById(R.id.new_input_bp);
            new_input_bp.setText(""+cursor_2.getCount());


            builder.setView(customLayout);
            final AlertDialog dialog= builder.create();
            txtClose.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    dialog.dismiss();
                }
            });


            new_submit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    try {
                        String bp = new_input_bp.getText().toString();

                        String regexpattern = "(\\d+)(/(\\d+))?";
                        Pattern pattern = Pattern.compile(regexpattern);
                        Matcher matcher = pattern.matcher(bp);
                        if (matcher.find()){
                            if (matcher.group(3) != null){

                                int syastolic_result = Integer.valueOf(matcher.group(1));
                                int diastolic_result = Integer.valueOf(matcher.group(3));

                                if(syastolic_result < 129 && diastolic_result < 79){

//                            datas(fullname,age,bp,"1");
                                    new_insert_data(bp,"1");
                                    dialog.dismiss();


                                }else if (syastolic_result >= 130 && diastolic_result >= 80 && syastolic_result <= 139 && diastolic_result <= 89){

//                            datas(fullname,age,bp,"2");
                                    new_insert_data(bp,"2");
                                    dialog.dismiss();

                                }else if (syastolic_result >= 140 && diastolic_result >= 90 && syastolic_result <= 179 && diastolic_result <= 119){

//                            datas(fullname,age,bp,"3");
                                    new_insert_data(bp,"3");
                                    dialog.dismiss();

                                }else if (syastolic_result >= 180 && diastolic_result >= 120){

//                            datas(fullname,age,bp,"4");
                                    new_insert_data(bp,"4");
                                    dialog.dismiss();

                                }else{
                                    message("Error", "Please check input bp!.");
                                }
                                display_all();

                            }

                        }
                    }catch (Exception e){
                        message("Error",e.toString());
                    }



                }
            });
            dialog.setCancelable(false);
            dialog.show();

        }

    }
    public void new_insert_data(String bp,String result){
        Date today = new Date();
        SimpleDateFormat formatter = new SimpleDateFormat("MMMM dd, yyyy");
        Dashboard.this.db = Dashboard.this.dh.getWritableDatabase();

        Dashboard.this.db.execSQL("INSERT INTO " + Dashboard.this.dh.BP_RESULT + "(user_id , bp , name, date_time, status) " +
                "VALUES(?,?,?,?,?)", new String[]{getId_(), bp, result,formatter.format(today),"0"});

    }

    public void display_all(){

        try {
            check_logs();
            Date today = new Date();
            SimpleDateFormat formatter = new SimpleDateFormat("MMMM dd, yyyy");
            SimpleDateFormat sdf = new SimpleDateFormat("MMMM dd, yyyy", Locale.ENGLISH);
            bp_list.removeAllViews();
            this.db = this.dh.getWritableDatabase();

            Cursor cursor_2 = db.rawQuery("SELECT * FROM " + this.dh.BP_RESULT +" WHERE user_id = '"+getId_()+"' ",null);
            while(cursor_2.moveToNext()){

                final Dashboard.Holder holder = new Dashboard.Holder();
                final View view = LayoutInflater.from(getApplicationContext()).inflate(R.layout.row_bp, null);
                holder.click_cardview = view.findViewById(R.id.click_cardview);
                holder.txt_bpresult = view.findViewById(R.id.txt_bpresult);
                holder.txt_bpdate = view.findViewById(R.id.txt_bpdate);
                holder.li_ = view.findViewById(R.id.li_);
                String get_id = cursor_2.getString(0);
                String n_bp = cursor_2.getString(2);
                String regexpattern = "(\\d+)(/(\\d+))?";
                Pattern pattern = Pattern.compile(regexpattern);
                Matcher matcher = pattern.matcher(n_bp);
                if (matcher.find()) {
                    if (matcher.group(3) != null) {

                        int syastolic_result = Integer.valueOf(matcher.group(1));
                        int diastolic_result = Integer.valueOf(matcher.group(3));

                        if(syastolic_result < 129 && diastolic_result < 79){
                            holder.txt_bpresult.setTextColor(Color.parseColor("#439E47"));
                            holder.txt_bpresult.setText("♥ "+cursor_2.getString(2)+ " mmHg");
                        }else{
                            holder.txt_bpresult.setTextColor(Color.parseColor("#BC1106"));
                            holder.txt_bpresult.setText("♥ "+cursor_2.getString(2)+ " mmHg " + cursor_2.getString(5));
                        }

                    }
                }


                bp = cursor_2.getString(3);

                String new_bp = bp;

                Date firstDate = sdf.parse(cursor_2.getString(4));
                Date secondDate = sdf.parse(formatter.format(today));

                long diff = secondDate.getTime() - firstDate.getTime();

                TimeUnit time = TimeUnit.DAYS;
                long diffrence = time.convert(diff, TimeUnit.MILLISECONDS);


                String tody = "";
                if (diffrence == 0){
                    tody = "Today ";
                }else if (diffrence == 1){
                    tody =""+diffrence + " day ago";
                }else {
                    tody =""+diffrence + " days ago";
                }
                holder.txt_bpdate.setText(tody);

                holder.click_cardview.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View view) {

                        if(new_bp.equals("1")){
                            message("Warning","PLease");
                        }else{
                            android.app.AlertDialog.Builder builder = new android.app
                                    .AlertDialog.Builder(Dashboard.this,R.style.AlertDialog);
                            String[] name = {"Suggestting Nutrition Diet" , "Recommended Exercises"};
                            builder.setItems(name, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    try {
                                        switch (which){
                                            case 0:
                                                Intent i = new Intent(getApplicationContext(), Sugguestion.class);
                                                Bundle bundle = new Bundle();
                                                bundle.putString("bp", new_bp);
                                                bundle.putString("get_id",get_id);
                                                i.putExtras(bundle);
                                                startActivity(i);

                                                break;
                                            case 1:
                                                Intent i1 = new Intent(getApplicationContext(), Recommend_Exercise.class);
                                                Bundle bundle1 = new Bundle();
                                                bundle1.putString("bp", new_bp);
                                                bundle1.putString("get_id",get_id);
                                                i1.putExtras(bundle1);
                                                startActivity(i1);

                                                break;
                                        }

                                    }catch (Exception e){

                                    }

                                }
                            });
                            builder.show();
                        }

                        return false;
                    }
                });

                bp_list.addView(view);
            }

        }catch (SQLiteException e){
            message("SQL Error", e.toString());
        }
        catch (Exception e){
            message("Error", e.toString());
        }
    }


    private void check_logs(){
        try {
            if (getId_().equals("1")){
                Toast.makeText(getApplicationContext(),"Welcome back "+user_fullname.getText().toString(),Toast.LENGTH_LONG);
            }else{
                Intent i = new Intent(this, Slide_One.class);
                startActivity(i);
            }

        }catch (Exception e){
            message("Error",e.toString());
        }

    }



    private class Holder {
        CardView click_cardview;
        LinearLayout li_;
        TextView txt_bpresult,txt_bpdate;

    }
    public void message(String title, String message){
        final AlertDialog.Builder builder= new AlertDialog.Builder(Dashboard.this);
        final View customLayout = LayoutInflater.from(Dashboard.this).inflate(R.layout.alert_layout, null);
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
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);
        startActivity(intent);
        finish();
    }
    public void setId_(String id_){
        this.id_ = id_;
    }

    public String getId_(){
        return id_;
    }



}