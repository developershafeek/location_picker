package com.farnek.locationpicker.service;

import android.Manifest;
import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Build;
import android.os.IBinder;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;

import com.farnek.locationpicker.activity.HomeActivity;
import com.farnek.locationpicker.api.JsonServiceHandler;
import com.farnek.locationpicker.model.CommonResponse;
import com.farnek.locationpicker.model.UserData;
import com.farnek.locationpicker.model.param.UpdateLiveLocationParam;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;
import com.google.gson.Gson;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.farnek.locationpicker.utility.AppConstants.USER;
import static com.farnek.locationpicker.utility.AppSharedPreference.getAppPreferences;
import static com.farnek.locationpicker.utility.utility.isNetworkConnected;


public class LocationUpdateService extends Service {

    //region data
    private FusedLocationProviderClient mFusedLocationClient;
    private String currentLocLatitude;
    private String currentLocLongitude;
    private LatLng latLng;
    private SharedPreferences appPreference;
    private UserData userData;
    private String deviceId;
    //endregion

    @Override
    public void onCreate() {
        super.onCreate();
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        LocationRequest mLocationRequest = LocationRequest.create();
        mLocationRequest.setInterval(3000);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Log.e("noRequest==>", "YES");
            return;
        }
        mFusedLocationClient.requestLocationUpdates(mLocationRequest, mLocationCallback, Looper.myLooper());
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    LocationCallback mLocationCallback = new LocationCallback() {
        @Override
        public void onLocationResult(LocationResult locationResult) {
            List<Location> locationList = locationResult.getLocations();
            if (locationList.size() > 0) {
                //The last location in the list is the newest

                Location location = locationList.get(locationList.size() - 1);
                latLng = new LatLng(location.getLatitude(), location.getLongitude());
                currentLocLatitude = String.valueOf(location.getLatitude());
                Log.e("currentLocLatitude==>>", new Gson().toJson(currentLocLatitude));
                currentLocLongitude = String.valueOf(location.getLongitude());
                Log.e("currentLocLongitude=>>", new Gson().toJson(currentLocLongitude));

                appPreference = getAppPreferences(getBaseContext());
                String loginString = appPreference.getString(USER, null);
                Gson gson = new Gson();
                userData = gson.fromJson(loginString, UserData.class);
                if (userData != null) {
                    deviceId = userData.getDevice_id();
                    Log.e("deviceId==>", deviceId);
                }

                UpdateLiveLocationParam updateLocation = new UpdateLiveLocationParam();
                if (currentLocLatitude != null)
                    updateLocation.setLatitude(currentLocLatitude);
                if (currentLocLongitude != null)
                    updateLocation.setLongitude(currentLocLongitude);
                if (deviceId != null)
                    updateLocation.setDevice_id(deviceId);

                if (deviceId != null) {
                    // Location updates
                    if (isNetworkConnected(getBaseContext())) {
                        callUpdateLiveLocation(updateLocation);
                    }
                    else {
                        Toast.makeText(com.farnek.locationpicker.service.LocationUpdateService.this, "Please check your internet connection", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        }
    };

    private void callUpdateLiveLocation(UpdateLiveLocationParam updateLocation) {
        Log.e("updateLocation===>", new Gson().toJson(updateLocation));
        Call<CommonResponse> apiCall = JsonServiceHandler.getJsonApiService().updateLiveLocation(updateLocation);

        apiCall.enqueue(new Callback<CommonResponse>() {
            @Override
            public void onResponse(Call<CommonResponse> call, Response<CommonResponse> response) {
                if (response.code() == 200) {
                    CommonResponse commonResponse = (CommonResponse) response.body();
                    if (commonResponse != null) {
                        if (commonResponse.getCode() == 200) {
                            if (commonResponse.getResponse() != null) {
                                if (commonResponse.getResponse().getStatus()) {
                                    Log.e("UpdateLocation===>>", "Yes");
                                }
                            }
                        }
                    }
                }
            }

            @Override
            public void onFailure(Call<CommonResponse> call, Throwable t) {
                Toast.makeText(com.farnek.locationpicker.service.LocationUpdateService.this, "Something went wrong in location update", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
