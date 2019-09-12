package com.example.theshayds.go4lunch.controller;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import androidx.annotation.Nullable;

import com.example.theshayds.go4lunch.fragments.MyMapFragment;
import com.google.android.material.appbar.AppBarLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.Priority;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.example.theshayds.go4lunch.models.Coworker;
import com.example.theshayds.go4lunch.pojo.MyPlace;
import com.example.theshayds.go4lunch.R;
import com.example.theshayds.go4lunch.utils.CoworkerAdapter;
import com.example.theshayds.go4lunch.utils.CoworkerHelper;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.Query;

public class PlaceActivity extends BaseActivity implements CoworkerAdapter.Listener{
    private String TAG = "PlaceActivity";

    // Firebase
    private CollectionReference coworkerReference;

    TextView mPlaceName;
    TextView mPlaceAddress;
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
        place = MyMapFragment.getInstance().getMyPlaceArrayList().get(position);

        // Firebase
        coworkerReference = CoworkerHelper.getCoworkersCollection();

        // Setup RecyclerView
        this.configureRecyclerView();
        adapter.notifyDataSetChanged();

        findViewById(R.id.button_call).setOnClickListener(v -> {
            String phoneNumberToCall = place.getFormatted_phone_number();
            if (phoneNumberToCall == null) {
                Toast.makeText(PlaceActivity.this, "There is no phone number for this place.", Toast.LENGTH_SHORT).show();
            } else {
                Intent dialIntent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + place.getFormatted_phone_number()));
                startActivity(dialIntent);
            }
        });

        findViewById(R.id.button_website).setOnClickListener(v -> {
            String URI = place.getWebsite();
            if (URI != null) {
                Intent websiteIntent = new Intent(Intent.ACTION_VIEW);
                websiteIntent.setData(Uri.parse(URI));
                startActivity(websiteIntent);
            } else {
                Toast.makeText(PlaceActivity.this, "There is no website for this place.", Toast.LENGTH_SHORT).show();
            }
        });

        findViewById(R.id.button_like).setOnClickListener(v -> {
            if (like){
                findViewById(R.id.place_empty_star).setVisibility(View.GONE);
                like = false;
            } else {
                findViewById(R.id.place_empty_star).setVisibility(View.VISIBLE);
                like = true;
            }
        });

        findViewById(R.id.fab).setOnClickListener(v -> {
            FirebaseUser currentUser = mAuth.getCurrentUser();
            // Update Firestore database with user's choice.
            if (currentUser != null) {
                String uid = currentUser.getUid();
                String userName = currentUser.getDisplayName();
                String urlPicture = String.valueOf(currentUser.getPhotoUrl());
                String placeID = place.getPlaceId();
                String placeName = place.getName();
                String placeAddress = place.getFormatted_address();
                String placeWebsite = place.getWebsite();
                String placePhoto = place.getPhotoURL();
                String placePhone = place.getFormatted_phone_number();
                String placeUrl = place.getUrl();

                CoworkerHelper.updatePlace(uid, userName, urlPicture, true, placeID, placeName, placeAddress, placeWebsite, placePhoto, placePhone, placeUrl);
            } else {
                Toast.makeText(PlaceActivity.this, "You should connect to the application first.", Toast.LENGTH_SHORT).show();
            }
        });

        updateUI();
    }

    public void updateUI(){
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
        Query mQuery = coworkerReference.whereEqualTo("placeName", place.getName());

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
