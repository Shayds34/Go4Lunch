package com.example.theshayds.go4lunch.fragments;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.example.theshayds.go4lunch.R;
import com.example.theshayds.go4lunch.controller.PlaceActivity;
import com.example.theshayds.go4lunch.pojo.MyPlace;
import com.example.theshayds.go4lunch.pojo.PlaceDetail;
import com.example.theshayds.go4lunch.utils.ApiStreams;
import com.example.theshayds.go4lunch.utils.CoworkerHelper;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Objects;

import io.reactivex.disposables.Disposable;
import io.reactivex.observers.DisposableObserver;


public class MyMapFragment2 extends Fragment implements OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks, LocationListener, GoogleApiClient.OnConnectionFailedListener {
    public static final String TAG = "MyMapFragment2";

    private static volatile MyMapFragment2 instance;

    // The request code must be 0 or greater.
    private static final int PLUS_ONE_REQUEST_CODE = 123;

    private Context mContext;
    private View view;
    private MapView mapView;
    private GoogleMap mMap;
    private Marker myMarker;

    private ProgressBar progressBar;

    //region {Location Purpose}
    public static final int MY_LOCATION_PERMISSION_REQUEST_CODE = 9001;

    private double mLatitude;
    private double mLongitude;

    private Location mLastLocation;

    private static final long UPDATE_FASTEST_INTERVAL = 1000 * 5; // 5 SEC
    private static final int UPDATE_INTERVAL = 1000 * 10;  // 10 SEC
    private static final int UPDATE_DISPLACEMENT = 20; // METERS

    LocationManager locationManager;
    FusedLocationProviderClient fusedLocationProviderClient;
    //endregion

    private GoogleApiClient mGoogleApiClient;
    private LocationRequest mLocationRequest;

    public ArrayList<MyPlace> getMyPlaceArrayList() {
        return mMyPlaceArrayList;
    }

    private ArrayList<MyPlace> mMyPlaceArrayList;

    private Disposable disposable;

    public MyMapFragment2() {
        // Required empty public constructor
    }

    public static MyMapFragment2 getInstance() {
        if (instance == null){
            instance = new MyMapFragment2();
        }
        return instance;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_my_map_fragment2, container, false);
        mContext = view.getContext();
        mapView = view.findViewById(R.id.map_view_test);

        progressBar = view.findViewById(R.id.progress_circular);

        // Checking location permission
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            checkLocationPermission();
        }

        //show error dialog if Google Play Services not available
        if (!isGooglePlayServicesAvailable()) {
            Log.d("onCreate", "Google Play Services not available. Ending Test case.");
            Objects.requireNonNull(getActivity()).finish();
        }
        else {
            Log.d("onCreate", "Google Play Services available. Continuing.");
        }

        // Create map and get notify when map is ready to be used.
        mapView.onCreate(savedInstanceState);
        mapView.onResume();
        mapView.getMapAsync(this);

        return view;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        //Initialize Google Play Services
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(mContext,
                    Manifest.permission.ACCESS_FINE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED) {
                mMap.setMyLocationEnabled(true);
            }
        } else {
            mMap.setMyLocationEnabled(true);
        }

        if (ActivityCompat.checkSelfPermission(view.getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(view.getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        buildGoogleApiClient();

        mMap.setMyLocationEnabled(true);
    }

    private synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(mContext)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        mGoogleApiClient.connect();
    }

    private void addPlacesToMap(String currentStringLocation) {
        mMyPlaceArrayList = new ArrayList<>();
        disposable = ApiStreams.streamNearbyPlacesAndGetDetails(currentStringLocation).subscribeWith(new DisposableObserver<PlaceDetail>() {

            @Override
            public void onNext(PlaceDetail response) {
                MyPlace mPlace = new MyPlace();

                if (response.getResult() != null) {
                    // Working
                    mPlace.setPlaceId(response.getResult().getPlace_id());
                    mPlace.setFormatted_address(response.getResult().getFormatted_address());
                    mPlace.setName(response.getResult().getName());
                    mPlace.setLat(response.getResult().getGeometry().getLocation().getLat());
                    mPlace.setLng(response.getResult().getGeometry().getLocation().getLng());
                    mPlace.setFormatted_phone_number(response.getResult().getFormatted_phone_number());
                    mPlace.setWebsite(response.getResult().getWebsite());

                    Log.d(TAG, "onNext: getNearbyPlacesAndGetDetails placeId: " + response.getResult().getPlace_id());
                    Log.d(TAG, "onNext: getNearbyPlacesAndGetDetails address: " + response.getResult().getFormatted_address());
                    Log.d(TAG, "onNext: getNearbyPlacesAndGetDetails name: " + response.getResult().getName());
                    Log.d(TAG, "onNext: getNearbyPlacesAndGetDetails lat: " + response.getResult().getGeometry().getLocation().getLat());
                    Log.d(TAG, "onNext: getNearbyPlacesAndGetDetails lng: " + response.getResult().getGeometry().getLocation().getLng());
                    Log.d(TAG, "onNext: getNearbyPlacesAndGetDetails phone: " + response.getResult().getFormatted_phone_number());
                    Log.d(TAG, "onNext: getNearbyPlacesAndGetDetails website: " + response.getResult().getWebsite());

                    mPlace.setPhotos(response.getResult().getPhotos());
                    if (response.getResult().getPhotos() != null) {
                        mPlace.setPhotoReference(response.getResult().getPhotos()[0].getPhotoReference());
                        mPlace.setPhotoURL("https://maps.googleapis.com/maps/api/place/photo?maxwidth=400&photoreference="
                                + response.getResult().getPhotos()[0].getPhotoReference()
                                + "&key=AIzaSyBEzFjiM61SPHxlMp601h_2ztVKCg80gi8");

                        Log.d(TAG, "onNext: " + response.getResult().getPhotos()[0].getPhotoReference());
                    }

                    mMyPlaceArrayList.add(mPlace);
                }
            }

            @Override
            public void onError(Throwable e) {
            }

            @Override
            public void onComplete() {
                Log.d(TAG, "onComplete: " + mMyPlaceArrayList.size());

                progressBar.setVisibility(View.GONE);
                mapView.setVisibility(View.VISIBLE);

                try {
                    mMap.clear();

                    // This loop will go through all the results and add marker on each location.
                    for (int i = 0; i < mMyPlaceArrayList.size(); i++) {
                        MyPlace place = mMyPlaceArrayList.get(i);

                        // Change marker's color according to users' choices
                        int finalI = i;
                        CoworkerHelper.getCoworkersCollection().whereEqualTo("placeChoice", place.getName())
                                .get()
                                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                        boolean isEmpty = Objects.requireNonNull(task.getResult()).isEmpty();

                                        myMarker = mMap.addMarker(new MarkerOptions()
                                                .position(new LatLng(place.getLat(), place.getLng()))
                                                .title(place.getName())
                                                .snippet(String.valueOf(finalI)));

                                        if (isEmpty){
                                            myMarker.setIcon(BitmapDescriptorFactory.fromResource(R.drawable.ic_restaurant_marker_orange));
                                        } else {
                                            myMarker.setIcon(BitmapDescriptorFactory.fromResource(R.drawable.ic_restaurant_marker_green));
                                        }
                                    }
                                });
                    }
                } catch (Exception e) {
                    Log.d("onResponse", "There is an error");
                    e.printStackTrace();
                }

                mMap.setOnMarkerClickListener(marker -> {
                    // Start new PlaceActivity
                    Intent intent = new Intent(mContext, PlaceActivity.class);
                    intent.putExtra("placePosition", marker.getSnippet());

                    MyPlace place = mMyPlaceArrayList.get(Integer.parseInt(marker.getSnippet()));
                    intent.putExtra("myPlaceList", mMyPlaceArrayList);
                    intent.putExtra("myPlace", place);
                    startActivity(intent);
                    return true;
                });
            }
        });
        Log.d(TAG, "getNearbyPlacesAndGetDetails: ");
    }

    @Override
    public void onResume() {
        super.onResume();

    }

    private void checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(mContext,
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED){

            // Asking user if explanation is needed
            if (ActivityCompat.shouldShowRequestPermissionRationale(Objects.requireNonNull(getActivity()), Manifest.permission.ACCESS_FINE_LOCATION)) {

                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.

                ActivityCompat.requestPermissions(getActivity(),
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_LOCATION_PERMISSION_REQUEST_CODE);

            } else {
                // No explanation need, we can request the permission.
                ActivityCompat.requestPermissions(getActivity(),
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_LOCATION_PERMISSION_REQUEST_CODE);
            }
        }
    }

    private boolean isGooglePlayServicesAvailable() {
        GoogleApiAvailability googleAPI = GoogleApiAvailability.getInstance();
        int result = googleAPI.isGooglePlayServicesAvailable(mContext);
        if (result != ConnectionResult.SUCCESS){
            if (googleAPI.isUserResolvableError(result)){
                googleAPI.getErrorDialog(getActivity(), result,
                        0).show();
            }
            return false;
        }
        return true;
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(UPDATE_INTERVAL);
        mLocationRequest.setFastestInterval(UPDATE_FASTEST_INTERVAL);
        mLocationRequest.setSmallestDisplacement(UPDATE_DISPLACEMENT);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
        if (ContextCompat.checkSelfPermission(mContext,
                Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
        }
    }



    @Override
    public void onLocationChanged(Location location) {
        Log.d(TAG, "onLocationChanged: entered");

        mLastLocation = location;

        mLatitude = location.getLatitude();
        mLongitude = location.getLongitude();
        LatLng latLng = new LatLng(mLatitude, mLongitude);

        mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
        mMap.animateCamera(CameraUpdateFactory.zoomTo(15f));

        String currentStringLocation = mLatitude + "," + mLongitude;
        addPlacesToMap(currentStringLocation);
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onConnectionSuspended(int i) {

    }
}
