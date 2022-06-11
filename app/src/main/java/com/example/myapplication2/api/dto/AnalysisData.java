package com.example.myapplication2.api.dto;

import com.google.gson.annotations.SerializedName;

public class AnalysisData {
    @SerializedName("id")
    private Long id;

    @SerializedName("state")
    private String state; //COMPLETE, INCOMPLETE

    @SerializedName("analysisContents")
    private AnalysisContentData analysisContent;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public AnalysisContentData getAnalysisContent() {
        return analysisContent;
    }

    public void setAnalysisContent(AnalysisContentData analysisContent) {
        this.analysisContent = analysisContent;
    }
}
