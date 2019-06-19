package com.example.theshayds.go4lunch.fragments;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.example.theshayds.go4lunch.pojo.MyPlace;
import com.example.theshayds.go4lunch.R;
import com.example.theshayds.go4lunch.controller.PlaceActivity;
import com.example.theshayds.go4lunch.utils.CoworkerHelper;
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

public class MyMapFragment extends Fragment {
    public static final String TAG = "MyMapFragment";

    // Fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "mLat";
    private static final String ARG_PARAM2 = "mLng";
    private static final String ARG_ARRAY_LIST = "mList";

    private double mLat;
    private double mLng;
    private ArrayList<MyPlace> myPlaceArrayList;

    private GoogleMap mMap;
    private MapView mapView;
    private Marker myMarker;

    public MyMapFragment() {
        // Required empty public constructor
    }

    public static MyMapFragment newInstance(double param1, double param2, ArrayList param3) {
        MyMapFragment fragment = new MyMapFragment();
        Bundle args = new Bundle();
        args.putDouble(ARG_PARAM1, param1);
        args.putDouble(ARG_PARAM2, param2);
        args.putParcelableArrayList(ARG_ARRAY_LIST, param3);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mLat = getArguments().getDouble(ARG_PARAM1);
            mLng = getArguments().getDouble(ARG_PARAM2);
            myPlaceArrayList = getArguments().getParcelableArrayList(ARG_ARRAY_LIST);
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View view = inflater.inflate(R.layout.fragment_custom_map, container, false);
        mapView = view.findViewById(R.id.map_view);
        mapView.onCreate(savedInstanceState);

        mapView.onResume();

        mapView.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap) {
                mMap = googleMap;

                // Add Marker Current Position
                LatLng mLatLng = new LatLng(mLat, mLng);
                if (ActivityCompat.checkSelfPermission(view.getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                        && ActivityCompat.checkSelfPermission(view.getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    return;
                }
                mMap.setMyLocationEnabled(true);
                mMap.moveCamera(CameraUpdateFactory.newLatLng(mLatLng));
                mMap.animateCamera(CameraUpdateFactory.zoomTo(15f));

                for (int i = 0; i < myPlaceArrayList.size(); i++) {
                    MyPlace place = myPlaceArrayList.get(i);

                    // Change marker's color according to users' choices.
                    int finalI = i;
                    CoworkerHelper.getCoworkersCollection().whereEqualTo("placeChoice", place.getName())
                            .get()
                            .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                    if (task.isSuccessful()){
                                        boolean isEmpty = task.getResult().isEmpty();

                                        myMarker = mMap.addMarker(new MarkerOptions()
                                                .position(new LatLng(place.getLat(), place.getLng()))
                                                .title(place.getName())
                                                .snippet(String.valueOf(finalI))); // Snippet here is the "position" of the place in list.

                                        // Change marker's color according to users' choices.
                                        if (isEmpty){
                                            myMarker.setIcon(BitmapDescriptorFactory.fromResource(R.drawable.ic_restaurant_marker_orange));
                                        } else {
                                            myMarker.setIcon(BitmapDescriptorFactory.fromResource(R.drawable.ic_restaurant_marker_green));
                                        }
                                    }
                                }
                            });
                }

                mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                    @Override
                    public boolean onMarkerClick(Marker marker) {
                        // Start new PlaceActivity
                        Intent mIntent = new Intent(view.getContext(), PlaceActivity.class);
                        mIntent.putExtra("placePosition", marker.getSnippet());
                        startActivity(mIntent);
                        return true;
                    }
                });
            }
        });
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }

    @Override
    public void onLowMemory(){
        super.onLowMemory();
        mapView.onLowMemory();
    }
}
