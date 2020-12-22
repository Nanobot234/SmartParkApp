package com.example.smartpark;


import android.content.SharedPreferences;
import android.location.Location;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Context;
import androidx.core.app.ComponentActivity;

import android.os.Build;
import android.util.Log;

import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;


/**
 * CLass to hold random locations generated near the spot hopefully that will be stored in dataBase,
 */
public class LocationFetch  {

    // CurrentLocationActivity firstActtivity;
    private LatLng currentLocal;
    private LatLng nearByLocation1Coord;
    private LatLng nearByLocation2Coord;
    SharedPreferences values;
    Context context;
    String userName;

    interface Listener {
        void onLoadCompleted();
    }

    public LocationFetch(final Listener listener) {


        context= CurrentLocationActivity.getContextofApp();
        values =context.getSharedPreferences("my Pref",0);
         userName = values.getString("Name",null);

         //The following code retrieves data from the Firebase database. in the on dataChnage method a snaprshot of the data
        //is retried based on the query
        FirebaseDatabase mData = FirebaseDatabase.getInstance();
        Query query = mData.getReference().child(userName);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Map clMap = (Map<String, Object>) snapshot.getValue();
                double lat = (double) clMap.get("latitude");
                double lon = (double) clMap.get("longitude");

                currentLocal = new LatLng(lat, lon);
                nearByLocation1Coord = getRandomLocation(currentLocal, 1000);
                nearByLocation2Coord = getRandomLocation(currentLocal, 1000);

                listener.onLoadCompleted();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


    }

    //returns random location in a given radius in meeters

    /**
     * THis function generates a random location given a Latn point, and a desired radius. This is used to simimulate the
     * location of a driver. In practice, I will have to get the nearby location of a driver from the database
     * @param point
     * @param radius The desired radius to generate points in, in meters
     * @return
     */
    public LatLng getRandomLocation(LatLng point, int radius) {

        double x0 = point.latitude;
        double y0 = point.longitude;

        Random random = new Random();

        // Convert radius from meters to degrees
        double radiusInDegrees = radius / 111000f;

        double u = random.nextDouble();
        double v = random.nextDouble();
        double w = radiusInDegrees * Math.sqrt(u);
        double t = 2 * Math.PI * v;
        double x = w * Math.cos(t);
        double y = w * Math.sin(t);

        // Adjust the x-coordinate for the shrinking of the east-west distances
        double new_x = x / Math.cos(Math.toRadians(y0));

        double foundLatitude = new_x + x0;
        double foundLongitude = y + y0;

        return new LatLng(foundLatitude, foundLongitude);
    }

    public LatLng getNearByLocation1Coord() {
        return nearByLocation1Coord;
    }



    public  LatLng getCurrentLocal() {return  currentLocal;}
}

