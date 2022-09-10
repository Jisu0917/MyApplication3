package com.example.myapplication2.api;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitClient3 {
    private static RetrofitClient3 instance = null;
    private static RetrofitAPI retrofitAPI;
    private final static String BASE_URL = "http://15.164.229.184:8080/";  //Client2, RetrofitAPI.java askAnalysis path도 수정해야함!

    private RetrofitClient3(){
        // timeout setting 해주기
        OkHttpClient okHttpClient = new OkHttpClient().newBuilder()
                .connectTimeout(600, TimeUnit.SECONDS)
                .readTimeout(600, TimeUnit.SECONDS)
                .writeTimeout(600, TimeUnit.SECONDS)
                .build();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .client(okHttpClient)
                .build();
        retrofitAPI = retrofit.create(RetrofitAPI.class);
    }

    public static RetrofitClient3 getInstance(){
        if (instance==null){
            instance = new RetrofitClient3();
        }
        return instance;
    }

    public static RetrofitAPI getRetrofitAPI(){
        return retrofitAPI;
    }
}
