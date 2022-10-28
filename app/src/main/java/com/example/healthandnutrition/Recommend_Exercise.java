package com.example.healthandnutrition;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class Recommend_Exercise extends AppCompatActivity {
    DatabaseHelper dh;
    SQLiteDatabase db;
    LinearLayout parentLayout;
    String bp ="";
    String st1 = "";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recommend_exercise);

        dh = new DatabaseHelper(this);
        parentLayout = (LinearLayout) findViewById(R.id.parentLayout);
        Bundle bundle = getIntent().getExtras();
        if (bundle !=null) {
            try {

                bp = bundle.getString("bp");

            }  catch (Exception ex) {
                message("Error", ex.toString());
            }

        }
        display_all();
    }
    private void display_all(){
        try {
            parentLayout.removeAllViews();
            this.db = this.dh.getWritableDatabase();
            Cursor result = db.rawQuery("SELECT * FROM "+ this.dh.EXERCISES +" WHERE bp ='"+bp+"' ",null);

            if (result.getCount() <= 0) {
                this.message("No Records", "No records found!");

            } else {
                while (result.moveToNext()) {


                    final Recommend_Exercise.Holder holder = new Recommend_Exercise.Holder();

                    final View view = LayoutInflater.from(getApplicationContext()).inflate(R.layout.row_recomm, null);
                    holder.t_day = view.findViewById(R.id.t_day);
                    holder.t_btn_down = view.findViewById(R.id.t_btn_down);
                    holder.t_btn_up = view.findViewById(R.id.t_btn_up);
                    holder.t_description = view.findViewById(R.id.t_description);
                    holder.r_details = view.findViewById(R.id.r_details);
                    holder.ck_day = view.findViewById(R.id.ck_day);
                    String _id_ = result.getString(0);
                    holder.t_day.setText(result.getString(2));
                    holder.t_description.setText(result.getString(3));

                    holder.t_btn_down.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            holder.t_btn_up.setVisibility(View.VISIBLE);
                            holder.t_btn_down.setVisibility(View.GONE);
                            holder.r_details.setVisibility(View.VISIBLE);
                        }
                    });
                    holder.t_btn_up.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            holder.t_btn_up.setVisibility(View.GONE);
                            holder.t_btn_down.setVisibility(View.VISIBLE);
                            holder.r_details.setVisibility(View.GONE);
                        }
                    });


                    if (result.getString(4).equals("1")){
                        holder.ck_day.setChecked(true);
                        st1 = "0";
                    }else{
                        holder.ck_day.setChecked(false);
                        st1 = "1";
                    }

                    holder.ck_day.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {

                            String st = "";
                            if(holder.ck_day.isChecked())
                            {
                                st = "1";
                            }
                            else
                            {
                                st = "0";
                            }
                            String finalSt = st;
                            try {
                                Recommend_Exercise.this.db = Recommend_Exercise.this.dh.getWritableDatabase();
                                Recommend_Exercise.this.db.execSQL(" UPDATE " + Recommend_Exercise.this.dh.EXERCISES + " SET status = ? WHERE id = ?", new String[]{finalSt,_id_});
                            }catch (SQLiteException e){
                                message("SQL Error", e.toString());
                            }catch (Exception e){
                                message("Error", e.toString());
                            }

                        }
                    });
                    parentLayout.addView(view);
                }

            }
        }catch (SQLiteException e){
            message("SQL Error", e.toString());
        }
        catch (Exception e){
            message("Error", e.toString());
        }
    }

    private class Holder {
        TextView t_day,t_btn_down,t_btn_up,t_description;
        RelativeLayout r_details;
        CheckBox ck_day;

    }

    public void message(String title, String message){
        final AlertDialog.Builder builder= new AlertDialog.Builder(Recommend_Exercise.this);
        final View customLayout = LayoutInflater.from(Recommend_Exercise.this).inflate(R.layout.alert_layout, null);
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
        Intent intent = new Intent(this, Dashboard.class);
        startActivity(intent);
        overridePendingTransition(R.anim.slide_in_left,
                R.anim.slide_out_right);
        finishAffinity();
    }
    public void onBack_R(View view){
        Intent intent = new Intent(this, Dashboard.class);
        startActivity(intent);
        overridePendingTransition(R.anim.slide_in_left,
                R.anim.slide_out_right);
        finishAffinity();
    }
}