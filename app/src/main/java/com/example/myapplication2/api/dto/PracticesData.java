package com.example.myapplication2.api.dto;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.myapplication2.DBHelper;
import com.google.gson.annotations.SerializedName;

public class PracticesData {

    @SerializedName("id")
    private Long id;

    @SerializedName("title")
    private String title;

//    @SerializedName("audioPath")
//    private String audioPath;

    @SerializedName("move_sensitivity")
    private int move_sensitivity;

    @SerializedName("eyes_sensitivity")
    private int eyes_sensitivity;

    @SerializedName("scope")
    private String scope; //PUBLIC, PRIVATE

    @SerializedName("sort")
    private String sort; //ONLINE, OFFLINE

    @SerializedName("user_id")
    private Long user_id;

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

//    public String getAudioPath() {
//        return audioPath;
//    }
//
//    public void setAudioPath(String audioPath) {
//        this.audioPath = audioPath;
//    }

    public int getMoveSensitivity() {
        return move_sensitivity;
    }

    public void setMoveSensitivity(int move_sensitivity) {
        this.move_sensitivity = move_sensitivity;
    }

    public int getEyesSensitivity() {
        return eyes_sensitivity;
    }

    public void setEyesSensitivity(int eyes_sensitivity) {
        this.eyes_sensitivity = eyes_sensitivity;
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

    public Long getUserId() {
        return user_id;
    }

    public void setUserId(Long user_id) {
        this.user_id = user_id;
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
