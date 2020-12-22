package com.example.smartpark;

import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.PreferenceManager;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.Toast;

/**
 * Class for the first screen asking for name and vehicleType
 */
public class NameandVehicleTypeActivity extends AppCompatActivity implements OnItemSelectedListener {

    Spinner vehicleSpinner;
    SharedPreferences userPrefs;

    Button startNextActivity;
    EditText name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nameand_vehicle_type);

        vehicleSpinner = findViewById(R.id.vehicleSpinner);
        name = findViewById(R.id.nametext);

        userPrefs = getApplicationContext().getSharedPreferences("my Pref",0);

        vehicleSpinner.setOnItemSelectedListener(this);
        List<String> vehicleTypes = new ArrayList<String>();
        vehicleTypes.add(0,"Select Your Car Type");
        vehicleTypes.add("Sedan");
        vehicleTypes.add("Truck");
        vehicleTypes.add("Minivan");
        vehicleTypes.add("SUV");


        ArrayAdapter<String> dataAdapt = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1,vehicleTypes);


        vehicleSpinner.setAdapter(dataAdapt);

        startNextActivity = findViewById(R.id.startLocationButton);

        startNextActivity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences.Editor editor;
                editor = userPrefs.edit();

                editor.putString("Name", String.valueOf(name.getText()));
                editor.commit();
                 SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                SharedPreferences.Editor edit = prefs.edit();
                edit.putBoolean("Started", Boolean.TRUE);
                edit.commit();
              confirmInput();
            }
        });

    }


    /**
     * Function that reads which vehicle type is chosen from the spinner
     * @param parent
     * @param view
     * @param position
     * @param id
     */
    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        SharedPreferences.Editor editor;
        editor = userPrefs.edit();
        //Maybe have an if statement like if the string is select car type, then just put 0
            editor.putString("Vehicle Type",parent.getItemAtPosition(position).toString());
            editor.commit();

    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    /**
     *Function to check if the user entermed his name and selected a vehicle
     */
    public void confirmInput(){
        String name = userPrefs.getString("Name",null);
        String vehicleChoice = userPrefs.getString("Vehicle Type",null);

        if(name != null && vehicleChoice != "Select Your Car Type"){
            Intent intent = new Intent(getApplicationContext(), CurrentLocationActivity.class);
            startActivity(intent);
        } else {
            Toast.makeText(getApplicationContext(),"Please enter your name or select a car type",Toast.LENGTH_LONG).show();
        }
    }
}