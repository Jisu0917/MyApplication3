package com.example.myapplication2.api.dto;

import com.google.gson.annotations.SerializedName;

public class FeedbacksData {
    @SerializedName("id")
    private Long id;

    @SerializedName("initiator")
    private String initiator; //USER, FRIEND

    @SerializedName("speed_score")
    private int speed_score;

    @SerializedName("speed_comment")
    private String speed_comment;

    @SerializedName("tone_score")
    private int tone_score;

    @SerializedName("tone_comment")
    private String tone_comment;

    @SerializedName("closing_score")
    private int closing_score;

    @SerializedName("closing_comment")
    private String closing_comment;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getInitiator() {
        return initiator;
    }

    public void setInitiator(String initiator) {
        this.initiator = initiator;
    }

    public int getSpeed_score() {
        return speed_score;
    }

    public void setSpeed_score(int speed_score) {
        this.speed_score = speed_score;
    }

    public String getSpeed_comment() {
        return speed_comment;
    }

    public void setSpeed_comment(String speed_comment) {
        this.speed_comment = speed_comment;
    }

    public int getTone_score() {
        return tone_score;
    }

    public void setTone_score(int tone_score) {
        this.tone_score = tone_score;
    }

    public String getTone_comment() {
        return tone_comment;
    }

    public void setTone_comment(String tone_comment) {
        this.tone_comment = tone_comment;
    }

    public int getClosing_score() {
        return closing_score;
    }

    public void setClosing_score(int closing_score) {
        this.closing_score = closing_score;
    }

    public String getClosing_comment() {
        return closing_comment;
    }

    public void setClosing_comment(String closing_comment) {
        this.closing_comment = closing_comment;
    }
}
