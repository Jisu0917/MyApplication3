package com.example.myapplication2.api.dto;

import com.google.gson.annotations.SerializedName;

public class FriendIdCodeData {
    @SerializedName("friend_id")
    private Long friend_id;

    @SerializedName("friendCode")
    private String friendCode;

    public Long getFriend_id() {
        return friend_id;
    }

    public void setFriend_id(Long friend_id) {
        this.friend_id = friend_id;
    }

    public String getFriendCode() {
        return friendCode;
    }

    public void setFriendCode(String friendCode) {
        this.friendCode = friendCode;
    }
}
