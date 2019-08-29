package com.example.theshayds.go4lunch.controller;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import androidx.annotation.Nullable;

import com.example.theshayds.go4lunch.fragments.MyMapFragment2;
import com.google.android.material.appbar.AppBarLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.Priority;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.example.theshayds.go4lunch.models.Coworker;
import com.example.theshayds.go4lunch.pojo.MyPlace;
import com.example.theshayds.go4lunch.R;
import com.example.theshayds.go4lunch.utils.ApiRequests;
import com.example.theshayds.go4lunch.utils.CoworkerAdapter;
import com.example.theshayds.go4lunch.utils.CoworkerHelper;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.Query;

import java.util.ArrayList;

public class PlaceActivity extends AppCompatActivity implements CoworkerAdapter.Listener{
    private String TAG = "PlaceActivity";

    // Firebase
    private CollectionReference coworkerReference;

    String mPlaceId;
    TextView mPlaceName, mPlaceAddress, mPlacePhone, mPlaceWeb;
    ImageView mPhoto;
    boolean like = false;

    private CoworkerAdapter adapter;

    FirebaseAuth mAuth;

    MyPlace place;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_place);

        mAuth = FirebaseAuth.getInstance();

        AppBarLayout mAppBarLayout = findViewById(R.id.app_bar);
        mAppBarLayout.setBackground(getDrawable(R.drawable.nav_header_background));

        // Bind Views.
        mPlaceName = findViewById(R.id.place_name);
        mPlaceAddress = findViewById(R.id.place_address);
        mPhoto = findViewById(R.id.place_photo);

        Intent mIntent = getIntent();
        int position = Integer.parseInt(mIntent.getStringExtra("placePosition"));
        place = MyMapFragment2.getInstance().getMyPlaceArrayList().get(position);

        Log.d(TAG, "onCreate: " + place.getName() + " , " + place.getFormatted_address() + " , " + place.getWebsite());

        // Firebase
        coworkerReference = CoworkerHelper.getCoworkersCollection();

        // Setup RecyclerView
        this.configureRecyclerView();
        adapter.notifyDataSetChanged();

        findViewById(R.id.button_call).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //
                Intent dialIntent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + place.getFormatted_phone_number()));
                startActivity(dialIntent);

                // TODO if phone number is null
            }
        });

        findViewById(R.id.button_website).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //
                String URI = place.getWebsite();
                Intent websiteIntent = new Intent(Intent.ACTION_VIEW);
                websiteIntent.setData(Uri.parse(URI));
                startActivity(websiteIntent);

                // TODO if website is null
            }
        });

        findViewById(R.id.button_like).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (like){
                    findViewById(R.id.place_empty_star).setVisibility(View.GONE);
                    like = false;
                } else {
                    findViewById(R.id.place_empty_star).setVisibility(View.VISIBLE);
                    like = true;
                }
            }
        });

        findViewById(R.id.fab).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // Update Firestore database with user's choice.
                // If user selects a different place, marker will change color.
                CoworkerHelper.updatePlace(mAuth.getCurrentUser().getUid(), place.getName());
                CoworkerHelper.updateHasChosen(mAuth.getCurrentUser().getUid(), true);
            }
        });

        // Give all information from Place Detail to all views.
        mPlaceName.setText(place.getName());
        mPlaceAddress.setText(place.getFormatted_address());

        // Setup default options for GLIDE
        RequestOptions mOptions = new RequestOptions()
                .centerCrop()
                .placeholder(R.drawable.ic_restaurant_marker_green)
                .error(R.drawable.ic_restaurant_marker_green)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .priority(Priority.HIGH)
                .dontAnimate()
                .dontTransform();

        Glide.with(this)
                .load(place.getPhotoURL())
                .apply(mOptions)
                .into(mPhoto);
    }

    private void configureRecyclerView() {

        // Get the list of Coworkers with placeName.
        Query mQuery = coworkerReference.whereEqualTo("placeChoice", place.getName());

        FirestoreRecyclerOptions<Coworker> mOptions = new FirestoreRecyclerOptions.Builder<Coworker>()
                .setQuery(mQuery, Coworker.class)
                .build();

        RecyclerView mRecyclerView = findViewById(R.id.recycler_view);
        adapter = new CoworkerAdapter(this, mOptions, Glide.with(this),this, "PlaceActivity");

        DividerItemDecoration mDividerItemDecoration = new DividerItemDecoration(mRecyclerView.getContext(), DividerItemDecoration.VERTICAL);
        mDividerItemDecoration.setDrawable(getResources().getDrawable(R.drawable.line_divider));

        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.setAdapter(adapter);
    }

    @Override
    public void onDataChanged() {
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onStart() {
        super.onStart();
        adapter.startListening();
    }

    @Override
    public void onStop() {
        super.onStop();
        adapter.stopListening();
    }
}
