package com.example.theshayds.go4lunch.controller;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.Priority;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.example.theshayds.go4lunch.R;
import com.example.theshayds.go4lunch.models.Coworker;
import com.example.theshayds.go4lunch.utils.CoworkerAdapter;
import com.example.theshayds.go4lunch.utils.CoworkerHelper;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.material.appbar.AppBarLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.Query;

import java.util.Objects;

public class YourLunchActivity extends BaseActivity implements CoworkerAdapter.Listener {
    private static final String TAG = "YourLunchActivity";

    private String placeAddress;
    private String placeName;
    private String placePhoto;

    // Firebase
    FirebaseAuth mAuth;
    private CollectionReference coworkerReference;

    TextView mPlaceName;
    TextView mPlaceAddress;
    ImageView mPhoto;
    boolean like = false;
    Coworker coworker;

    private CoworkerAdapter adapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_place);


        mAuth = FirebaseAuth.getInstance();
        coworkerReference = CoworkerHelper.getCoworkersCollection();

        placeName = getIntent().getStringExtra("placeName");

        AppBarLayout mAppBarLayout = findViewById(R.id.app_bar);
        mAppBarLayout.setBackground(getDrawable(R.drawable.nav_header_background));

        // Bind Views.
        mPlaceName = findViewById(R.id.place_name);
        mPlaceAddress = findViewById(R.id.place_address);
        mPhoto = findViewById(R.id.place_photo);

        // Firebase
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null){
            DocumentReference documentReference = CoworkerHelper.getCoworkersCollection().document(Objects.requireNonNull(currentUser.getUid()));
            documentReference.get().addOnCompleteListener(task -> {
                if (task.isSuccessful()){
                    DocumentSnapshot document = task.getResult();
                    if (document != null){
                        coworker = document.toObject(Coworker.class);
                        if (coworker != null){
                            placeName = coworker.getPlaceName();
                            placePhoto = coworker.getPlacePhoto();
                            placeAddress = coworker.getPlaceAddress();
                        }
                        updateUI();
                    }
                }
            });
        } else {
            Toast.makeText(this, getResources().getString(R.string.authentication_needed), Toast.LENGTH_SHORT).show();
        }

        this.configureRecyclerView();
        adapter.notifyDataSetChanged();

        findViewById(R.id.button_call).setOnClickListener(v -> {
            String phoneNumberToCall = coworker.getPlacePhone();
            if (phoneNumberToCall == null) {
                Toast.makeText(YourLunchActivity.this, getResources().getString(R.string.no_phone), Toast.LENGTH_SHORT).show();
            } else {
                Intent dialIntent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + coworker.getPlacePhone()));
                startActivity(dialIntent);
            }
        });

        findViewById(R.id.button_website).setOnClickListener(v -> {
            String URI = coworker.getPlaceWebsite();
            if (URI == null){
                Toast.makeText(YourLunchActivity.this, getResources().getString(R.string.no_website), Toast.LENGTH_SHORT).show();
            } else {
                Intent websiteIntent = new Intent(Intent.ACTION_VIEW);
                websiteIntent.setData(Uri.parse(URI));
                startActivity(websiteIntent);
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

        // It's the current user's choice, no need to let him choose it again.
        findViewById(R.id.fab).setVisibility(View.GONE);
    }

    private void updateUI() {
        // Give all information from Place Detail to all views.
        mPlaceName.setText(placeName);
        mPlaceAddress.setText(placeAddress);

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
                .load(placePhoto)
                .apply(mOptions)
                .into(mPhoto);
    }

    private void configureRecyclerView() {

        // Get the list of Coworkers with placeName.
        Query mQuery = coworkerReference.whereEqualTo("placeName", placeName);

        FirestoreRecyclerOptions<Coworker> mOptions = new FirestoreRecyclerOptions.Builder<Coworker>()
                .setQuery(mQuery, Coworker.class)
                .build();
        RecyclerView mRecyclerView = findViewById(R.id.recycler_view);
        adapter = new CoworkerAdapter(this, mOptions, Glide.with(this),this, "YourLunchActivity");

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
