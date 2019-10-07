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
import com.google.maps.android.SphericalUtil;

import java.util.ArrayList;
import java.util.Objects;

import io.reactivex.disposables.Disposable;
import io.reactivex.observers.DisposableObserver;


public class MyMapFragment extends Fragment implements OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks, LocationListener, GoogleApiClient.OnConnectionFailedListener {
    public static final String TAG = "MyMapFragment";

    private static volatile MyMapFragment instance;

    private Context mContext;
    private View view;
    private MapView mapView;
    private GoogleMap mMap;
    private Marker myMarker;

    private ProgressBar progressBar;

    //region {Location Purpose}
    private static final int MY_LOCATION_PERMISSION_REQUEST_CODE = 9001;

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

    ArrayList<MyPlace> getMyPlaceArrayList() {
        return mMyPlaceArrayList;
    }

    private ArrayList<MyPlace> mMyPlaceArrayList;

    private Disposable disposable;

    public MyMapFragment() {
        // Required empty public constructor
    }

    public static MyMapFragment getInstance() {
        if (instance == null){
            instance = new MyMapFragment();
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

        Log.d(TAG, "onCreateView: ");

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
        Log.d(TAG, "onMapReady: ");

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
        Log.d(TAG, "buildGoogleApiClient: ");
        mGoogleApiClient = new GoogleApiClient.Builder(mContext)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        mGoogleApiClient.connect();
    }

    private void addPlacesToMap(String currentStringLocation) {
        Log.d(TAG, "addPlacesToMap: ");
        mMyPlaceArrayList = new ArrayList<>();
        disposable = ApiStreams.streamNearbyPlacesAndGetDetails(currentStringLocation, getResources().getString(R.string.google_api_key)).subscribeWith(new DisposableObserver<PlaceDetail>() {
            @Override
            public void onNext(PlaceDetail response) {
                MyPlace mPlace = new MyPlace();
                Log.d(TAG, "onNext: " + response.getStatus());

                if (response.getResult() != null) {
                    // Working
                    mPlace.setPlaceId(response.getResult().getPlace_id());
                    mPlace.setFormatted_address(response.getResult().getVicinity());
                    mPlace.setName(response.getResult().getName());
                    mPlace.setLat(response.getResult().getGeometry().getLocation().getLat());
                    mPlace.setLng(response.getResult().getGeometry().getLocation().getLng());
                    mPlace.setFormatted_phone_number(response.getResult().getFormatted_phone_number());
                    mPlace.setWebsite(response.getResult().getWebsite());
                    mPlace.setRating(response.getResult().getRating());

                    // Display distance between the user and the place.
                    LatLng userLatLng = new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude());
                    LatLng placeLatLng = new LatLng(response.getResult().getGeometry().getLocation().getLat(), response.getResult().getGeometry().getLocation().getLng());
                    double distanceBetween = SphericalUtil.computeDistanceBetween(userLatLng, placeLatLng);
                    int distanceInMeters = (int) distanceBetween;
                    mPlace.setDistance(distanceInMeters);

                    try {
                        mPlace.setOpeningHours(response.getResult().getOpening_hours());
                        Log.d(TAG, "onNext: " + response.getResult().getOpening_hours());
                    } catch (Exception exception) {
                        exception.printStackTrace();
                    }

                    mPlace.setPhotos(response.getResult().getPhotos());
                    if (response.getResult().getPhotos() != null) {
                        mPlace.setPhotoReference(response.getResult().getPhotos()[0].getPhotoReference());
                        mPlace.setPhotoURL("https://maps.googleapis.com/maps/api/place/photo?maxwidth=400&photoreference="
                                + response.getResult().getPhotos()[0].getPhotoReference()
                                + "&key=AIzaSyBEzFjiM61SPHxlMp601h_2ztVKCg80gi8");
                    }
                    mMyPlaceArrayList.add(mPlace);
                }
            }

            @Override
            public void onError(Throwable e) {
            }

            @Override
            public void onComplete() {
                progressBar.setVisibility(View.GONE);
                mapView.setVisibility(View.VISIBLE);
                updateMarkers(mMyPlaceArrayList);
            }
        });
    }

    private void updateMarkers(ArrayList<MyPlace> mMyPlaceArrayList) {
        Log.d(TAG, "updateMarkers: ");
        try {
            mMap.clear();

            // This loop will go through all the results and add marker on each location.
            for (int i = 0; i < mMyPlaceArrayList.size(); i++) {
                MyPlace place = mMyPlaceArrayList.get(i);

                CoworkerHelper.getCoworkersCollection().whereEqualTo("placeName", place.getName())
                        .get()
                        .addOnCompleteListener(task -> {
                            boolean isEmpty = Objects.requireNonNull(task.getResult()).isEmpty();

                            myMarker = mMap.addMarker(new MarkerOptions()
                                    .position(new LatLng(place.getLat(), place.getLng()))
                                    .title(place.getName())
                                    .snippet(place.getPlaceId()));

                            // Change marker's color according to users' choices
                            if (isEmpty){
                                myMarker.setIcon(BitmapDescriptorFactory.fromResource(R.drawable.ic_restaurant_marker_orange));
                            } else {
                                myMarker.setIcon(BitmapDescriptorFactory.fromResource(R.drawable.ic_restaurant_marker_green));
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
            intent.putExtra("placeId", marker.getSnippet());
            startActivity(intent);
            return true;
        });
    }

    @Override
    public void onResume() {
        super.onResume();

    }

    private void checkLocationPermission() {
        Log.d(TAG, "checkLocationPermission: ");
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
        Log.d(TAG, "isGooglePlayServicesAvailable: ");
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
        Log.d(TAG, "onConnected: ");
        LocationRequest mLocationRequest = new LocationRequest();
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
        mMap.clear();

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

    public Location getLastLocation() {
        return mLastLocation;
    }
}
