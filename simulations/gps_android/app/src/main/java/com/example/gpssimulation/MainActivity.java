package com.example.gpssimulation;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;

import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    public static final int DEFAULT_UPDATE_INTERVAL = 30;
    public static final int FAST_UPDATE_INTERVAL = 1;
    public static final int PERMISSIONS_FINE_LOCATION = 99;
    // GPS UI
    TextView tv_lat,tv_lon,tv_altitude,tv_accuracy,tv_speed,tv_sensor,tv_updates,tv_address;
    Switch sw_locationsupdates,sw_gps;

    // Location request config file for all settings related to FusedLocationProviderClient
    LocationRequest locationRequest;

    // Google's API for location services
    FusedLocationProviderClient fusedLocationProviderClient;

    // location callback function
    LocationCallback locationCallback;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        // GPS UI
        tv_lat=findViewById(R.id.tv_lat);
        tv_lon=findViewById(R.id.tv_lon);
        tv_altitude=findViewById(R.id.tv_altitude);
        tv_accuracy=findViewById(R.id.tv_accuracy);
        tv_speed=findViewById(R.id.tv_speed);
        tv_sensor=findViewById(R.id.tv_sensor);
        tv_updates=findViewById(R.id.tv_updates);
        tv_address=findViewById(R.id.tv_address);
        sw_locationsupdates=findViewById(R.id.sw_locationsupdates);
        sw_gps=findViewById(R.id.sw_gps);

        // set all properties of LocationRequest
        locationRequest= new LocationRequest();

        // how often does default location check occur?
        locationRequest.setInterval(1000* DEFAULT_UPDATE_INTERVAL);

        // how often does location check occur when set to the most frequent update?
        locationRequest.setFastestInterval(1000* FAST_UPDATE_INTERVAL);

        sw_gps.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v){
                if(sw_gps.isChecked()){
                    // most accurate - use GPS
                    locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
                    tv_sensor.setText("Using GPS sensors");
                }
                else {
                    locationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
                    tv_sensor.setText("Using Towers + GPS");
                }
            }
        });

        sw_locationsupdates.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(sw_locationsupdates.isChecked()){
                    // turn on tracking
                    startLocationUpdate();
                } else{
                    // turn off tracking
                    stopLocationUpdates();
                }
            }
        });

        updateGPS();

        // triggered whenever update interval is met
        locationCallback = new LocationCallback(){

            @Override
            public void onLocationResult(LocationResult locationResult) {
                super.onLocationResult(locationResult);
                // Update the location
                updateUIValues(locationResult.getLastLocation());
            }
        };

    }

    private void stopLocationUpdates() {
        tv_updates.setText("Location is being tracked.");
        fusedLocationProviderClient.requestLocationUpdates(locationRequest,locationCallback,null);
        updateGPS();
    }

    private void startLocationUpdate() {
        tv_updates.setText("Location is NOT being tracked.");
        tv_lat.setText("Location is NOT being tracked.");
        tv_lon.setText("Location is NOT being tracked.");
        tv_speed.setText("Location is NOT being tracked.");
        tv_accuracy.setText("Location is NOT being tracked.");
        tv_address.setText("Location is NOT being tracked.");
        tv_sensor.setText("Location is NOT being tracked.");

        fusedLocationProviderClient.removeLocationUpdates(locationCallback);

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode){
            case PERMISSIONS_FINE_LOCATION:
                if (grantResults[0]==PackageManager.PERMISSION_GRANTED){
                    updateGPS();
                }
                else{
                    Toast.makeText(this,"This app requires permissions to be granted in order to work properly",Toast.LENGTH_SHORT).show();
                    finish();
                }
        }
    }

    private void updateGPS(){
        // get permissions from user to track GPS
        // get current location from fused client
        // update UI - i.e set all properties in their associated text view

        fusedLocationProviderClient= LocationServices.getFusedLocationProviderClient(MainActivity.this);

        if(ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)== PackageManager.PERMISSION_GRANTED){
            // permission provided by user
            fusedLocationProviderClient.getLastLocation().addOnSuccessListener(this, new OnSuccessListener<Location>() {
                @Override
                public void onSuccess(Location location) {
                    // we got permission. Put the values of location. XXX into the UI components
                    updateUIValues(location);
                }
            });
        } else{
            // permissions not granted
             if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.M){
                 requestPermissions(new String[] {Manifest.permission.ACCESS_FINE_LOCATION},PERMISSIONS_FINE_LOCATION);
             }
             else {
                 Toast.makeText(this,"Build version too low",Toast.LENGTH_SHORT).show();
             }
        }


    }

    private void updateUIValues(Location location) {
        // update all of the UI
        tv_lat.setText(String.valueOf(location.getLatitude()));
        tv_lon.setText(String.valueOf(location.getLongitude()));
        tv_accuracy.setText(String.valueOf(location.getAccuracy()));
        if (location.hasAltitude()) tv_altitude.setText(String.valueOf(location.getAltitude()));
        else tv_altitude.setText("Not available.");
        if (location.hasSpeed()) tv_speed.setText(String.valueOf(location.getSpeed()));
        else tv_speed.setText("Not available.");

        Geocoder geocoder = new Geocoder(MainActivity.this);
        try {
            List<Address> addresses= geocoder.getFromLocation(location.getLatitude(),location.getLongitude(),1);
            tv_address.setText(addresses.get(0).getAddressLine(0));
        } catch (Exception e){
            tv_address.setText("Unable to get address.");
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
