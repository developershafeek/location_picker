package com.farnek.locationpicker.service;

import android.Manifest;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.IBinder;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;

import com.farnek.locationpicker.R;
import com.farnek.locationpicker.activity.HomeActivity;
import com.farnek.locationpicker.api.JsonServiceHandler;
import com.farnek.locationpicker.model.CommonResponse;
import com.farnek.locationpicker.model.LoginResponse.UserDetails;
import com.farnek.locationpicker.model.param.UpdateLiveLocationParam;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;
import com.google.gson.Gson;

import java.util.List;

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
    private UserDetails userDetails;
    private String deviceId;
    //endregion

    @Override
    public void onCreate() {
        super.onCreate();
        appPreference = getAppPreferences(getBaseContext());
        String loginString = appPreference.getString(USER, null);
        Gson gson = new Gson();
        userDetails = gson.fromJson(loginString, UserDetails.class);
        if (userDetails != null) {
            deviceId = userDetails.getDeviceId();
            Log.e("deviceId==>", deviceId);
        }
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        LocationRequest mLocationRequest = LocationRequest.create();
        mLocationRequest.setInterval((userDetails!=null&&userDetails.getFrequency()!=null )? Integer.parseInt(userDetails.getFrequency()) *1000  : 3000);
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
    @RequiresApi(api = Build.VERSION_CODES.M)
    private void StartForeground() {
        Intent intent = new Intent(this, HomeActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0 /* Request code */, intent, PendingIntent.FLAG_ONE_SHOT);

        String CHANNEL_ID = "channel_location";
        String CHANNEL_NAME = "Foreground Service Channel";

        NotificationCompat.Builder builder = null;
        NotificationManager notificationManager = (NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, CHANNEL_NAME, NotificationManager.IMPORTANCE_DEFAULT);
            channel.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);
            notificationManager.createNotificationChannel(channel);
            builder = new NotificationCompat.Builder(getApplicationContext(), CHANNEL_ID);
            builder.setChannelId(CHANNEL_ID);
            builder.setBadgeIconType(NotificationCompat.BADGE_ICON_NONE);
        } else {
            builder = new NotificationCompat.Builder(getApplicationContext(), CHANNEL_ID);
        }

        builder.setContentTitle("Farnek");
        builder.setContentText("Location service is running...");
        Uri notificationSound = RingtoneManager.getActualDefaultRingtoneUri(this, RingtoneManager.TYPE_NOTIFICATION);
        builder.setSound(notificationSound);
        builder.setAutoCancel(true);
        builder.setSmallIcon(R.drawable.app_logo);
        builder.setContentIntent(pendingIntent);
        Notification notification = builder.build();
        startForeground(101, notification);
    }
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            StartForeground();
        }
        return START_STICKY;
    }
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
    @Override
    public void onDestroy() {
        if (mFusedLocationClient != null) {
            mFusedLocationClient.removeLocationUpdates(mLocationCallback);
            Log.e("TAG_LOCATION", "Location Update Callback Removed");
        }
        super.onDestroy();
    }
}
