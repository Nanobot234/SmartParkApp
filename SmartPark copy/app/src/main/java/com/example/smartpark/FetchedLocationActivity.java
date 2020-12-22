package com.example.smartpark;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class FetchedLocationActivity extends AppCompatActivity {
    /**
     * An instance of the location Fetch class
     */
    LocationFetch locationFetch;
    Location currentLocal;
    Location destinanationLocal;
    ProgressBar timeLeftBar;
    CountDownTimer countDownTimer;
    SharedPreferences vehicle;
    Button navButton;
    Button cancel;
    Geocoder geocoder;
    TextView timeText;
    List<Address> addresses;
    int progress = 0;
    int countdown = 10;

    //Handler handler = new Handler(Looper.getMainLooper());

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fetched_location);

        /**
         * When a new location object is instantiated, which includes fetching from the database, then the text is set.
         * This is asynchronous so as to prevent overload on the UI thread.
         */
        locationFetch = new LocationFetch(() -> {
            TextView LocalText = findViewById(R.id.fetchedLocationView);

            //getting the distance between current locationa and
             vehicle = getSharedPreferences("my Pref",0);
             String vehicleType = vehicle.getString("Vehicle Type","");
                Log.d("FectchLocalVal",vehicleType);

            currentLocal = new Location("");


            destinanationLocal = new Location("");



            currentLocal.setLatitude(locationFetch.getCurrentLocal().latitude);
            currentLocal.setLongitude(locationFetch.getCurrentLocal().longitude);

            destinanationLocal.setLongitude(locationFetch.getNearByLocation1Coord().longitude);
            destinanationLocal.setLatitude(locationFetch.getNearByLocation1Coord().latitude);

            double distance = currentLocal.distanceTo(destinanationLocal);
            double distanceinMiles =  distance/1609;  //The distance being converted to miles(approximate)


            String appendit = String.format("%s%n%n%s%.2f%s%n%n%s","Parking Space Found!", "Distance: " ,distanceinMiles," miles away","Vehicle type: " + vehicleType);
            LocalText.setText(appendit);
            //LocalText.append(vehicleType);

            timeLeftBar = (ProgressBar) findViewById(R.id.timeLeftProgress);
            timeText = (TextView) findViewById(R.id.timerText);
            timeLeftBar.setProgress(progress);

            countDownTimer = new CountDownTimer(10000,1000) {
                @Override
                public void onTick(long millisUntilFinished) {
                    progress++;
                    timeLeftBar.setProgress((int)progress*100/(10000/1000));
                    timeText.setText(String.format("%s%s",countdown,"s"));
                    countdown--;

                }

                @Override
                public void onFinish() {
                    progress++;
                    countdown = 0;
                    timeLeftBar.setProgress(100);
                    timeText.setText(String.format("%s%s",countdown,"s"));
                    Intent acitivity2intent = new Intent(getApplicationContext(),CurrentLocationActivity.class);
                    startActivity(acitivity2intent);
                    Toast.makeText(getApplicationContext(),"You took too long to accept this space",Toast.LENGTH_LONG).show();
                }
            };
                countDownTimer.start();

//            handler.postDelayed(() -> {
//                // put code in here that will run in 20 seconds when the option expires
//                onBackPressed();
//            }, 20_000); // run in 20 seconds
        });

        ActionBar actionBar = getSupportActionBar();

        actionBar.setDisplayHomeAsUpEnabled(true);

        cancel = findViewById(R.id.cancelButton);

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                countDownTimer.cancel();
                Intent intent = new Intent(getApplicationContext(),CurrentLocationActivity.class);
                startActivity(intent);
            }
        });


        navButton = findViewById(R.id.navigateButton);

        navButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadNavigationView("" + locationFetch.getNearByLocation1Coord().latitude,"" + locationFetch.getNearByLocation1Coord().longitude);
            }
        });




    }

    //stop the time when backbutton is pressed

    public boolean onOptionsItemSelected(MenuItem item){
        Intent myIntent = new Intent(getApplicationContext(), CurrentLocationActivity.class);
        startActivityForResult(myIntent, 0);
        return true;
    }


    public void onBackPressed(){
        if(countDownTimer != null) {
            countDownTimer.cancel();
        }
        super.onBackPressed();
    }

    /**
     * function to allow user to navigate to the parking space. THe function takes the the latitude and logitude of the target location
     * and passes it to Google Maps.
     * @param lat The desired latutude
     * @param lng the desired longitude
     */
    public void loadNavigationView(String lat,String lng){
        countDownTimer.cancel();
        Uri navigation = Uri.parse("google.navigation:q="+lat+","+lng+"");
        Intent navigationIntent = new Intent(Intent.ACTION_VIEW, navigation);
        navigationIntent.setPackage("com.google.android.apps.maps");
        startActivity(navigationIntent);
    }



}