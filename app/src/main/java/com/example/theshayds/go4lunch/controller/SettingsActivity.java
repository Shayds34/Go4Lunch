package com.example.theshayds.go4lunch.controller;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.SwitchPreference;

import com.example.theshayds.go4lunch.R;
import com.example.theshayds.go4lunch.utils.NotificationsService;
import com.google.android.material.appbar.AppBarLayout;

import java.util.Calendar;

public class SettingsActivity extends BaseActivity {
    private static final String TAG = "SettingsActivity";

    Toolbar toolbar;
    AppBarLayout appBarLayout;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_activity);

        configureToolbar();

        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.pref_content, new MySettingsFragment())
                .commit();
    }

    private void configureToolbar() {
        appBarLayout = findViewById(R.id.appBarLayout);
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle("Settings");
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    public static class MySettingsFragment extends PreferenceFragmentCompat {
        @Override
        public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
            setPreferencesFromResource(R.xml.preferences, rootKey);
        }
    }

    public void startAlarmReceiver(){
        // Build new alarmManager using ALARM_SERVICE
        Intent notificationIntent = new Intent(getApplicationContext(), NotificationsService.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        Calendar triggerTime = Calendar.getInstance();

        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        alarmManager.setInexactRepeating(AlarmManager.RTC_WAKEUP, triggerTime.getTimeInMillis(), 15000, pendingIntent);
        Toast.makeText(this, "Notification system has been started (24 hours delay).", Toast.LENGTH_LONG).show();

        Log.d(TAG, "startAlarmReceiver: started.");
    }
    
    private void cancelAlarmReceiver(){
        // Get current alarmManager and cancel it.
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        Intent notificationIntent = new Intent(this, NotificationsService.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        
        alarmManager.cancel(pendingIntent);
        Toast.makeText(this, "Notification system has been cancelled.", Toast.LENGTH_LONG).show();

        Log.d(TAG, "cancelAlarmReceiver: cancelled.");
    }
}
