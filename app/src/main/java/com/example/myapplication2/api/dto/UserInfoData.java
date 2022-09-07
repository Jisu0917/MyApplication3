package com.example.myapplication2.api.dto;

import com.google.gson.annotations.SerializedName;

public class UserInfoData {
    @SerializedName("id")
    private Long id;

    @SerializedName("name")
    private String name;

    @SerializedName("email")
    private String email;

    @SerializedName("picture")
    private String picture;

    @SerializedName("point")
    private int point;

    @SerializedName("practices")
    private PracticesData[] practices;

    @SerializedName("posts")
    private PostsData[] posts;

    @SerializedName("feedbacks")
    private FeedbacksData[] feedbacks;

    @SerializedName("friends")
    private FriendsData[] friends;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPicture() {
        return picture;
    }

    public void setPicture(String picture) {
        this.picture = picture;
    }

    public int getPoint() {
        return point;
    }

    public void setPoint(int point) {
        this.point = point;
    }

    public PracticesData[] getPractices() {
        return practices;
    }

    public void setPractices(PracticesData[] practices) {
        this.practices = practices;
    }

    public PostsData[] getPosts() {
        return posts;
    }

    public void setPosts(PostsData[] posts) {
        this.posts = posts;
    }

    public FeedbacksData[] getFeedbacks() {
        return feedbacks;
    }

    public void setFeedbacks(FeedbacksData[] feedbacks) {
        this.feedbacks = feedbacks;
    }

    public FriendsData[] getFriends() {
        return friends;
    }

    public void setFriends(FriendsData[] friends) {
        this.friends = friends;
    }

}
