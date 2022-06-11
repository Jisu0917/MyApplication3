package com.example.myapplication2.api.dto;

import com.google.gson.annotations.SerializedName;

public class PostsData {
    @SerializedName("id")
    private Long id;

    @SerializedName("title")
    private String title;

    @SerializedName("content")
    private String content;

    @SerializedName("practices")
    private PracticesData practices;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public PracticesData getPractices() {
        return practices;
    }

    public void setPractices(PracticesData practices) {
        this.practices = practices;
    }
}
