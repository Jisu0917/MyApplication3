package com.example.myapplication2;

import android.app.Application;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class UserPoint extends Application {
    private int user_point;
    //private final Map<String, Integer> history = new HashMap<>();
    private ArrayList<String> history = new ArrayList<>();

    public int getUserPoint() {
        return user_point;
    }

    public void setUserPoint(int point) {
        this.user_point = point;
    }

    public ArrayList<String> getHistory() {
        return this.history;
    }

    public void updateUserPoint(int point, String instruction) {
        if (instruction.equals("plus")) {
            this.user_point += point;
            
            history.add(point+" 포인트 적립 ➜ point : " + user_point);
        }
        else if (instruction.equals("minus")) {
            this.user_point -= point;
            
            history.add(point+" 포인트 차감 ➜ point : " + user_point);
        }
    }
}
