package com.example.myapplication2.api.dto;

import com.google.gson.annotations.SerializedName;

public class PointData {
    @SerializedName("id")
    private Long id;

    @SerializedName("point")
    private int point;

    @SerializedName("instruction")
    private String instruction;  //plus, minus

    public Long getUserId() {
        return id;
    }

    public void setUserId(Long id) {
        this.id = id;
    }

    public int getPoint() {
        return point;
    }

    public void setPoint(int point) {
        this.point = point;
    }

    public String getInstruction() {
        return instruction;
    }

    public void setInstruction(String instruction) {
        this.instruction = instruction;
    }
}
