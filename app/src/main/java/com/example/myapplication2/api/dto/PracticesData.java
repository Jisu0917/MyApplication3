package com.example.myapplication2.api.dto;

import com.google.gson.annotations.SerializedName;

public class PracticesData {
    @SerializedName("id")
    private Long id;

    @SerializedName("title")
    private String title;

    @SerializedName("audioPath")
    private String audioPath;

    @SerializedName("sensitivity")
    private int sensitivity;

    @SerializedName("scope")
    private String scope; //PUBLIC, PRIVATE

    @SerializedName("sort")
    private String sort; //ONLINE, OFFLINE

    @SerializedName("gender")
    private String gender; //WOMEN, MEN

    @SerializedName("analysis")
    private AnalysisData analysis;

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

    public String getAudioPath() {
        return audioPath;
    }

    public void setAudioPath(String audioPath) {
        this.audioPath = audioPath;
    }

    public int getSensitivity() {
        return sensitivity;
    }

    public void setSensitivity(int sensitivity) {
        this.sensitivity = sensitivity;
    }

    public String getScope() {
        return scope;
    }

    public void setScope(String scope) {
        this.scope = scope;
    }

    public String getSort() {
        return sort;
    }

    public void setSort(String sort) {
        this.sort = sort;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public AnalysisData getAnalysis() {
        return analysis;
    }

    public void setAnalysis(AnalysisData analysis) {
        this.analysis = analysis;
    }
}
