package com.example.myapplication2.api.dto;

import com.google.gson.annotations.SerializedName;

public class FriendsData {
    @SerializedName("id")
    private Long id;

    @SerializedName("friend_id")
    private Long friend_id;

    @SerializedName("friend_name")
    private String friend_name;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getFriend_id() {
        return friend_id;
    }

    public void setFriend_id(Long friend_id) {
        this.friend_id = friend_id;
    }

    public String getFriend_name() {
        return friend_name;
    }

    public void setFriend_name(String friend_name) {
        this.friend_name = friend_name;
    }
}
