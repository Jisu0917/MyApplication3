package com.example.myapplication2.api.dto;

import com.google.gson.annotations.SerializedName;

public class AnalysisContentData {
    @SerializedName("id")
    private Long id;

    @SerializedName("integration")
    private String integration;

    @SerializedName("movement")
    private String movement;

    @SerializedName("posture")
    private String posture;

    @SerializedName("speed")
    private String speed;

    @SerializedName("volumne")
    private String volume;

    @SerializedName("tone")
    private String tone;

    @SerializedName("closing")
    private String closing;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getIntegration() {
        return integration;
    }

    public void setIntegration(String integration) {
        this.integration = integration;
    }

    public String getMovement() {
        return movement;
    }

    public void setMovement(String movement) {
        this.movement = movement;
    }

    public String getPosture() {
        return posture;
    }

    public void setPosture(String posture) {
        this.posture = posture;
    }

    public String getSpeed() {
        return speed;
    }

    public void setSpeed(String speed) {
        this.speed = speed;
    }

    public String getVolume() {
        return volume;
    }

    public void setVolume(String volume) {
        this.volume = volume;
    }

    public String getTone() {
        return tone;
    }

    public void setTone(String tone) {
        this.tone = tone;
    }

    public String getClosing() {
        return closing;
    }

    public void setClosing(String closing) {
        this.closing = closing;
    }
}
