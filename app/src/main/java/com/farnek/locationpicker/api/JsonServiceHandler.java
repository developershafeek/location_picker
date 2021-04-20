package com.farnek.locationpicker.api;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class JsonServiceHandler {

    private static final String BASE_URL = "https://alfabee.pk/Services/";
    public static final String RELATIVE_URL_LOGIN = "login";
    public static final String RELATIVE_URL_UPDATE_LIVE_LOCATION = "update_rider_live_location";

    //Retrofit
    private static HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
    // set your desired log level
    private static OkHttpClient.Builder httpClient = new OkHttpClient.Builder();
    private static Gson gson = new GsonBuilder()
            .disableHtmlEscaping()
            .setLenient()
            .create();
    private static Retrofit.Builder builder = new Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create(gson));
    private static Retrofit retrofit;

    public static com.farnek.locationpicker.api.JsonApiService getJsonApiService() {
        logging.setLevel(HttpLoggingInterceptor.Level.HEADERS);
        httpClient.addInterceptor(logging);
        httpClient.addInterceptor(new Interceptor() {
            @Override
            public Response intercept(Chain chain) throws IOException {
                Request original = chain.request();
                Request request = original.newBuilder()
                        .header("Content-Type", "application/json")
                        .header("User-Agents", "com.alfabee.com")
                        .header("Authtoken", "Alwafaa123")
                        .method(original.method(), original.body())
                        .build();
                return chain.proceed(request);
            }
        });
        retrofit = builder.client(httpClient.build()).build();
        return retrofit.create(com.farnek.locationpicker.api.JsonApiService.class);
    }
}
