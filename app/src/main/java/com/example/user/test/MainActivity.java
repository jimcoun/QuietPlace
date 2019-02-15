package com.example.user.test;

import android.Manifest;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.openlocationcode.OpenLocationCode;

import static android.Manifest.permission.ACCESS_FINE_LOCATION;

public class MainActivity extends AppCompatActivity {

    public static final String EXTRA_MESSAGE = "com.example.user.test.MESSAGE";
    protected static final int REQUEST_CHECK_SETTINGS = 0x1;

    private FusedLocationProviderClient mFusedLocationClient; // Declare location client
    private LocationCallback mLocationCallback;
    private Location location;
    private LocationRequest mLocationRequest;
    private boolean mRequestingLocationUpdates;

    private double latitude;
    private double longitude;
    private double accuracy;
    private String plusCode;
    private String plusCodeClean;

    private TextView statusText;
    private TextView latitudeText;
    private TextView longitudeText;
    private TextView accuracyText;
    private TextView plusCodeText;
    private ToggleButton toggleButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        statusText = findViewById(R.id.statusText);
        latitudeText = findViewById(R.id.latitudeText);
        longitudeText = findViewById(R.id.longitudeText);
        accuracyText = findViewById(R.id.accuracyText);
        plusCodeText = findViewById(R.id.plusCodeText); // Find the plus code element

        toggleButton = findViewById(R.id.toggleButton);

        statusText.setText("App started.");

        getLastLocation(); // Retrieves the device last known location
        checkLocationSettings(); // Checks that GPS is on


        toggleButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked){
                    statusText.setText("Location updates started");
                    getLiveLocation();
                }
                else{
                    stopLocationUpdates();
                    statusText.setText("Location updates stopped");
                }
            }
        });

    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mRequestingLocationUpdates) {
            stopLocationUpdates();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
//        if (mRequestingLocationUpdates) {
//            startLocationUpdates();
//        }
    }



    // Gets location and shows longitude, latitude
    private void getLastLocation() {

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this); // Create the location client

        if (ActivityCompat.checkSelfPermission(this, ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            requestPermission();
        }
        else {
            mFusedLocationClient.getLastLocation()
                    .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                        @Override
                        public void onSuccess(Location location) {
                            // Got last known location. In some rare situations this can be null.
                            if (location != null) {

                                printLocation(location);

                            }
                        }
                    });
        }

    }

    private void getLiveLocation(){


        mLocationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                if (locationResult == null) {
                    return;
                }

                mRequestingLocationUpdates = true;
                location = locationResult.getLastLocation();

                printLocation(location);

           }
        };

        if (ActivityCompat.checkSelfPermission(this, ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            requestPermission();

            return;
        }
        mFusedLocationClient.requestLocationUpdates(mLocationRequest,
                mLocationCallback,
                null /* Looper */);
    }



    // Sets the location settings and checks device
    private void checkLocationSettings() {

        // Set the app's location requirements
        mLocationRequest = LocationRequest.create();
        mLocationRequest.setInterval(5000);
        mLocationRequest.setFastestInterval(4000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        // Make request
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                .addLocationRequest(mLocationRequest);

        SettingsClient client = LocationServices.getSettingsClient(this);
        Task<LocationSettingsResponse> task = client.checkLocationSettings(builder.build());

        task.addOnSuccessListener(this, new OnSuccessListener<LocationSettingsResponse>() {
            @Override
            public void onSuccess(LocationSettingsResponse locationSettingsResponse) {
            }
        });

        task.addOnFailureListener(this, new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                if (e instanceof ResolvableApiException) {
                    // Location settings are not satisfied, but this can be fixed
                    // by showing the user a dialog.
                    try {
                        // Show the dialog by calling startResolutionForResult(),
                        // and check the result in onActivityResult().
                        ResolvableApiException resolvable = (ResolvableApiException) e;
                        resolvable.startResolutionForResult(MainActivity.this,
                                REQUEST_CHECK_SETTINGS);
                    } catch (IntentSender.SendIntentException sendEx) {
                        // Ignore the error.
                    }
                }
            }
        });
    }



    private void requestPermission(){
        ActivityCompat.requestPermissions(this, new String[]{ACCESS_FINE_LOCATION}, 1);

    }

    private void stopLocationUpdates() {
        mFusedLocationClient.removeLocationUpdates(mLocationCallback);
    }

    // Checks if user has granted location access permission
    boolean hasLocationPermission(){
        return ActivityCompat.checkSelfPermission(this, ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED;
    }

    // Converts to plus code and prints all location data
    private void printLocation(Location location){
        latitude = location.getLatitude();
        longitude = location.getLongitude();
        accuracy = location.getAccuracy();

        latitudeText.setText(String.valueOf(latitude));
        longitudeText.setText(String.valueOf(longitude));
        accuracyText.setText(String.format("%.2f m", accuracy));


        // Convert location to Plus Code
        OpenLocationCode code = new OpenLocationCode(latitude, longitude); // Create object

        plusCode = code.getCode(); // Extract plus code from the object

        // Check the validity of the code
        if(OpenLocationCode.isValidCode(plusCode)) {
            plusCodeText.setText(plusCode); // Display plus code
            // Delete "+" sign
            StringBuilder sb = new StringBuilder(plusCode);
            sb.deleteCharAt(8);
            plusCodeClean = sb.toString();
        }
        else{
            plusCodeText.setText("Cannot convert!"); // Display error
            plusCodeClean = "0000000000";
        }

    }

    public void startProofRequest(View view){
        Intent intent = new Intent (this, RequestProof.class);
        Bundle extras = new Bundle();
        extras.putString("EXTRA_PLUS_CODE", plusCodeClean);
        intent.putExtras(extras);
        startActivity(intent);

    }

}
