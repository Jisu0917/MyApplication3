package com.example.myapplication2.api;

import com.example.myapplication2.api.dto.LoginRequestDto;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface RetrofitAPI {
    @POST("/api/login")
    Call<LoginRequestDto> postData(@Body LoginRequestDto param);
}
