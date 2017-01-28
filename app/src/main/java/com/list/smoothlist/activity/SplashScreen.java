package com.list.smoothlist.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;

import com.list.smoothlist.R;
import com.list.smoothlist.database.DBManager;

public class SplashScreen extends AppCompatActivity {

    SharedPreferences mShared;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        mShared = getSharedPreferences("com.list.smoothlist", Context.MODE_PRIVATE);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null)
            actionBar.hide();
        DBManager.getInstance(this);

        new Handler().postDelayed(() -> {
            if (!isFirstLaunch())
                startActivity(new Intent(this, MainActivity.class));
            else {
                setLaunched();
                startActivity(new Intent(this, TutorialActivity.class));
            }
            finish();
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        }, 1000);
    }

    private boolean isFirstLaunch() {
        return mShared.getBoolean("firstLaunch", true);
    }

    private void setLaunched() {
        SharedPreferences.Editor edit = mShared.edit();
        edit.putBoolean("firstLaunch", false);
        edit.apply();
    }
}
