package com.example.theshayds.go4lunch.controller;

import android.app.ProgressDialog;

import androidx.annotation.VisibleForTesting;
import androidx.appcompat.app.AppCompatActivity;
import com.example.theshayds.go4lunch.R;

public class BaseActivity extends AppCompatActivity {

    @VisibleForTesting
    public ProgressDialog mProgressDialog;

    public void showProgressDialog() {
        if (mProgressDialog == null){
            mProgressDialog = new ProgressDialog(this);
            mProgressDialog.setMessage(getString(R.string.loading));
            mProgressDialog.setIndeterminate(true);
        }
        mProgressDialog.show();
    }

    public void hideProgressDialog(){
        if (mProgressDialog != null && mProgressDialog.isShowing()){
            mProgressDialog.dismiss();
        }
    }

    @Override
    public void onStop(){
        super.onStop();
        hideProgressDialog();
    }
}

