package com.example.theshayds.go4lunch.controller;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.example.theshayds.go4lunch.R;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.auth.TwitterAuthProvider;
import com.twitter.sdk.android.core.Callback;
import com.twitter.sdk.android.core.Result;
import com.twitter.sdk.android.core.Twitter;
import com.twitter.sdk.android.core.TwitterAuthConfig;
import com.twitter.sdk.android.core.TwitterConfig;
import com.twitter.sdk.android.core.TwitterException;
import com.twitter.sdk.android.core.TwitterSession;
import com.twitter.sdk.android.core.identity.TwitterLoginButton;

public class  AuthenticationActivity extends BaseActivity implements View.OnClickListener {

    //region {Initialization}
    public static final String TAG = "AuthenticationActivity";
    public static final int RC_SIGN_IN = 9001;

    // Check Network status
    // private NetworkStatus mNetworkStatus;

    // Twitter Sign In
    private TwitterLoginButton mTwitterLoginButton;

    // Google Sign In
    private GoogleSignInClient mGoogleSignInClient;

    // Facebook Sign In
    private CallbackManager mCallbackManager;

    // Declare views
    private EditText mEmailField;
    private EditText mPasswordField;

    // Declare Firebase Authentication
    private FirebaseAuth mAuth;

    //endregion

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Configure Twitter SDK
        TwitterAuthConfig authConfig = new TwitterAuthConfig(
                getString(R.string.CONSUMER_KEY),
                getString(R.string.CONSUMER_SECRET));
        TwitterConfig twitterConfig = new TwitterConfig.Builder(this)
                .twitterAuthConfig(authConfig)
                .build();

        Twitter.initialize(twitterConfig);

        setContentView(R.layout.activity_authentication);

        // BindViews
        mEmailField = findViewById(R.id.fieldEmail);
        mPasswordField = findViewById(R.id.fieldPassword);

        findViewById(R.id.create_with_email).setOnClickListener(this);
        findViewById(R.id.sign_in_with_email).setOnClickListener(this);
        findViewById(R.id.sign_in_with_google).setOnClickListener(this);
        findViewById(R.id.sign_in_with_facebook).setOnClickListener(this);
        findViewById(R.id.sign_in_with_twitter).setOnClickListener(this);

        // Get Firebase instance
        FirebaseApp.initializeApp(this);
        mAuth = FirebaseAuth.getInstance();

        // Get CurrentUser to pass Sign In / Log In if User was already signed/logged in.
        FirebaseUser mCurrentUser = mAuth.getCurrentUser();

        // If user is already logged in from previous session.
        if (mCurrentUser != null) {
            Intent mIntent = new Intent(AuthenticationActivity.this, MainActivity.class);
            startActivity(mIntent);
            finish();
        } else {
            // Configure Google Sign In
            GoogleSignInOptions mGoogleSignInOptions = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                    .requestIdToken(getString(R.string.default_web_client_id))
                    .requestEmail()
                    .build();
            mGoogleSignInClient = GoogleSignIn.getClient(this, mGoogleSignInOptions);

            // Configure Facebook Sign In
            mCallbackManager = CallbackManager.Factory.create();
            LoginButton mLoginButton = findViewById(R.id.sign_in_with_facebook);
            mLoginButton.setHeight(47);
            mLoginButton.setPermissions("email", "public_profile");
            mLoginButton.registerCallback(mCallbackManager, new FacebookCallback<LoginResult>() {
                @Override
                public void onSuccess(LoginResult loginResult) {
                    Log.d(TAG, "facebook:onSuccess " + loginResult);
                    firebaseAuthWithFacebook(loginResult.getAccessToken());
                }

                @Override
                public void onCancel() {
                    Log.d(TAG, "facebook:onCancel");
                }

                @Override
                public void onError(FacebookException error) {
                    Log.d(TAG, "facebook:onError", error);
                }
            });

            // Configure Twitter Sign In
            mTwitterLoginButton = findViewById(R.id.sign_in_with_twitter);
            mTwitterLoginButton.setTextSize(14);
            mTwitterLoginButton.setCallback(new Callback<TwitterSession>() {
                @Override
                public void success(Result<TwitterSession> result) {
                    firebaseAuthWithTwitter(result.data);
                }

                @Override
                public void failure(TwitterException exception) {

                }
            });
        }
    }

    //region [Email and Password Authentication]
    private void createAccount(String email, String password){
        Log.d(TAG, "create account " + email);
        if(!validateForm()){
            return;
        }

        showProgressDialog();

        // Start create_user_with_email
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()){
                        // Sign in success, update UI with signed user's information
                        Log.d(TAG, "createUserWithEmail:success");
                        FirebaseUser mUser = mAuth.getCurrentUser();
                        assert mUser != null;
                        Toast.makeText(AuthenticationActivity.this, "Your account with email: " + mUser.getEmail() + " has been created. ", Toast.LENGTH_SHORT).show();

                        startMainActivity();
                    } else {
                        // If sign in fails, display a message to the user.
                        Log.w(TAG, "createUserWithEmail:failure", task.getException());
                        Toast.makeText(AuthenticationActivity.this, "Authentication Failed.", Toast.LENGTH_LONG).show();
                    }

                    hideProgressDialog();
                });
    }

    private void signInWithEmail(String email, String password){
        Log.d(TAG, "signInWithEmail: " + email);
        if(!validateForm()){
            return;
        }

        showProgressDialog();

        // Start sign_in_with_email
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()){
                        // Sign in success, update UI with the signed-in user's information.
                        Log.d(TAG, "signInWithEmail:success");

                        startMainActivity();
                    } else {
                        // If sign in fails, display a message to the user.
                        Log.w(TAG, "signInWithEmail:failure", task.getException());
                        Toast.makeText(AuthenticationActivity.this, "Authentication failed.", Toast.LENGTH_LONG).show();
                    }

                    // Start Exclude
                    if(!task.isSuccessful()){
                        Toast.makeText(AuthenticationActivity.this, R.string.auth_failed, Toast.LENGTH_SHORT).show();
                    }
                    hideProgressDialog();
                    // End Exclude
                });
    }

    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    private boolean validateForm() {
        boolean valid = true;

        String email = mEmailField.getText().toString();
        if (TextUtils.isEmpty(email)){
            mEmailField.setError("Required.");
            valid = false;
        } else {
            mEmailField.setText(null);
        }

        String password = mPasswordField.getText().toString();
        if (TextUtils.isEmpty(password)){
            mPasswordField.setError("Required.");
            valid = false;
        } else {
            mPasswordField.setText(null);
        }
        return valid;
    }
    //endregion

    //region [Google Authentication]
    private void firebaseAuthWithGoogle(GoogleSignInAccount account){
        Log.d(TAG, "firebaseAuthWithGoogle:" + account.getId());

        AuthCredential mCredential = GoogleAuthProvider.getCredential(account.getIdToken(), null);
        mAuth.signInWithCredential(mCredential)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()){
                        // Sign in success, update Ui with the signed-in user's information
                        Log.d(TAG, "signInWithGoogle: success");

                        startMainActivity();
                    } else {
                        // If sign in fails, display a message to user.
                        Log.w(TAG, "signInWithGoogle:failure", task.getException());
                        Snackbar.make(findViewById(R.id.main_layout), "Authentication Failed.", Snackbar.LENGTH_SHORT).show();
                    }
                    hideProgressDialog();
                });
    }

    // Start signIn
    private void signInWithGoogle(){
            Intent signInIntent = mGoogleSignInClient.getSignInIntent();
            startActivityForResult(signInIntent, RC_SIGN_IN);
    }
   //endregion

    //region [Facebook Authentication]
    private void firebaseAuthWithFacebook(AccessToken accessToken){
        Log.d(TAG, "signInWithFacebookToken: " + accessToken);
        showProgressDialog();

        AuthCredential mCredential = FacebookAuthProvider.getCredential(accessToken.getToken());
        mAuth.signInWithCredential(mCredential)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()){
                        Log.d(TAG, "signInWithFacebook:success");

                        startMainActivity();
                    } else {
                        // If sign in fails, display a message to user.
                        Log.w(TAG, "signInWithFacebook:failure", task.getException());
                        Snackbar.make(findViewById(R.id.main_layout), "Authentication Failed.", Snackbar.LENGTH_SHORT).show();
                    }
                    hideProgressDialog();
                });
    }
    //endregion

    //region [Twitter Authentication]
    private void firebaseAuthWithTwitter(TwitterSession session){
        Log.d(TAG, "firebaseAuthWithTwitter: ");

        AuthCredential authCredential = TwitterAuthProvider.getCredential(session.getAuthToken().token,
                session.getAuthToken().secret);
        
        mAuth.signInWithCredential(authCredential)
                .addOnCompleteListener(this, task -> {
                    Log.d(TAG, "onComplete: ");

                    startMainActivity();
                })
                .addOnFailureListener(this, e -> Log.d(TAG, "onFailure: "));
    }
    //endregion

    //region [Start onActivityResult]
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        mTwitterLoginButton.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> mTask = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // Google Sign In was successful, authentication with Firebase
                GoogleSignInAccount account = mTask.getResult(ApiException.class);
                assert account != null;
                firebaseAuthWithGoogle(account);
            } catch (ApiException e) {
                // Google Sign In Failed, update UI.
                Log.w(TAG, "Google sign in failed.", e);
            }
        } else {
            Log.w(TAG, "Facebook sign in success.");
            mCallbackManager.onActivityResult(requestCode, resultCode, data);
        }
    }
    //endregion

    // Start Activity
    private void startMainActivity(){
        Intent mIntent = new Intent(AuthenticationActivity.this, MainActivity.class);
        startActivity(mIntent);
        finish();
    }

    @Override
    public void onClick(View v) {
        int i = v.getId();
            if (i == R.id.create_with_email){
                createAccount(mEmailField.getText().toString(), mPasswordField.getText().toString());
            } else if (i == R.id.sign_in_with_email){
                signInWithEmail(mEmailField.getText().toString(), mPasswordField.getText().toString());
            } else if (i == R.id.sign_in_with_google) {
                signInWithGoogle();
            }
    }
}