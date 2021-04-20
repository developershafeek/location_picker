package com.farnek.locationpicker.activity;

import android.app.DatePickerDialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.farnek.locationpicker.R;
import com.farnek.locationpicker.adapter.LocationListAdapter;
import com.farnek.locationpicker.api.JsonServiceHandler;
import com.farnek.locationpicker.model.LocationHistoryModel.DateModel;
import com.farnek.locationpicker.model.LocationHistoryModel.LocationHistoryModel;
import com.farnek.locationpicker.model.LoginResponse.UserDetails;
import com.farnek.locationpicker.model.param.LocationHistoryParam;
import com.github.ybq.android.spinkit.SpinKitView;
import com.google.gson.Gson;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.farnek.locationpicker.utility.AppConstants.USER;
import static com.farnek.locationpicker.utility.AppSharedPreference.getAppPreferences;
import static com.farnek.locationpicker.utility.utility.isNetworkConnected;

public class LocationHistoryActivity extends AppCompatActivity {

    private Toolbar tbHome;
    private EditText etDate;
    private RecyclerView rvList;
    private LocationListAdapter locationListAdapter;
    private DrawerLayout drawerLayout;
    private UserDetails userDetails;
    private List<DateModel> locationHistoryList = new ArrayList<>();
    private SharedPreferences appPreference;
    private SpinKitView spinKit;
    private RelativeLayout rlNoData;
    private String deviceId;
    final Calendar myCalendar = Calendar.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location_history);
    }

    @Override
    protected void onResume() {
        super.onResume();
        appPreference = getAppPreferences(getBaseContext());
        String loginString = appPreference.getString(USER, null);
        Gson gson = new Gson();
        userDetails = gson.fromJson(loginString, UserDetails.class);
        if (userDetails != null) {
            deviceId = userDetails.getDeviceId();
            Log.e("deviceId==>", deviceId);
        }
        initView();
    }

    private void initView() {
        etDate = (EditText) findViewById(R.id.et_date);
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        tbHome = (androidx.appcompat.widget.Toolbar) findViewById(R.id.tb_home);
        rvList = (RecyclerView) findViewById(R.id.rv_list);
        spinKit = (SpinKitView) findViewById(R.id.spin_kit);
        rlNoData = (RelativeLayout) findViewById(R.id.rl_no_data_found);
        String date = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
        etDate.setText(date);
        if (isNetworkConnected(getBaseContext())) {
            locationHistoryList.clear();
            callOnTrackingList();
        } else {
            Toast.makeText(this, "Please check network connection", Toast.LENGTH_SHORT).show();
        }
        DatePickerDialog.OnDateSetListener dateSetListener = new DatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear,
                                  int dayOfMonth) {
                // TODO Auto-generated method stub
                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH, monthOfYear);
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                updateLabel();
            }

        };

        etDate.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                new DatePickerDialog(LocationHistoryActivity.this, dateSetListener, myCalendar
                        .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });
    }

    private void updateLabel() {
        String date = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(myCalendar.getTime());

        etDate.setText(date);
        callOnTrackingList();
    }

    private void callOnTrackingList() {
        spinKit.setVisibility(View.VISIBLE);
        LocationHistoryParam param = getLocationHistoryParam();
        Log.e("LocationHistoryParam==>", new Gson().toJson(param));
        Call<LocationHistoryModel> apiCall = JsonServiceHandler.getJsonApiService().onLocationHistoryList(param);
        apiCall.enqueue(new Callback<LocationHistoryModel>() {
            @Override
            public void onResponse(Call<LocationHistoryModel> call, Response<LocationHistoryModel> response) {
                if (response.code() == 200) {
                    LocationHistoryModel locationHistoryModel = (LocationHistoryModel) response.body();
                    if (locationHistoryModel != null) {
                        if (locationHistoryModel.getCode() == 200) {
                            if (locationHistoryModel.getResponse() != null) {
                                if (locationHistoryModel.getResponse().getStatus()) {
                                    if (locationHistoryModel.getResult() != null) {
                                        if (locationHistoryModel.getResult().getDatelist() != null) {
//                                            if (locationHistoryList.size() == 0) {
                                            locationHistoryList = locationHistoryModel.getResult().getDatelist();
                                            setUpRecyclerView();
//                                            } else {
//                                                List<DateModel> orderDataLimit = locationHistoryModel.getResult().getDatelist();
//                                                if (orderDataLimit != null && orderDataLimit.size() > 0) {
//                                                    for (DateModel orderLmt : orderDataLimit) {
//                                                        locationHistoryList.add(orderLmt);
//                                                    }
                                            locationListAdapter.notifyDataSetChanged();
                                            spinKit.setVisibility(View.GONE);
//                                                }
//                                            }
                                        }
                                    }
                                } else {
                                    spinKit.setVisibility(View.GONE);
                                    Toast.makeText(LocationHistoryActivity.this, locationHistoryModel.getResponse().getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            }
                        }
                    }
                }
            }

            @Override
            public void onFailure(Call<LocationHistoryModel> call, Throwable t) {
            }
        });
    }

    private LocationHistoryParam getLocationHistoryParam() {
        LocationHistoryParam param = null;
        try {
            param = new LocationHistoryParam();
            if (deviceId != null) {
                param.setDevice_id(deviceId);
            }
            param.setDate(etDate.getText().toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return param;
    }


    private void setUpRecyclerView() {
        if (locationHistoryList.size() > 0) {
            rlNoData.setVisibility(View.GONE);
            rvList.setVisibility(View.VISIBLE);
            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getBaseContext(), LinearLayoutManager.VERTICAL, false);
            rvList.setLayoutManager(linearLayoutManager);
            Log.e("ordersList==>", new Gson().toJson(locationHistoryList));
            locationListAdapter = new LocationListAdapter(getBaseContext(), locationHistoryList);
            rvList.setAdapter(locationListAdapter);
            spinKit.setVisibility(View.GONE);
        } else {
            rlNoData.setVisibility(View.VISIBLE);
            rvList.setVisibility(View.GONE);
            spinKit.setVisibility(View.GONE);
        }
    }

}