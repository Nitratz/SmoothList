package com.list.smoothlist.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;

import com.list.smoothlist.R;
import com.list.smoothlist.activity.MainActivity;
import com.list.smoothlist.database.DBManager;

public class SplashScreen extends AppCompatActivity {


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null)
            actionBar.hide();
        DBManager.getInstance(this);

        new Handler().postDelayed(() -> {
            startActivity(new Intent(this, MainActivity.class));
            finish();
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        }, 1000);
    }
}
