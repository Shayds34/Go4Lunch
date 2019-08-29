package com.example.theshayds.go4lunch.controller;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.SearchView;
import android.widget.TextView;

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
import com.example.theshayds.go4lunch.fragments.CoworkersFragment;
import com.example.theshayds.go4lunch.fragments.MyMapFragment2;
import com.example.theshayds.go4lunch.fragments.PlacesFragment;
import com.example.theshayds.go4lunch.pojo.MyPlace;
import com.example.theshayds.go4lunch.utils.CoworkerHelper;
import com.facebook.login.LoginManager;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;

import java.util.ArrayList;
import java.util.Objects;

public class MainActivity extends BaseActivity implements NavigationView.OnNavigationItemSelectedListener {
    private static final String TAG = "MainActivity";

    private FirebaseAuth mAuth;
    private GoogleSignInClient mGoogleSignInClient;

    Toolbar toolbar;

    ImageView mPhoto;
    TextView username, email;

    ArrayList<MyPlace> mMyPlaceArrayList;

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
        //endregion

        showFragment(MyMapFragment2.getInstance());

        //region {Bottom Navigation Listener}
        BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener = new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.action_map_view:
                        showFragment(MyMapFragment2.getInstance());
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
        //endregion

        // Bottom Navigation
        BottomNavigationView navigation = findViewById(R.id.bottom_navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        this.configureToolbar();
        this.configureNavigationDrawer();

        //region {Create Firestore Collection "Users" and create current User.}
        String uid = Objects.requireNonNull(mAuth.getCurrentUser()).getUid();
        String userName = mAuth.getCurrentUser().getDisplayName();
        String urlPicture = (mAuth.getCurrentUser().getPhotoUrl() != null) ? mAuth.getCurrentUser().getPhotoUrl().toString() : null;


//        DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();
//        DatabaseReference userNameID = rootRef.child("users").child(mAuth.getCurrentUser().getUid());
//        ValueEventListener eventListener = new ValueEventListener() {
//            @Override
//            public void onDataChange(DataSnapshot dataSnapshot) {
//                // Creating new user in database
//                if (!dataSnapshot.exists()) {
//                    Log.d(TAG, "onDataChange: entered and doesn't exist");
//                CoworkerHelper.createUser(uid, userName, urlPicture, false, "").addOnFailureListener(e -> Log.e(TAG, "onFailure: ", e));
//                } else {
//                    Log.d(TAG, "onDataChange: entered and exist");
//                }
//            }
//
//            @Override
//            public void onCancelled(FirebaseError firebaseError) {
//
//            }
//        };


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
        if (id == R.id.nav_lunch) {

            //TODO finalise
            DocumentReference documentReference = CoworkerHelper.getCoworkersCollection().document(Objects.requireNonNull(mAuth.getCurrentUser()).getUid());
            documentReference.get().addOnCompleteListener(task -> {
                if (task.isSuccessful()){
                    DocumentSnapshot document = task.getResult();
                    assert document != null;
                    if (document.exists()) {
                        boolean hasChosen = document.getBoolean("hasChosen");
                        Log.d(TAG, "onComplete: " + hasChosen);

                    }else {
                        Log.d(TAG, "onComplete: No such document");
                    }
                } else {
                    Log.d(TAG, "onComplete: failure.", task.getException());
                }
            });

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
