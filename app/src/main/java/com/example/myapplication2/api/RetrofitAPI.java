package com.example.myapplication2.api;

import com.example.myapplication2.api.dto.LoginRequestDto;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface RetrofitAPI {
    @POST("/api/login")
    Call<LoginRequestDto> postLoginToken(@Body LoginRequestDto param);

    @GET("/api/users/{id}")
    Call getUserInfo(@Path("id") Long id);
}
