package com.example.smartpark;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.sql.Driver;
import java.util.Random;
import java.util.Timer;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Activity for drivers who want to alert othe  users to come to their parking space. This is a sampke activty with
 * hardocded values. THis is due to the fact that I dont have users on the platform as of yer.
 */
public class LeavingLocationActivity extends AppCompatActivity {

    TextView LeavingView;
    CountDownTimer timer;
    ProgressBar searchingBar;
    Button continueButton;
    TextView progress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_leaving_location);

        LeavingView = findViewById(R.id.DriverFound);
        continueButton = findViewById(R.id.continueButton);
        searchingBar = findViewById(R.id.Searchingbar);
        progress = findViewById(R.id.LocatingProgress);

        ActionBar actionBar = getSupportActionBar();

        actionBar.setDisplayHomeAsUpEnabled(true);
        Random rand = new Random();
        int max = 10;
        int min = 2;
        int result = rand.nextInt(max-min) + min;

        /**
         * Visibiity animations
         */
        timer = new CountDownTimer(2000,500) {
            @Override
            public void onTick(long millisUntilFinished) {
                LeavingView.setVisibility(View.GONE);
                continueButton.setVisibility(View.INVISIBLE);

            }

            @Override
            public void onFinish() {
                LeavingView.setVisibility(View.VISIBLE);
                continueButton.setVisibility(View.VISIBLE);
                searchingBar.setVisibility(View.GONE);
                progress.setVisibility(View.GONE);
            }
        };
        timer.start();

        continueButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(),CurrentLocationActivity.class);
                startActivity(intent);
            }
        });
        //In practice, I would have to get these values from the databse
        String driverName = "Jack";
        String lisencePlate = "H45RTQ";
        String textString = String.format("%s%n%n%s%n%n%d%s"," Driver Found! ",driverName + " with liscense plate " + lisencePlate + " is ",result," minutes away");

        LeavingView.setText(textString);


    }

    public boolean onOptionsItemSelected(MenuItem item){
        Intent myIntent = new Intent(getApplicationContext(), CurrentLocationActivity.class);
        startActivityForResult(myIntent, 0);
        return true;
    }

    public void onBackPressed(){
        if(timer != null){
            timer.cancel();
            Intent leavingIntent = new Intent(getApplicationContext(),CurrentLocationActivity.class);
            startActivity(leavingIntent);
        }
    }


}