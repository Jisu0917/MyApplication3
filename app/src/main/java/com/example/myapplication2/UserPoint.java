package com.example.myapplication2;

import android.app.Application;

public class UserPoint extends Application {
    private int user_point;

    public int getUserPoint() {
        return user_point;
    }

    public void setUserPoint(int point) {
        this.user_point = point;
    }

    public void updateUserPoint(int point, String instruction) {
        if (instruction.equals("plus"))
            this.user_point += point;
        else if (instruction.equals("minus"))
            this.user_point -= point;
    }
}
