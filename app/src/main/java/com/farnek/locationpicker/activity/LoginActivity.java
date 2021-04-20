package com.farnek.locationpicker.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.farnek.locationpicker.R;
import com.farnek.locationpicker.api.JsonServiceHandler;
import com.farnek.locationpicker.model.LoginResponse.LoginResponse;
import com.farnek.locationpicker.model.LoginResponse.UserDetails;
import com.farnek.locationpicker.model.param.LoginParam;
import com.farnek.locationpicker.utility.AppConstants;
import com.github.ybq.android.spinkit.SpinKitView;
import com.google.android.material.snackbar.Snackbar;
import com.google.gson.Gson;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.farnek.locationpicker.utility.utility.isNetworkConnected;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    private EditText etName;
    private Button btnLogin;
    private RelativeLayout parentLayout;
    private SpinKitView spinKit;
    private UserDetails userDetails;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        initView();
    }

    private void initView() {
        parentLayout = (RelativeLayout) findViewById(R.id.rl_loginpage);
        etName = (EditText) findViewById(R.id.et_email_login);
        btnLogin = (Button) findViewById(R.id.btn_login);
        spinKit = (SpinKitView) findViewById(R.id.spin_kit);
        registerListener();
    }

    private void registerListener() {
        btnLogin.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_login:
                if (isNetworkConnected(getBaseContext())) {
                    if (validate()) {
                        callApiLogin();
                    }
                } else {
                    Toast.makeText(this, "Please check your internet connection", Toast.LENGTH_SHORT).show();
                }
//                startActivity(new Intent(this,HomeActivity.class));
                break;
        }
    }

    private void callApiLogin() {
        spinKit.setVisibility(View.VISIBLE);
        btnLogin.setVisibility(View.GONE);
        final LoginParam loginParam = getLoginParam();
        Log.e("LoginRequest=>", new Gson().toJson(loginParam));
        Call<LoginResponse> apiCall = JsonServiceHandler.getJsonApiService().login(loginParam);
        apiCall.enqueue(new Callback<LoginResponse>() {
            @Override
            public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {
                spinKit.setVisibility(View.GONE);
                btnLogin.setVisibility(View.VISIBLE);
                if (response.code() == 200) {
                    LoginResponse loginResponse = (LoginResponse) response.body();
                    if (loginResponse != null) {
                        if (loginResponse.getCode() == 200) {
                            if (loginResponse.getResponse() != null) {
                                if (loginResponse.getResponse().getStatus()) {
                                    if (loginResponse.getResult() != null) {
                                        if (loginResponse.getResult().getUser_details() != null) {
                                            userDetails = loginResponse.getResult().getUser_details();
                                            if (userDetails != null) {
                                                SharedPreferences sharedPreferences = getSharedPreferences(AppConstants.SHARED_PREF_USER, 0);
                                                Gson gson = new Gson();
                                                String json = gson.toJson(userDetails);
                                                sharedPreferences.edit().putString(AppConstants.USER, json).commit();
                                                startActivity(new Intent(LoginActivity.this, HomeActivity.class));
                                                finish();
                                            }
                                        }
                                    }
                                } else {
                                    Snackbar.make(parentLayout, loginResponse.getResponse().getMessage(), Snackbar.LENGTH_LONG).show();
                                }
                            }
                        } else {
                            Snackbar.make(parentLayout, loginResponse.getResponse().getMessage(), Snackbar.LENGTH_LONG).show();
                        }
                    }
                }
            }

            @Override
            public void onFailure(Call<LoginResponse> call, Throwable t) {
                spinKit.setVisibility(View.GONE);
                btnLogin.setVisibility(View.VISIBLE);
                Snackbar.make(parentLayout, "Something went wrong try later", Snackbar.LENGTH_LONG).show();
            }
        });
    }

    private LoginParam getLoginParam() {
        LoginParam loginParam = null;
        try {
            loginParam = new LoginParam();
            if (!etName.getText().toString().isEmpty()) {
                loginParam.setUser_name(etName.getText().toString());
            }

            String android_id = Settings.Secure.getString(this.getContentResolver(),
                    Settings.Secure.ANDROID_ID);
            if (android_id != null) {
                loginParam.setDevice_id(android_id);
            }
            loginParam.setFrequency(3);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return loginParam;
    }

    private boolean validate() {
        boolean isValid = true;
        if (etName.getText().toString().isEmpty()) {
            Snackbar.make(parentLayout, "Please enter name", Snackbar.LENGTH_LONG).show();
            isValid = false;
        }
        return isValid;
    }
}