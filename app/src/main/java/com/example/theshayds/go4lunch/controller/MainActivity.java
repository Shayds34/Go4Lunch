package com.example.theshayds.go4lunch.controller;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.theshayds.go4lunch.R;
import com.example.theshayds.go4lunch.entities.Prediction;
import com.example.theshayds.go4lunch.entities.Predictions;
import com.example.theshayds.go4lunch.fragments.CoworkersFragment;
import com.example.theshayds.go4lunch.fragments.MyMapFragment;
import com.example.theshayds.go4lunch.fragments.PlacesFragment;
import com.example.theshayds.go4lunch.pojo.MyPlace;
import com.example.theshayds.go4lunch.utils.CoworkerHelper;
import com.example.theshayds.go4lunch.utils.PlacesAutocompleteAdapter;
import com.facebook.login.LoginManager;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.twitter.sdk.android.core.models.Place;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class MainActivity extends BaseActivity implements NavigationView.OnNavigationItemSelectedListener {
    private static final String TAG = "MainActivity";

    private FirebaseAuth mAuth;
    private GoogleSignInClient mGoogleSignInClient;

    Toolbar toolbar;

    ImageView mPhoto;
    TextView username, email;

    private AutoCompleteTextView autoCompleteTextView;

    ArrayList<MyPlace> mMyPlaceArrayList;

    FirebaseUser currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        autoCompleteTextView = findViewById(R.id.autocompleteTextViewPlace);

        //region {Firebase Authentication}
        mAuth = FirebaseAuth.getInstance();
        GoogleSignInOptions mGoogleSignInOptions = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, mGoogleSignInOptions);
        currentUser = mAuth.getCurrentUser();
        //endregion

        showFragment(MyMapFragment.getInstance());

        //region {Bottom Navigation Listener}
        BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener = new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.action_map_view:
                        showFragment(MyMapFragment.getInstance());
                        return true;
                    case R.id.action_list_view:
                        showFragment(PlacesFragment.newInstance());
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
        //endregion

        this.configureToolbar();
        this.configureNavigationDrawer();

        //region {Create Firestore Collection "Users" and create current User.}
        String uid = currentUser.getUid();
        String userName = Objects.requireNonNull(currentUser.getDisplayName());
        String urlPicture = (Objects.requireNonNull(currentUser).getPhotoUrl() != null) ? Objects.requireNonNull(currentUser.getPhotoUrl()).toString() : null;

        DocumentReference docRef = CoworkerHelper.getCoworkersCollection().document(uid);
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    assert document != null;
                    if (!document.exists()) {
                        // User doesn't exist in database, create user.
                        Log.d(TAG, "No such document");
                        CoworkerHelper.createUser(uid, userName, urlPicture, false, "", "", "", "", "", "", "");
                    }
                } else {
                    // Failed getting document
                    Log.d(TAG, " get failed with ", task.getException());
                }
            }
        });
        //endregion

        //region {Create Notification Channel}
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            NotificationChannel channel = new NotificationChannel("MyNotifications", "MyNotifications", NotificationManager.IMPORTANCE_DEFAULT);
            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(channel);
        }
        //endregion
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

    public void showFragment(Fragment fragment){
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
        switch (id) {
            case R.id.nav_lunch:
                DocumentReference documentReference = CoworkerHelper.getCoworkersCollection().document(Objects.requireNonNull(mAuth.getCurrentUser()).getUid());
                documentReference.get().addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        assert document != null;
                        if (document.exists()) {
                            boolean hasChosen = document.getBoolean("hasChosen");
                            Log.d(TAG, "onComplete: " + hasChosen);

                            if (hasChosen) {
                                Intent intent = new Intent(MainActivity.this, YourLunchActivity.class);
                                intent.putExtra("placeName", document.getString("placeName"));
                                startActivity(intent);
                            } else {
                                Toast.makeText(this, "You have to choose a place.", Toast.LENGTH_LONG).show();
                            }
                        } else {
                            Log.d(TAG, "onComplete: No such document");
                        }
                    } else {
                        Log.d(TAG, "onComplete: failure.", task.getException());
                    }
                });
                break;

            case R.id.nav_settings:
                // TODO
                break;

            case R.id.nav_logout:
                // Firebase & Twitter Logout
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
                break;
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
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.action_search: {
                Log.d(TAG, "Search initialized.");

                autoCompleteTextView.setVisibility(View.VISIBLE);
                loadData();

                return true;
            }
        }
        return false;
    }

    private void loadData() {
        List<Prediction> predictionsList = new ArrayList<>();
        PlacesAutocompleteAdapter placesAutocompleteAdapter = new PlacesAutocompleteAdapter(getApplicationContext(), predictionsList);
        autoCompleteTextView.setThreshold(1);
        autoCompleteTextView.setAdapter(placesAutocompleteAdapter);
    }
}
