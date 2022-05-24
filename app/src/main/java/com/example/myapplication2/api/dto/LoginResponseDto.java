package com.example.myapplication2.api.dto;

public class LoginResponseDto {
    Long user_id;

    public LoginResponseDto(Long user_id){
        this.user_id = user_id;
    }

    public Long getUser_id() {
        return user_id;
    }

    public void setUser_id(Long user_id) {
        this.user_id = user_id;
    }
}
