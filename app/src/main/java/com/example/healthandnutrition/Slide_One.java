package com.example.healthandnutrition;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.View;

public class Slide_One extends AppCompatActivity {
    MediaPlayer media;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_slide_one);
        media = MediaPlayer.create(this,R.raw.intro_1);
        media.start();
    }

    public void OpenSecondActivity(View view)
    {
        media.stop();
        Intent intent = new Intent(this, Slide_Two.class);
        startActivity(intent);
        overridePendingTransition(R.anim.slide_in_right,
                R.anim.slide_out_left);
        finish();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        media.stop();
        finishAffinity();
    }
}