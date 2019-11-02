package com.example.theshayds.go4lunch.controller;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;

import com.example.theshayds.go4lunch.R;
import com.example.theshayds.go4lunch.utils.MyPreferences;
import com.google.android.material.appbar.AppBarLayout;

import java.util.Objects;

public class SettingsActivity extends BaseActivity {

    Toolbar toolbar;
    AppBarLayout appBarLayout;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_activity);

        configureToolbar();

        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.pref_content, new MyPreferences())
                .commit();
    }

    private void configureToolbar() {
        appBarLayout = findViewById(R.id.appBarLayout);
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle("Settings");
        Objects.requireNonNull(getSupportActionBar()).setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }
}
