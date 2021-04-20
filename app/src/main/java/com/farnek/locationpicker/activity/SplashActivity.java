package com.farnek.locationpicker.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;

import com.farnek.locationpicker.R;
import com.farnek.locationpicker.model.UserData;
import com.farnek.locationpicker.utility.AppConstants;
import com.google.gson.Gson;


public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        setSplashScreen();
    }

    private void setSplashScreen() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                SharedPreferences sharedPreference = getApplicationContext().getSharedPreferences(AppConstants.SHARED_PREF_USER, 0);
                Gson gson = new Gson();
                String json = sharedPreference.getString(AppConstants.USER, "");
                UserData userData = gson.fromJson(json, UserData.class);
                if (userData != null) {
                    Intent intent = new Intent(SplashActivity.this, HomeActivity.class);
//                    intent.putExtras(bundle);
                    startActivity(intent);
                } else {
                    startActivity(new Intent(SplashActivity.this, HomeActivity.class));
                }
                finish();
            }
        }, 1000);
    }
}