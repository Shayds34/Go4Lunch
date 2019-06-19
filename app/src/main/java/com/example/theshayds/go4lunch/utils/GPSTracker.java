package com.example.theshayds.go4lunch.utils;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;

import static android.content.Context.LOCATION_SERVICE;

public class GPSTracker {
    private static final String TAG = "GPSTracker";
    private static volatile GPSTracker instance;

    private static final int MY_PERMISSION_REQUEST_CODE = 9001;

    private Context mContext;

    boolean isGPSEnabled = false;
    boolean isNetworkEnabled = false;
    public boolean canGetLocation = false;

    private Location mLastKnownLocation;
    private Location mLastLocation;
    private double mLatitude;
    private double mLongitude;

    private static int UPDATE_INTERVAL = 5000; // SEC
    private static int DISPLACEMENT = 20; // METERS

    private LocationManager locationManager;
    private FusedLocationProviderClient fusedLocationProviderClient;

    private GPSTracker(Context mContext) {
        if (instance != null) {
            throw new RuntimeException("Use getInstance() method to get the single instance of this class.");
        }
        this.mContext = mContext;
        getLocation();
    }

    public static GPSTracker getInstance(Context mContext) {
        if (instance == null) {
            instance = new GPSTracker(mContext);
        }
        return instance;
    }

    public Location getLocation() {
        Log.d(TAG, "getLocation: ");

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(mContext);

        // Check location permissions
        if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            //Run-time request permission
            ActivityCompat.requestPermissions((Activity) mContext, new String[]{
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
            }, MY_PERMISSION_REQUEST_CODE);
        } else {
            try {
                locationManager = (LocationManager) mContext.getSystemService(LOCATION_SERVICE);

                // Get GPS & Network Status
                isGPSEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
                isNetworkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

                // Custom Criteria
                final Criteria criteria = new Criteria();
                criteria.setAccuracy(Criteria.ACCURACY_FINE);
                criteria.setSpeedRequired(true);
                criteria.setAltitudeRequired(false);
                criteria.setBearingRequired(false);
                criteria.setCostAllowed(true);

                final String bestProvider = locationManager.getBestProvider(criteria, true);
                locationManager.requestLocationUpdates(bestProvider, UPDATE_INTERVAL, DISPLACEMENT, locationListener);

                if (!isGPSEnabled && !isNetworkEnabled) {
                    Toast.makeText(mContext, "Network and/or GPS not enabled.", Toast.LENGTH_SHORT).show();
                } else {
                    this.canGetLocation = true;
                    // First, get location from Network provider
                    if (isNetworkEnabled) {
                        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, UPDATE_INTERVAL, DISPLACEMENT, locationListener);
                        Log.d(TAG, "getLocation: network");
                        if (locationManager != null) {
                            mLastLocation = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                            if (mLastLocation != null) {
                                mLatitude = mLastLocation.getLatitude();
                                mLongitude = mLastLocation.getLongitude();
                            }
                        }
                    }
                    // If GPS is enabled, get lat/lng using GPS Services
                    if (isGPSEnabled) {
                        if (mLastLocation == null) {
                            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, UPDATE_INTERVAL, DISPLACEMENT, locationListener);
                            Log.d(TAG, "getLocation: gps");
                            if (locationManager != null) {
                                mLastLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                                if (mLastLocation != null) {
                                    mLatitude = mLastLocation.getLatitude();
                                    mLongitude = mLastLocation.getLongitude();
                                }
                            }
                        }
                    }
                }
            } catch (Exception e) {
                Log.d(TAG, "getLocation: " + e);
                e.printStackTrace();
            }
        }
        return mLastLocation;
    }

    public void stopUsingGPS(){
        if (locationManager != null){
            locationManager.removeUpdates(locationListener);
        }
    }

    public boolean canGetLocation(){
        return this.canGetLocation;
    }

    private LocationListener locationListener = new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {
            mLastLocation = location;

            Log.d(TAG, "onLocationChanged: latitude: " + location.getLatitude());
            Log.d(TAG, "onLocationChanged: longitude: " + location.getLongitude());
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {

        }

        @Override
        public void onProviderEnabled(String provider) {
            canGetLocation = true;
        }

        @Override
        public void onProviderDisabled(String provider) {
            canGetLocation = false;
        }
    };
}
