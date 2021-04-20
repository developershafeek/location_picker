package com.farnek.locationpicker.activity;

import android.Manifest;
import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.farnek.locationpicker.R;
import com.farnek.locationpicker.model.LoginResponse.UserDetails;
import com.farnek.locationpicker.service.LocationUpdateService;
import com.farnek.locationpicker.utility.AppConstants;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.gson.Gson;

import static com.farnek.locationpicker.utility.AppSharedPreference.getAppPreferences;
import static com.farnek.locationpicker.utility.utility.isLocationEnabled;

public class HomeActivity extends AppCompatActivity implements View.OnClickListener, OnMapReadyCallback {
    private static final int PERMISSION_REQUEST_CODE = 1;
    private ImageView ivNav;
    private FusedLocationProviderClient mFusedLocationClient;
    private GoogleMap gMap;
    private TextView tvName;
    private RelativeLayout rlHome;
    private RelativeLayout rlLogOut;
    private RelativeLayout rlLocationTracking;
    private DrawerLayout drawerLayout;
    private RelativeLayout rlMap;
    private TextView tvContent;
    private Dialog dialogLogOut;
    private Button btnYes;
    private Button btnNo;
    private Button btnGo;
    private SharedPreferences appPreference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        initViews();
    }

    private void initViews() {

        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        ivNav = (ImageView) findViewById(R.id.iv_nav);
        rlMap = (RelativeLayout) findViewById(R.id.rl_map);
        tvName = (TextView) findViewById(R.id.tv_name);
        rlHome = (RelativeLayout) findViewById(R.id.rl_home);
        rlLocationTracking = (RelativeLayout) findViewById(R.id.rl_my_location);
        rlLogOut = (RelativeLayout) findViewById(R.id.rl_log_out);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);


        registerListeners();


    }

    @Override
    protected void onResume() {
        super.onResume();
        appPreference = getAppPreferences(getBaseContext());
        Gson gson = new Gson();
        String json = appPreference.getString(AppConstants.USER, "");
        UserDetails userDetails = gson.fromJson(json, UserDetails.class);
        if (userDetails != null && userDetails.getUserName() != null) {
            tvName.setText(userDetails.getUserName().toString());
        }
    }

    private void initLocationServices() {

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            requestPermission();
        } else {
            enableUserLocation();
            zoomToUserLocation();
            Intent intent = new Intent(HomeActivity.this, LocationUpdateService.class);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                startForegroundService(intent);
            } else {
                startService(intent);
            }
        }
    }

    private void enableUserLocation() {
        try {
            gMap.setMyLocationEnabled(true);
        } catch (SecurityException e) {
            e.printStackTrace();
        }
    }

    private void zoomToUserLocation() {
        try {
            Task<Location> locationTask = mFusedLocationClient.getLastLocation();
            locationTask.addOnSuccessListener(new OnSuccessListener<Location>() {
                @Override
                public void onSuccess(Location location) {
                    LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
                    gMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 17));
//                    gMap.addMarker(new MarkerOptions().position(latLng));
                }
            });
        } catch (SecurityException e) {
            e.printStackTrace();
        }

    }

    private void showLocationSettings(final int i) {

        final Dialog dialogobj = new Dialog(HomeActivity.this);
        dialogobj.setContentView(R.layout.dialog_device_location_access);
        dialogobj.setCanceledOnTouchOutside(false);
        assert dialogobj.getWindow() != null;
        dialogobj.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        btnGo = (Button) dialogobj.findViewById(R.id.btn_go);
        tvContent = (TextView) dialogobj.findViewById(R.id.tv_content);
        if (i == 1) {
            tvContent.setText("Your Device Location is OFF.Go To Settings,Turn it ON");
        } else if (i == 2) {
            tvContent.setText("Turn on Internet");
        }
        btnGo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (i == 1) {
                    Intent myIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                    startActivity(myIntent);
                } else if (i == 2) {
                    Intent myIntent = new Intent(Settings.ACTION_WIFI_SETTINGS);
                    startActivity(myIntent);
                }
                dialogobj.dismiss();
            }
        });
        dialogobj.show();
    }

    private void registerListeners() {

        ivNav.setOnClickListener(this);
        rlHome.setOnClickListener(this);
        rlLocationTracking.setOnClickListener(this);
        rlLogOut.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {

        switch (view.getId()) {

            case R.id.rl_home:
                drawerLayout.closeDrawer(GravityCompat.START);
                break;


            case R.id.rl_my_location:
                startActivity(new Intent(HomeActivity.this, LocationHistoryActivity.class));
                drawerLayout.closeDrawer(GravityCompat.START);
                break;


            case R.id.rl_log_out:
                showDialogForLogOut();
                drawerLayout.closeDrawer(GravityCompat.START);
                break;

            case R.id.iv_nav:
                if (drawerLayout.isDrawerOpen(GravityCompat.END)) {
                    drawerLayout.closeDrawer(GravityCompat.END);
                } else
                    drawerLayout.openDrawer(GravityCompat.START);
                break;
            default:
                break;
        }
    }

    private void showDialogForLogOut() {

        dialogLogOut = new Dialog(HomeActivity.this);
        dialogLogOut.setContentView(R.layout.dialog_log_out);
        assert dialogLogOut.getWindow() != null;
        dialogLogOut.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        btnYes = (Button) dialogLogOut.findViewById(R.id.btn_yes);
        btnNo = (Button) dialogLogOut.findViewById(R.id.btn_no);
        dialogLogOut.show();
        btnYes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                logoutAction();
            }
        });
        btnNo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialogLogOut.dismiss();
            }
        });
    }

    private void logoutAction() {
        Intent myService = new Intent(HomeActivity.this, LocationUpdateService.class);
        stopService(myService);
        appPreference.edit().clear().apply();
        appPreference.edit().commit();
        Intent intent = new Intent(this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    private void requestPermission() {
        ActivityCompat.requestPermissions(HomeActivity.this, new String[]{
                Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, PERMISSION_REQUEST_CODE);
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[], @NonNull int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_REQUEST_CODE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    enableUserLocation();
                    zoomToUserLocation();
                    Intent intent = new Intent(HomeActivity.this, LocationUpdateService.class);
                    startService(intent);
                } else if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_DENIED) {
                    requestPermission();
                    boolean showRationale = shouldShowRequestPermissionRationale(permissions[0]);
                    if (!showRationale) {
                        break;
                    } else if (permissions.length > 0 && Manifest.permission.WRITE_EXTERNAL_STORAGE.equals(permissions[0])) {
                        requestPermission();
                    }
                }
                break;
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        gMap = googleMap;
        if (!isLocationEnabled(this)) {
            showLocationSettings(1);
        } else {
            initLocationServices();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(dialogLogOut!=null&& dialogLogOut.isShowing()) {
            dialogLogOut.dismiss();
        }
        if(dialogLogOut!=null&& dialogLogOut.isShowing()) {

        }

    }
}