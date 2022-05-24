package com.example.myapplication2.api;

import com.example.myapplication2.api.dto.Post;

import java.util.HashMap;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public interface RetrofitAPI {
    @POST("/api/login")
    Call<Post> postData(@Body Post param);
}
