package com.example.myapplication2.api.dto;

public class Post {
    String idToken;
    Long user_id;

    public Post(String idToken, Long user_id){
        this.idToken = idToken;
        this.user_id = user_id;
    }

    public String getIdToken() {
        return idToken;
    }

    public void setIdToken(String idToken) {
        this.idToken = idToken;
    }

    public Long getUser_id() {
        return user_id;
    }

    public void setUser_id(Long user_id) {
        this.user_id = user_id;
    }
}