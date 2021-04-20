package com.farnek.locationpicker.api;



import com.farnek.locationpicker.model.CommonResponse;
import com.farnek.locationpicker.model.LoginResponse.LoginResponse;
import com.farnek.locationpicker.model.param.LoginParam;
import com.farnek.locationpicker.model.param.UpdateLiveLocationParam;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

import static com.farnek.locationpicker.api.JsonServiceHandler.RELATIVE_URL_LOGIN;
import static com.farnek.locationpicker.api.JsonServiceHandler.RELATIVE_URL_UPDATE_LIVE_LOCATION;


public interface JsonApiService {

    // Login
    @POST(RELATIVE_URL_LOGIN)
    Call<LoginResponse> login(@Body LoginParam loginParam);

    @POST(RELATIVE_URL_UPDATE_LIVE_LOCATION)
    Call<CommonResponse> updateLiveLocation(@Body UpdateLiveLocationParam updateLocation);
}
