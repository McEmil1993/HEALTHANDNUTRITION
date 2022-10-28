package com.example.healthandnutrition;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;

public class Slide_Three extends AppCompatActivity {
    MediaPlayer media;
    SharedPreferences sharedpreferences;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_slide_three);
        sharedpreferences = getSharedPreferences(Result.SESSION, Context.MODE_PRIVATE);

//        new Handler().postDelayed(new Runnable() {
//            @Override
//            public void run() {
//
//            }
//        },1000);
        media = MediaPlayer.create(Slide_Three.this,R.raw.intro_3);
        media.start();

        SharedPreferences.Editor editor = sharedpreferences.edit();
        editor.clear();
        editor.commit();
    }

    public void OpenMain(View view)
    {
        media.stop();
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        overridePendingTransition(R.anim.slide_in_right,
                R.anim.slide_out_left);
        finish();
    }

    // when the user pressed back button this function
    // get invoked automatically.
    @Override
    public void onBackPressed()
    {
        super.onBackPressed();
        media.stop();
        Intent intent = new Intent(this, Slide_Two.class);
        startActivity(intent);
        overridePendingTransition(R.anim.slide_in_left,
                R.anim.slide_out_right);
        finish();
    }
}