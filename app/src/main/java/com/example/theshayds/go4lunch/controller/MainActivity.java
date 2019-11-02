package com.example.theshayds.go4lunch.controller;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
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
import com.example.theshayds.go4lunch.fragments.CoworkersFragment;
import com.example.theshayds.go4lunch.fragments.MyMapFragment;
import com.example.theshayds.go4lunch.fragments.PlacesFragment;
import com.example.theshayds.go4lunch.utils.CoworkerHelper;
import com.example.theshayds.go4lunch.utils.PlacesAutocompleteAdapter;
import com.facebook.login.LoginManager;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class MainActivity extends BaseActivity implements NavigationView.OnNavigationItemSelectedListener {
    private static final String TAG = "MainActivity";

    private FirebaseAuth mAuth;
    private GoogleSignInClient mGoogleSignInClient;

    Toolbar toolbar;
    AppBarLayout appBarLayout;
    RelativeLayout searchView;

    ImageView mPhoto;
    String uid, userName, urlPicture;
    TextView username, email;

    FirebaseUser currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

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

        //region {Bottom Navigation}
        BottomNavigationView navigation = findViewById(R.id.bottom_navigation);
        // Bottom Navigation Listener
        BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener = item -> {
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
        };
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        //endregion

        this.configureToolbar();
        this.configureNavigationDrawer();

        //region {Create Firestore Collection "Users" and create current User.}
        uid = currentUser.getUid();
        if (currentUser.getDisplayName() != null){
            userName = Objects.requireNonNull(currentUser.getDisplayName());
            urlPicture = (Objects.requireNonNull(currentUser).getPhotoUrl() != null) ? Objects.requireNonNull(currentUser.getPhotoUrl()).toString() : null;
        } else {
            userName = "";
            urlPicture = "";
        }

        DocumentReference docRef = CoworkerHelper.getCoworkersCollection().document(uid);
        docRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                assert document != null;
                if (!document.exists()) {
                    // User doesn't exist in database, create user.
                    Log.d(TAG, "No such document");
                    CoworkerHelper.createUser(uid, userName, urlPicture, false, "", "");
                }
            } else {
                // Failed getting document
                Log.d(TAG, " get failed with ", task.getException());
            }
        });
        //endregion
    }

    private void configureToolbar() {
        searchView = findViewById(R.id.search_layout);
        appBarLayout = findViewById(R.id.appBarLayout);
        toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle(getString(R.string.toolbar_hungry));
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
        // Show fragment according to BottomNavigation current item
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

        // Update UI with currentUser's information
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
            } else {

                DocumentReference dr = CoworkerHelper.getCoworkersCollection().document(mAuth.getCurrentUser().getUid());
                dr.get().addOnCompleteListener(task -> {
                    if (task.isSuccessful()){
                        DocumentSnapshot document = task.getResult();
                        if (document != null){
                            Log.d(TAG, "updateUIWhenCreating: photo is " + document.getString("urlPicture"));
                            Glide.with(getApplicationContext())
                                    .load(document.getString("urlPicture"))
                                    .apply(RequestOptions.circleCropTransform())
                                    .into(mPhoto);
                        }
                    }
                });

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
                            Boolean hasChosen = document.getBoolean("hasChosen");
                            if (hasChosen != null) {
                                if (hasChosen) {
                                    Intent intent = new Intent(MainActivity.this, PlaceActivity.class);
                                    intent.putExtra("placeId", document.getString("placeID"));
                                    startActivity(intent);
                                } else {
                                    Toast.makeText(this, getResources().getString(R.string.choose_a_place), Toast.LENGTH_LONG).show();
                                }
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
                Intent intent = new Intent(this, SettingsActivity.class);
                startActivity(intent);
                break;

            case R.id.nav_logout:
                // Firebase & Twitter Logout
                FirebaseAuth.getInstance().signOut();
                // Facebook Logout
                LoginManager.getInstance().logOut();
                // Google Logout
                mGoogleSignInClient.signOut().addOnCompleteListener(this, task -> {
                    Intent mIntent = new Intent(MainActivity.this, AuthenticationActivity.class);
                    startActivity(mIntent);
                    finish();
                });
                break;
        }
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_search) {
            loadData();
            return true;
        }
        return false;
    }


    private void loadData() {
        // Change views visibility
        searchView.setVisibility(View.VISIBLE);
        appBarLayout.setVisibility(View.INVISIBLE);

        AutoCompleteTextView autoCompleteTextView = findViewById(R.id.autocomplete_search_input);

        // Create List and Adapter
        List<Prediction> predictionsList = new ArrayList<>();
        PlacesAutocompleteAdapter placesAutocompleteAdapter = new PlacesAutocompleteAdapter(this, predictionsList);
        autoCompleteTextView.setThreshold(1);
        autoCompleteTextView.setAdapter(placesAutocompleteAdapter);

        // Clear AutocompleteTextView and Adapter
        ImageView clearButton = findViewById(R.id.clear_button);
        clearButton.setOnClickListener(v -> {
            autoCompleteTextView.setText("");
            placesAutocompleteAdapter.clear();
            searchView.setVisibility(View.INVISIBLE);
            appBarLayout.setVisibility(View.VISIBLE);
        });
    }
}
