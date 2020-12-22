package com.example.smartpark;

import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.PreferenceManager;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;

public class Splash extends AppCompatActivity {


    //Will display it  here
    public final int Splash_length = 3000;
    SharedPreferences values;
    SharedPreferences prefs;
    Handler handler;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        boolean started = prefs.getBoolean("Started",false);
//        values = getSharedPreferences("my Pref", 0);
//         String nameVal = values.getString("Name", null);
        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
            @Override
            public void run() {

                if(started != false) {
                    Intent intent = new Intent(getApplicationContext(), CurrentLocationActivity.class);
                    startActivity(intent);
                    finish();
                } else{
                    Intent intent = new Intent(getApplicationContext(), NameandVehicleTypeActivity.class);
                    startActivity(intent);
                    finish();
                }
            }
        }, Splash_length);


    }


}