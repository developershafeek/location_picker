package com.farnek.locationpicker.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.farnek.locationpicker.R;
import com.farnek.locationpicker.api.JsonServiceHandler;
import com.farnek.locationpicker.model.LoginResponse.LoginResponse;
import com.farnek.locationpicker.model.UserData;
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
    private UserData userData;
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
                }
                else {
                    Toast.makeText(this, "Please check your internet connection", Toast.LENGTH_SHORT).show();
                }
//                startActivity(new Intent(this,HomeActivity.class));
                break;
        }
    }

    private void callApiLogin() {
        spinKit.setVisibility(View.VISIBLE);
        final LoginParam loginParam = getLoginParam();
        Log.e("LoginRequest=>", new Gson().toJson(loginParam));
        Call<LoginResponse> apiCall = JsonServiceHandler.getJsonApiService().login(loginParam);
        apiCall.enqueue(new Callback<LoginResponse>() {
            @Override
            public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {
                if (response.code() == 200){
                    LoginResponse loginResponse = (LoginResponse) response.body();
                    if (loginResponse != null){
                        if (loginResponse.getCode() == 200){
                            if (loginResponse.getResponse() != null){
                                if (loginResponse.getResponse().getStatus()){
                                    if (loginResponse.getResult() != null){
                                        if (loginResponse.getResult().getUser_details() != null){
                                            userData = loginResponse.getResult().getUser_details();

                                            if(userData!=null){

                                                // store customer in the shared preference
                                                SharedPreferences sharedPreferences = getSharedPreferences(AppConstants.SHARED_PREF_USER, 0);
                                                Gson gson = new Gson();
                                                String json = gson.toJson(userData);
                                                sharedPreferences.edit().putString(AppConstants.USER, json).commit();
//                                                AppSharedPreference.putString(LoginActivity.this, AppSharedPreference.PREF_KEY.LOG_IN, json);

                                                startActivity(new Intent(LoginActivity.this, HomeActivity.class));
                                                finish();
                                            }
                                        }
                                    }
                                }
                                else {
                                    spinKit.setVisibility(View.GONE);
                                    Snackbar.make(parentLayout, loginResponse.getResponse().getMessage(), Snackbar.LENGTH_LONG).show();
                                }
                            }
                        }
                        else {
                            spinKit.setVisibility(View.GONE);
                            Snackbar.make(parentLayout, loginResponse.getResponse().getMessage(), Snackbar.LENGTH_LONG).show();
                        }
                    }
                }
            }

            @Override
            public void onFailure(Call<LoginResponse> call, Throwable t) {
                spinKit.setVisibility(View.GONE);
                Snackbar.make(parentLayout, "Something went wrong try later", Snackbar.LENGTH_LONG).show();
            }
        });
    }

    private LoginParam getLoginParam() {
        LoginParam loginParam = null;
        try {
            loginParam = new LoginParam();
            if (!etName.getText().toString().isEmpty()) {
                loginParam.setName(etName.getText().toString());
            }

            String deviceToken = "123456789";
            if (deviceToken != null) {
                loginParam.setDevice_id(deviceToken);
            }
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