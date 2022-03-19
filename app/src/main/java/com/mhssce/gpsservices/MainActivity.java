package com.mhssce.gpsservices;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
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
    ImageView Sign;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        SharedPreferences sharedPreferences = getSharedPreferences("sharedPrefs", MODE_PRIVATE);
        final SharedPreferences.Editor editor = sharedPreferences.edit();
        final boolean isDarkModeOn = sharedPreferences.getBoolean("isDarkModeOn", false);

        txtLatitude=(TextView) findViewById(R.id.txtLatitude);
        txtLongitude=(TextView) findViewById(R.id.txtLongitude);

        btnViewMaps=(Button) findViewById(R.id.btnViewMaps);
        btnFetch=(Button) findViewById(R.id.btnFetchLocation);
        btnFetch.setOnClickListener(v -> {
            txtLatitude.setText(R.string.waiting);
            txtLongitude.setText(R.string.waiting);
            LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
            locationNW = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
            if (locationNW != null) {
                txtLatitude.setText(String.valueOf(locationNW.getLatitude()));
                txtLongitude.setText(String.valueOf(locationNW.getLongitude()));
                btnViewMaps.setVisibility(View.VISIBLE);
            }
            else{
                txtLatitude.setText(R.string.valueNotFound);
                txtLongitude.setText(R.string.valueNotFound);
            }
        });

        btnViewMaps.setOnClickListener(v -> {
            String Maps = String.format(Locale.ENGLISH, "http://maps.google.com/maps?q=loc:%f,%f",locationNW.getLatitude(),locationNW.getLongitude());
            Intent mapInt = new Intent(Intent.ACTION_VIEW, Uri.parse(Maps));
            startActivity(mapInt);
        });

        Sign = (ImageView) findViewById(R.id.Sign);
        Sign.setOnClickListener(v -> {
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

        if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED)
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        else
            btnFetch.setVisibility(View.VISIBLE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(MainActivity.this, "Permission Granted", Toast.LENGTH_SHORT).show();
                    btnFetch.setVisibility(View.VISIBLE);
                }
            } else {
                Toast.makeText(MainActivity.this, "Permission Denied", Toast.LENGTH_SHORT).show();
            }
        }
    }
}