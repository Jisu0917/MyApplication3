package com.example.myapplication2.api;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitClient4 {
    private static RetrofitClient4 instance = null;
    private static RetrofitAPI retrofitAPI;
    private final static String BASE_URL = "https://d25o9y1ds6dmpt.cloudfront.net";

    private RetrofitClient4(){
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        retrofitAPI = retrofit.create(RetrofitAPI.class);
    }

    public static RetrofitClient4 getInstance(){
        if (instance==null){
            instance = new RetrofitClient4();
        }
        return instance;
    }

    public static RetrofitAPI getRetrofitAPI(){
        return retrofitAPI;
    }
}
