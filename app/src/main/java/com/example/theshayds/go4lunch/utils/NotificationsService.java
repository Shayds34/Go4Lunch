package com.example.theshayds.go4lunch.utils;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.util.Log;
import com.example.theshayds.go4lunch.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.Objects;

public class NotificationsService extends BroadcastReceiver {
    private static final String TAG = "NotificationsService";

    public static final int NOTIFICATION_ID = 1;

    FirebaseAuth mAuth = FirebaseAuth.getInstance();

    String placeName, placeAddress;
    ArrayList<String> users;

    CollectionReference coworkerHelper;

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d(TAG, "onReceive.");

        FirebaseUser currentUser = mAuth.getCurrentUser();

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        boolean notificationsActivated = sharedPreferences.getBoolean("notifications", false);

            // If Notifications are ON, notify the user with Place + People
            if (notificationsActivated){
                coworkerHelper = CoworkerHelper.getCoworkersCollection();

                assert currentUser != null;
                DocumentReference documentReference = CoworkerHelper.getCoworkersCollection().document(currentUser.getUid());
                documentReference.get().addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        assert document != null;
                        if (document.exists()) {

                            placeName = document.getString("placeName");
                            Log.d(TAG, "onReceive: current user's choice: " + placeName);
                            placeAddress = document.getString("placeAddress");

                            CoworkerHelper.getCoworkersCollection().whereEqualTo("placeName", placeName).get().addOnCompleteListener(task1 -> {
                                if (task1.isSuccessful()){
                                    users = new ArrayList<>();
                                    for (QueryDocumentSnapshot document1 : Objects.requireNonNull(task1.getResult())){
                                        if (!Objects.requireNonNull(document1.getString("userName")).equals(currentUser.getDisplayName())){
                                            users.add(document1.getString("userName"));
                                        }
                                    }
                                    Log.d(TAG, "onComplete: users: " + users.toString());

                                    String message = TextUtils.join(", ", users);

                                    Notification.Builder builder = new Notification.Builder(context);
                                    Notification notification = builder.setSmallIcon(R.drawable.go4launch_icon)
                                            .setContentTitle(context.getResources().getString(R.string.notification_title) + placeName)
                                            .setStyle(new Notification.BigTextStyle().bigText(context.getResources().getString(R.string.notification_big_text) + message))
                                            .setAutoCancel(true)
                                            .build();

                                    NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
                                    notificationManager.notify(NOTIFICATION_ID, notification);

                                    // Reset user's choice
                                    CoworkerHelper.updatePlace(currentUser.getUid(), false, "", "");
                                }
                            });
                        } else {
                            Log.d(TAG, "onComplete: No such document");
                        }
                    } else {
                        Log.d(TAG, "onComplete: failure.", task.getException());
                    }
                });
            } else {
                // Reset User's choice
                if (currentUser != null){
                    CoworkerHelper.updatePlace(currentUser.getUid(), false, "", "");
                }
            }
        }
}
