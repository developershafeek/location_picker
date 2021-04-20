package com.farnek.locationpicker.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;

import androidx.appcompat.app.AppCompatActivity;

import com.farnek.locationpicker.R;
import com.farnek.locationpicker.model.LoginResponse.UserDetails;
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
                UserDetails userData = gson.fromJson(json, UserDetails.class);
                if (userData != null) {
                    Intent intent = new Intent(SplashActivity.this, HomeActivity.class);
//                    intent.putExtras(bundle);
                    startActivity(intent);
                } else {
                    startActivity(new Intent(SplashActivity.this, LoginActivity.class));
                }
                finish();
            }
        }, 1000);
    }
}