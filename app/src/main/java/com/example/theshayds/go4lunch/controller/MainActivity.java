package com.example.theshayds.go4lunch.controller;

import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.SearchView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.theshayds.go4lunch.pojo.MyPlace;
import com.example.theshayds.go4lunch.R;
import com.example.theshayds.go4lunch.fragments.CoworkersFragment;
import com.example.theshayds.go4lunch.fragments.MyMapFragment;
import com.example.theshayds.go4lunch.fragments.PlacesFragment;
import com.example.theshayds.go4lunch.utils.ApiRequests;
import com.example.theshayds.go4lunch.utils.GPSTracker;
import com.example.theshayds.go4lunch.utils.CoworkerHelper;
import com.facebook.login.LoginManager;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;

import java.util.ArrayList;

public class MainActivity extends BaseActivity implements NavigationView.OnNavigationItemSelectedListener {
    private static final String TAG = "MainActivity";

    private Context context;

    private FirebaseAuth mAuth;
    private GoogleSignInClient mGoogleSignInClient;

    Toolbar toolbar;

    ImageView mPhoto;
    TextView username, email;

    double mLat, mLng;

    ArrayList<MyPlace> mMyPlaceArrayList;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        context = this.getApplicationContext();

        // Firebase Authentication
        mAuth = FirebaseAuth.getInstance();
        GoogleSignInOptions mGoogleSignInOptions = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, mGoogleSignInOptions);

        Location location = GPSTracker.getInstance(this).getLocation();
        if (location != null) {
            mLat = location.getLatitude();
            mLng = location.getLongitude();

            String currentStringLocation = mLat + " , " + mLng;
            Log.d(TAG, "onCreate: " + currentStringLocation);

            // mMyPlaceArrayList = ApiRequests.getInstance(this).getNearbyPlaces(currentStringLocation);
            mMyPlaceArrayList = ApiRequests.getInstance(this).getNearbyPlacesAndGetDetails(currentStringLocation);
            showFragment(MyMapFragment.newInstance(mLat, mLng, mMyPlaceArrayList));
        } else {
            Log.d(TAG, "onCreate: Location is null");
        }

        BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener = new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.action_map_view:
                        Location mLocation = GPSTracker.getInstance(context).getLocation();
                        boolean canGetLocation = GPSTracker.getInstance(context).canGetLocation;

                        if (canGetLocation) {
                            mLat = mLocation.getLatitude();
                            mLng = mLocation.getLongitude();
                            showFragment(MyMapFragment.newInstance(mLat, mLng, mMyPlaceArrayList));
                        }
                        return true;
                    case R.id.action_list_view:
                        showFragment(PlacesFragment.newInstance(mMyPlaceArrayList));
                        return true;
                    case R.id.action_workmates_view:
                        showFragment(CoworkersFragment.newInstance());
                        return true;
                }
                return false;
            }
        };

        // Bottom Navigation
        BottomNavigationView navigation = findViewById(R.id.bottom_navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        this.configureToolbar();
        this.configureNavigationDrawer();

        // TODO MOVE THIS.
        // Create Firestore Collection "Users" and create current User.
        String uid = mAuth.getCurrentUser().getUid();
        String userName = mAuth.getCurrentUser().getDisplayName();
        String urlPicture = (mAuth.getCurrentUser().getPhotoUrl() != null) ? mAuth.getCurrentUser().getPhotoUrl().toString() : null;

        CollectionReference ref = CoworkerHelper.getCoworkersCollection();

        CoworkerHelper.createUser(uid, userName, urlPicture, false, "").addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.e(TAG, "onFailure: ", e);
            }
        });
    }

    private void configureToolbar() {
        toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("I'm Hungry !");
        setSupportActionBar(toolbar);
    }

    private void configureNavigationDrawer() {
        // Drawer Layout
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        // Navigation Drawer Layout
        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        // Header customization
        View headerView = navigationView.getHeaderView(0);
        updateUIWhenCreating(headerView);
    }

    private void showFragment(Fragment fragment){
        FragmentManager mFragmentManager = getSupportFragmentManager();
        mFragmentManager.beginTransaction()
                .replace(R.id.main_container, fragment)
                .commit();
    }

    // Get User's information from the Firebase currentUser.
    private void updateUIWhenCreating(View headerView){
        mPhoto = headerView.findViewById(R.id.user_picture);
        username = headerView.findViewById(R.id.user_names);
        email = headerView.findViewById(R.id.user_mail);

        if (mAuth.getCurrentUser() != null){
            String mUsername = mAuth.getCurrentUser().getDisplayName();
            String mEmail = mAuth.getCurrentUser().getEmail();

            username.setText(mUsername);
            email.setText(mEmail);

            if (mAuth.getCurrentUser().getPhotoUrl() != null){
                Glide.with(this)
                        .load(mAuth.getCurrentUser().getPhotoUrl())
                        .apply(RequestOptions.circleCropTransform())
                        .into(mPhoto);
            }
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        // Handle navigation view item clicks here.
        int id = menuItem.getItemId();
        if (id == R.id.nav_lunch) {

            // TODO get User's choice from Firestore database and start PlaceActivity with Intent (PlaceID)
            CoworkerHelper.getUser(mAuth.getCurrentUser().getUid());

            // TODO WIP
//            Intent mIntent = new Intent(MainActivity.this, PlaceActivity.class);
//            startActivity(mIntent);

        } else if (id == R.id.nav_settings) {
            // TODO
        } else if (id == R.id.nav_logout) {
            // Firebase Logout
            FirebaseAuth.getInstance().signOut();
            // Facebook Logout
            LoginManager.getInstance().logOut();
            // Google Logout
            mGoogleSignInClient.signOut().addOnCompleteListener(this, new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    Intent mIntent = new Intent(MainActivity.this, AuthenticationActivity.class);
                    startActivity(mIntent);
                    finish();
                }
            });
        }
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        Log.d(TAG, "onCreateOptionsMenu: ");
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);

        SearchView mSearchView = (SearchView) menu.findItem(R.id.action_search).getActionView();
        mSearchView.setQueryHint("Search...");
        mSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                Log.d(TAG, "onQueryTextSubmit: ");
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                Log.d(TAG, "onQueryTextChange: ");
                return false;
            }
        });
        return true;
    }
}
