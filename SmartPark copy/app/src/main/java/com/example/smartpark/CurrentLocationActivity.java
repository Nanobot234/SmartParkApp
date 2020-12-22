package com.example.smartpark;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;
import android.location.Location;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;


import com.google.android.material.navigation.NavigationView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;

import java.util.HashMap;
import java.util.Map;

public class CurrentLocationActivity extends AppCompatActivity implements OnMapReadyCallback {


    private FirebaseDatabase mData;
    UserInfo userInfo;
    private DatabaseReference dataReference;
    Location currentLocation;
    SharedPreferences values;
    /**
     * The client that allows me to access certain location services such as getting current location
     */
    private FusedLocationProviderClient mFusedLocationClient;
    /**
     * The navigationd drawer object that the user interacts with, I set controls for this later
     */
    NavigationView navView;
    UserInfo usersInfo;
    String vehicleType;
    String nameVal;
    public static Context applicationContext;
    private static final int REQUEST_CODE = 101;
    private ActionBarDrawerToggle toggle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Here I am checking if the user has entered his name and vehicle type before. If not then they are directed to the
        //sign up page
        values = getSharedPreferences("my Pref", 0); //retrives the contents of the sharedPreferences file
        nameVal = values.getString("Name", null);
        if(nameVal != null) {
            setContentView(R.layout.activity_current_location);

            navView = findViewById(R.id.nav_view_home);
            applicationContext = getApplicationContext();
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);

            toggle = new ActionBarDrawerToggle(
                    this, findViewById(R.id.drawer_layout), R.string.app_name, R.string.app_name);


            mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
            fetchLastLocation();

            Button searchParkingButton = findViewById(R.id.lookingforParkingButton);

            /**
             * Listener to listen for button, clicks, when the user clicks they are moved to the respective activty
             */
            searchParkingButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    sendtoFireBase();
                    Intent acitivity2intent = new Intent(getApplicationContext(), FetchedLocationActivity.class);
                    startActivity(acitivity2intent);
                }
            });

            /**
             *  /**
             *          * Listener to listen for button, clicks, when the user clicks they are moved to the respective activty
             */

            Button LeavingSpaceButton = findViewById(R.id.leavingSpaceButton);

            LeavingSpaceButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent leavingIntent = new Intent(getApplicationContext(),LeavingLocationActivity.class);
                    startActivity(leavingIntent);
                }
            });

            /**
             * Listener for an item selection from the navigation drawer. When an item is selected, the user is sent to the repsective activities
             */
            navView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                    switch(item.getItemId()){
                        case R.id.action_settings:
                            Intent intent = new Intent(getApplicationContext(),SettingsActivity.class);
                            startActivity(intent);
                        case R.id.about:
                            Intent second = new Intent(getApplicationContext(),AboutActivity.class);
                            startActivity(second);
                    }
                    return false;
                }
            });



        } else {
            Intent intent = new Intent(getApplicationContext(),NameandVehicleTypeActivity.class);
            startActivity(intent);
        }




    }


    /**
     * This function returns the application context, I used it to access the shared Pref values in  a non activity class
     * @return
     */
    public static Context getContextofApp(){
        return applicationContext;
    }

    @Override
    public void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        toggle.syncState();
    }

    /**
     *
     * @param item The item that is specified by the menu object.
     * @return
     */
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home: 
                return toggle.onOptionsItemSelected(item);


        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * Thiis function asks the user for permission to access his location. Once the user agress, a call back object is triggred
     * stating that the map fragment can be used
     */
    private void fetchLastLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_CODE);
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        Task<Location> task = mFusedLocationClient.getLastLocation();
        task.addOnSuccessListener(new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                if (location != null) {
                    currentLocation = location;
                    SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
                    mapFragment.getMapAsync(CurrentLocationActivity.this::onMapReady);
                }
            }
        });
    }

    /**
     * Function to store username, and the coordinates of the users location in the Google Firebase Database. First, a reference to the datase
     * is created. Then I store the user information in a hashmap and then set it as a child of the username
     */
    public void sendtoFireBase() {

        userInfo = new UserInfo(nameVal, currentLocation.getLatitude(), currentLocation.getLongitude());
        //Will end up putting an instance of the class inside of the
        mData = FirebaseDatabase.getInstance();
        Map<String, Object> userVals = userInfo.toMap();

        mData.getReference().child(nameVal).setValue(userVals);
    }


    /**
     * Method that displays user location
     * @param googleMap THe googlemap obejct form the gooogle mpas SDK. I use this object to move the camera to the current userlocation
     *  and to set the marker
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {

        //MapsInitializer.initialize(getContext());

        LatLng latLng = new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude());
        MarkerOptions markerOptions = new MarkerOptions().position(latLng).title("I am here");

        // googleMap.animateCamera(CameraUpdateFactory.newLatLng(latLng));
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15));
        googleMap.addMarker(markerOptions);
    }


    /**
     * Method to determine if user has granted permission, if they have then the fetchlocation method is called
     * @param requestCode
     * @param permissions
     * @param grantResults
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case REQUEST_CODE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    fetchLastLocation();
                }
                break;
        }
    }

}