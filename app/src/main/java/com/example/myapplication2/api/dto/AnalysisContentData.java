package com.example.myapplication2.api.dto;

import com.google.gson.annotations.SerializedName;

public class AnalysisContentData {
    @SerializedName("id")
    private Long id;

    /*
    * "total_duration": 20.5714,
            "inclined_duration": 0.0,
            "first_duration": 0.0,
            "second_duration": 0.0,
            "third_duration": 0.0,
            "script_duration": 0.0,
            "around_duration": 1.0,
            "face_move_duration": 0.0,
            "speed": 2076.0,
            "closing_remarks": 614.3,
            "shimmer": 6.01,
            "jitter": 1.44
            * */

    @SerializedName("total_duration")
    private float total_duration;

    @SerializedName("inclined_duration")
    private float inclined_duration;

    @SerializedName("first_duration")
    private float first_duration;

    @SerializedName("second_duration")
    private float second_duration;

    @SerializedName("third_duration")
    private float third_duration;

    @SerializedName("script_duration")
    private float script_duration;

    @SerializedName("around_duration")
    private float around_duration;

    @SerializedName("face_move_duration")
    private float face_move_duration;

    @SerializedName("speed")
    private float speed;

    @SerializedName("closing_remarks")
    private float closing_remarks;

    @SerializedName("shimmer")
    private float shimmer;

    @SerializedName("jitter")
    private float jitter;

    @SerializedName("createdDate")
    private String createdDate;

    @SerializedName("modifiedDate")
    private String modifiedDate;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public float getTotalDuration() {
        return total_duration;
    }

    public void setTotalDuration(float total_duration) {
        this.total_duration = total_duration;
    }

    public float getInclinedDuration() {
        return inclined_duration;
    }

    public void setInclinedDuration(float inclined_duration) {
        this.inclined_duration = inclined_duration;
    }

    public float getFirstDuration() {
        return first_duration;
    }

    public void setFirstDuration(float first_duration) {
        this.first_duration = first_duration;
    }

    public float getSecondDuration() {
        return second_duration;
    }

    public void setSecondDuration(float second_duration) {
        this.second_duration = second_duration;
    }

    public float getThirdDuration() {
        return third_duration;
    }

    public void setThirdDuration(float third_duration) {
        this.third_duration = third_duration;
    }

    public float getScriptDuration() {
        return script_duration;
    }

    public void setScriptDuration(float script_duration) {
        this.script_duration = script_duration;
    }

    public float getAroundDuration() {
        return around_duration;
    }

    public void setAroundDuration(float around_duration) {
        this.around_duration = around_duration;
    }

    public float getFaceMoveDuration() {
        return face_move_duration;
    }

    public void setFaceMoveDuration(float face_move_duration) {
        this.face_move_duration = face_move_duration;
    }

    public float getSpeed() {
        return speed;
    }

    public void setSpeed(float speed) {
        this.speed = speed;
    }

    public float getClosingRemarks() {
        return closing_remarks;
    }

    public void setClosingRemarks(float closing_remarks) {
        this.closing_remarks = closing_remarks;
    }

    public float getShimmer() {
        return shimmer;
    }

    public void setShimmer(float shimmer) {
        this.shimmer = shimmer;
    }

    public float getJitter() {
        return jitter;
    }

    public void setJitter(float jitter) {
        this.jitter = jitter;
    }

    public String getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(String createdDate) {
        this.createdDate = createdDate;
    }

    public String getModifiedDate() {
        return modifiedDate;
    }

    public void setModifiedDate(String modifiedDate) {
        this.modifiedDate = modifiedDate;
    }

//
//    @SerializedName("integration")
//    private String integration;
//
//    @SerializedName("movement")
//    private String movement;
//
//    @SerializedName("posture")
//    private String posture;
//
//    @SerializedName("speed")
//    private String speed;
//
//    @SerializedName("volumne")
//    private String volume;
//
//    @SerializedName("tone")
//    private String tone;
//
//    @SerializedName("closing")
//    private String closing;
//
//    public Long getId() {
//        return id;
//    }
//
//    public void setId(Long id) {
//        this.id = id;
//    }
//
//    public String getIntegration() {
//        return integration;
//    }
//
//    public void setIntegration(String integration) {
//        this.integration = integration;
//    }
//
//    public String getMovement() {
//        return movement;
//    }
//
//    public void setMovement(String movement) {
//        this.movement = movement;
//    }
//
//    public String getPosture() {
//        return posture;
//    }
//
//    public void setPosture(String posture) {
//        this.posture = posture;
//    }
//
//    public String getSpeed() {
//        return speed;
//    }
//
//    public void setSpeed(String speed) {
//        this.speed = speed;
//    }
//
//    public String getVolume() {
//        return volume;
//    }
//
//    public void setVolume(String volume) {
//        this.volume = volume;
//    }
//
//    public String getTone() {
//        return tone;
//    }
//
//    public void setTone(String tone) {
//        this.tone = tone;
//    }
//
//    public String getClosing() {
//        return closing;
//    }
//
//    public void setClosing(String closing) {
//        this.closing = closing;
//    }
}
