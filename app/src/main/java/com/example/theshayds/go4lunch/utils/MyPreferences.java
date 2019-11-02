package com.example.theshayds.go4lunch.utils;

import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.SwitchPreference;

import com.example.theshayds.go4lunch.R;

public class MyPreferences extends PreferenceFragmentCompat {
    private static final String TAG = "MyPreferences";

    private boolean isChecked;

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.preferences, rootKey);

        SwitchPreference switchPreference = findPreference("notifications");

        if (switchPreference != null){
            switchPreference.setOnPreferenceChangeListener((preference, isSwitchChecked) -> {
                isChecked = (Boolean) isSwitchChecked;
                if (isChecked){
                    Toast.makeText(getActivity(), getResources().getString(R.string.notifications_started), Toast.LENGTH_SHORT).show();
                    Log.d(TAG, "onPreferenceChange: start Alarm Receiver");
                } else {
                    Toast.makeText(getActivity(), getResources().getString(R.string.notifications_canceled), Toast.LENGTH_SHORT).show();
                    Log.d(TAG, "onPreferenceChange: cancel Alarm Receiver");
                }
                return true;
            });
        }
    }
}
