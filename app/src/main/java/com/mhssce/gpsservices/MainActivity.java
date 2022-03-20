package com.mhssce.gpsservices;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private Location locationNW;

    TextView txtLatitude, txtLongitude;
    Button btnFetch, btnViewMaps;
    ImageView AII;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        SharedPreferences sharedPreferences = getSharedPreferences("sharedPrefs", MODE_PRIVATE); //Saves Users Preferences wrt device
        final SharedPreferences.Editor editor = sharedPreferences.edit();
        final boolean isDarkModeOn = sharedPreferences.getBoolean("isDarkModeOn", false); //Boolean to store Dark Mode state

        txtLatitude=(TextView) findViewById(R.id.txtLatitude); //References Latitude TextView
        txtLongitude=(TextView) findViewById(R.id.txtLongitude); //References Longitude TextView

        btnViewMaps=(Button) findViewById(R.id.btnViewMaps); //References "View on Google Maps" Button
        btnFetch=(Button) findViewById(R.id.btnFetchLocation); //References "Fetch Last Known Location" Button
        btnFetch.setOnClickListener(v -> { //When "btnFetch" is clicked
            txtLatitude.setText(R.string.fetching); //Display "Fetching…" Message
            txtLongitude.setText(R.string.fetching); //Display "Fetching…" Message
            //Location Manager fetches Location Services
            LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
            //Variable to store Last Known Location using Network Provider
            locationNW = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
            if (locationNW != null) {
                txtLatitude.setText(String.valueOf(locationNW.getLatitude())); //Displaying Latitude of Location Fetched
                txtLongitude.setText(String.valueOf(locationNW.getLongitude())); //Displaying Longitude of Location Fetched
                btnViewMaps.setVisibility(View.VISIBLE); //Making "btnViewMaps" Visible
            }
            else{
                txtLatitude.setText(R.string.valueNotFound); //Display "Value Not Found" Message
                txtLongitude.setText(R.string.valueNotFound); //Display "Value Not Found" Message
            }
        });

        btnViewMaps.setOnClickListener(v -> { //When "btnViewMaps" is clicked
            //Creating Custom Google Maps URL using Latitude & Longitude
            String Maps = String.format(Locale.ENGLISH, "http://maps.google.com/maps?q=loc:%f,%f",locationNW.getLatitude(),locationNW.getLongitude());
            Intent mapInt = new Intent(Intent.ACTION_VIEW, Uri.parse(Maps)); //Intent to View Above URL
            startActivity(mapInt);
        });

        AII = (ImageView) findViewById(R.id.AII); //References AII Logo
        AII.setOnClickListener(v -> { //When "AII" is clicked
            if(isDarkModeOn){
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                editor.putBoolean("isDarkModeOn", false);
                editor.apply();
            }
            else{
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                editor.putBoolean("isDarkModeOn", true);
                editor.apply();
            }
        });

        if (isDarkModeOn)
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        else
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);

        //Check if Permission for Location is not Granted
        if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED)
            //Request Permission for Location
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        else
            btnFetch.setVisibility(View.VISIBLE);
    }

    //Function to Request Permission for Location
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1) {
            //If Permission is Granted
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(MainActivity.this, "Permission Granted", Toast.LENGTH_SHORT).show();
                    btnFetch.setVisibility(View.VISIBLE); //Making "btnFetch" Visible
                }
            } else {
                Toast.makeText(MainActivity.this, "Permission Denied", Toast.LENGTH_SHORT).show();
            }
        }
    }
}