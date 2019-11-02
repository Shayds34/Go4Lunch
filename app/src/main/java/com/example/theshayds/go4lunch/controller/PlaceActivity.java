package com.example.theshayds.go4lunch.controller;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.core.content.res.ResourcesCompat;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.Priority;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.example.theshayds.go4lunch.R;
import com.example.theshayds.go4lunch.models.Coworker;
import com.example.theshayds.go4lunch.pojo.PlaceDetail;
import com.example.theshayds.go4lunch.utils.ApiStreams;
import com.example.theshayds.go4lunch.utils.CoworkerAdapter;
import com.example.theshayds.go4lunch.utils.CoworkerHelper;
import com.example.theshayds.go4lunch.utils.NotificationsService;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.material.appbar.AppBarLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.Query;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Objects;

import io.reactivex.disposables.Disposable;
import io.reactivex.observers.DisposableObserver;

public class PlaceActivity extends BaseActivity implements CoworkerAdapter.Listener{
    private String TAG = "PlaceActivity";

    //region {Initializing}
    // Place info
    private String placeAddress;
    private String placeName;
    private String placePhoto;
    private String phoneNumberToCall;
    private String placeURI;

    // Firebase
    FirebaseAuth mAuth;
    private CollectionReference coworkerReference;

    // Views
    TextView mPlaceName;
    TextView mPlaceAddress;
    ImageView mPhoto;

    boolean like = false;
    Disposable disposable;
    private CoworkerAdapter adapter;
    //endregion

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_place);

        mAuth = FirebaseAuth.getInstance();
        coworkerReference = CoworkerHelper.getCoworkersCollection();

        AppBarLayout mAppBarLayout = findViewById(R.id.app_bar);
        mAppBarLayout.setBackground(getDrawable(R.drawable.nav_header_background));

        // Bind Views.
        mPlaceName = findViewById(R.id.place_name);
        mPlaceAddress = findViewById(R.id.place_address);
        mPhoto = findViewById(R.id.place_photo);

        // Get placeId from Intent Extras to fetch Place Info.
        String placeId = getIntent().getStringExtra("placeId");
        this.fetchPlaceInfo(placeId);

        findViewById(R.id.button_call).setOnClickListener(v -> {
            if (phoneNumberToCall == null) {
                Toast.makeText(PlaceActivity.this, getResources().getString(R.string.no_phone), Toast.LENGTH_SHORT).show();
            } else {
                Intent dialIntent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + phoneNumberToCall));
                startActivity(dialIntent);
            }
        });

        findViewById(R.id.button_website).setOnClickListener(v -> {
            if (placeURI == null){
                Toast.makeText(PlaceActivity.this, getResources().getString(R.string.no_website), Toast.LENGTH_SHORT).show();
            } else {
                Intent websiteIntent = new Intent(Intent.ACTION_VIEW);
                websiteIntent.setData(Uri.parse(placeURI));
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

        findViewById(R.id.fab).setOnClickListener(v -> {
            FirebaseUser currentUser = mAuth.getCurrentUser();
            // Update Firestore database with user's choice.
            if (currentUser != null) {
                String uid = currentUser.getUid();
                CoworkerHelper.updatePlace(uid,true, placeId, placeName);

                startAlarmReceiver();
            } else {
                Toast.makeText(PlaceActivity.this, getResources().getString(R.string.authentication_needed), Toast.LENGTH_SHORT).show();
            }
        });
    }

    // If user makes a choice, start alarm receiver to reset his choice next day at noon
    private void startAlarmReceiver(){
        // Build new alarmManager using ALARM_SERVICE
        Intent notificationIntent = new Intent(this, NotificationsService.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        AlarmManager alarmManager = (AlarmManager) this.getSystemService(Context.ALARM_SERVICE);

        Calendar triggerCal = new GregorianCalendar();
        int hourOfToday = triggerCal.get(Calendar.HOUR_OF_DAY);

        // Set the alarm at noon or the next day at noon.
        if (hourOfToday < 12) {
            triggerCal.set(Calendar.HOUR_OF_DAY, 0);
            triggerCal.set(Calendar.MINUTE, 0);
            triggerCal.set(Calendar.SECOND, 0);
            triggerCal.add(Calendar.MILLISECOND, 1000 * 60 * 60 *  12);
        } else {
            triggerCal.set(Calendar.HOUR_OF_DAY, 0);
            triggerCal.set(Calendar.MINUTE, 0);
            triggerCal.set(Calendar.SECOND, 0);
            // triggerCal.add(Calendar.DAY_OF_MONTH, 1);
            triggerCal.add(Calendar.MILLISECOND,  54000000 + 2640000 );
        }

        long triggerTime = triggerCal.getTimeInMillis();

        alarmManager.set(AlarmManager.RTC_WAKEUP, triggerTime, pendingIntent);
    }

    private void fetchPlaceInfo(String placeId) {
        disposable = ApiStreams.streamDetailsPlaces(placeId, getResources().getString(R.string.google_api_key)).subscribeWith(new DisposableObserver<PlaceDetail>() {
            @Override
            public void onNext(PlaceDetail placeDetail) {
                Log.d(TAG, "onNext: " + placeDetail.getStatus());
                if (placeDetail.getResult() != null) {
                    placeName = placeDetail.getResult().getName();
                    placeAddress = placeDetail.getResult().getVicinity();
                    if (placeDetail.getResult().getPhotos() != null) {
                        placePhoto = "https://maps.googleapis.com/maps/api/place/photo?maxwidth=400&photoreference="
                                + placeDetail.getResult().getPhotos()[0].getPhotoReference()
                                + "&key=AIzaSyBEzFjiM61SPHxlMp601h_2ztVKCg80gi8";
                    }
                    phoneNumberToCall = placeDetail.getResult().getFormatted_phone_number();
                    placeURI = placeDetail.getResult().getWebsite();
                }
            }

            @Override
            public void onError(Throwable e) {
                Log.d(TAG, "onError: " + e);
            }

            @Override
            public void onComplete() {
                Log.d(TAG, "onComplete: ");
                updateUI();
                configureRecyclerView();
                adapter.notifyDataSetChanged();
            }
        });
    }

    public void updateUI(){
        // Give all information from Place Detail to all views.
        mPlaceName.setText(placeName);
        mPlaceAddress.setText(placeAddress);

        // Setup default options for GLIDE
        RequestOptions mOptions = new RequestOptions()
                .centerCrop()
                .placeholder(R.drawable.ic_photo_camera_black_24dp)
                .error(R.drawable.ic_photo_camera_black_24dp)
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
        adapter = new CoworkerAdapter(this, mOptions, Glide.with(this),this, "PlaceActivity");

        DividerItemDecoration mDividerItemDecoration = new DividerItemDecoration(mRecyclerView.getContext(), DividerItemDecoration.VERTICAL);
        mDividerItemDecoration.setDrawable(Objects.requireNonNull(ResourcesCompat.getDrawable(getResources(), R.drawable.line_divider, null)));

        adapter.startListening();

        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.setAdapter(adapter);
    }

    @Override
    public void onDataChanged() {
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onStop() {
        super.onStop();
        adapter.stopListening();
    }
}
