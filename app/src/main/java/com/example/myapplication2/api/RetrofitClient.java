package com.example.myapplication2.api;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitClient {
    private static RetrofitClient instance = null;
    private static RetrofitAPI retrofitAPI;
    private final static String BASE_URL = "http://3.36.74.60:8080/"; //http://10.0.2.2:8080"; //임시 LOCAL URL

    private RetrofitClient(){
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        retrofitAPI = retrofit.create(RetrofitAPI.class);
    }

    public static RetrofitClient getInstance(){
        if (instance==null){
            instance = new RetrofitClient();
        }
        return instance;
    }

    public static RetrofitAPI getRetrofitAPI(){
        return retrofitAPI;
    }
}
