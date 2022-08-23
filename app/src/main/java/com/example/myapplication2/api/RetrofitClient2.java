package com.example.myapplication2.api;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitClient2 {
    private static RetrofitClient2 instance = null;
    private static RetrofitAPI retrofitAPI;
    private final static String BASE_URL = "http://13.125.254.29:8080/";

    private RetrofitClient2(){
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        retrofitAPI = retrofit.create(RetrofitAPI.class);
    }

    public static RetrofitClient2 getInstance(){
        if (instance==null){
            instance = new RetrofitClient2();
        }
        return instance;
    }

    public static RetrofitAPI getRetrofitAPI(){
        return retrofitAPI;
    }
}
