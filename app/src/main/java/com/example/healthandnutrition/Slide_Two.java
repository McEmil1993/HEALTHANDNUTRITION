package com.example.healthandnutrition;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;

public class Slide_Two extends AppCompatActivity {
    MediaPlayer media;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_slide_two);
//        new Handler().postDelayed(new Runnable() {
//            @Override
//            public void run() {
//
//            }
//        },1000);
        media = MediaPlayer.create(Slide_Two.this,R.raw.intro_2);
        media.start();

    }

    public void Open3rdActivity(View view)
    {
        media.stop();
        Intent intent = new Intent(this, Slide_Three.class);
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
        Intent intent = new Intent(this, Slide_One.class);
        startActivity(intent);
        overridePendingTransition(R.anim.slide_in_left,
                R.anim.slide_out_right);
        finish();
    }
}