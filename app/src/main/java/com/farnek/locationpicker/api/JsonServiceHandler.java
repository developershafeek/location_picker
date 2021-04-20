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

    private static final String BASE_URL = "https://interview.farnek.com/shafeek/webservice/Services/";
    public static final String RELATIVE_URL_LOGIN = "login";
    public static final String RELATIVE_URL_UPDATE_LOCATION = "update_location";
    public static final String RELATIVE_URL_LOCATION_HISTORY = "date_location";
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
                        .method(original.method(), original.body())
                        .build();
                return chain.proceed(request);
            }
        });
        retrofit = builder.client(httpClient.build()).build();
        return retrofit.create(com.farnek.locationpicker.api.JsonApiService.class);
    }
}
