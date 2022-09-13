package com.example.myapplication2;

import android.app.Application;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.os.Environment;
import android.util.Log;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class UserPoints extends Application {

    private Long userId;
    private int user_point;

    DBHelper dbHelper;
    SQLiteDatabase db = null;
    Cursor cursor;

    public UserPoints() {
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public int getUserPoint() {
        return user_point;
    }

    public void setUserPoint(int point) {
        this.user_point = point;
    }

    public void updateUserPoint(int point, String instruction) {

        if (instruction.equals("plus")) {
            this.user_point += point;
            addHistoryToFile(getNowDateTime() + "#" + point+" 포인트 적립" + "#남은 포인트 : " + user_point);
        }
        else if (instruction.equals("minus")) {
            this.user_point -= point;
            addHistoryToFile(getNowDateTime() + "#" + point+" 포인트 차감" + "#남은 포인트 : " + user_point);
        } else if (instruction.equals("join")) {
            addHistoryToFile("join");
        }

//        addHistoryToFile(point, instruction);
//        addHistoryToFile(getHistoryStr(point, instruction));
    }


    private String getHistoryStr(int point, String instruction) {
        String str = "";
        if (instruction.equals("plus"))
            str = getNowDateTime() + "#" + point+" 포인트 적립" + "#남은 포인트 : " + user_point;
        else if (instruction.equals("minus"))
            str = getNowDateTime() + "#" + point+" 포인트 차감" + "#남은 포인트 : " + user_point;

        return str;
    }

    private void addHistoryToFile(String str) {

        dbHelper = new DBHelper(this, 4);
        db = dbHelper.getWritableDatabase();    // 읽기/쓰기 모드로 데이터베이스를 오픈

        if (str.equals("join")) {
            Cursor cursor = db.rawQuery(" SELECT * FROM pointHistory ", null);
            if (cursor.moveToFirst()) {
                int count = 1;
                while (cursor.moveToNext())
                    count++;

                System.out.println("count : " + count);  //임시, 확인용

                if (count == 0) {
                    String s = "회원가입을 축하합니다!#30 포인트 지급#남은 포인트 : 30";
                    String sql = "INSERT INTO pointHistory (history) VALUES ('" + s + "');";
                    db.execSQL(sql);
                }
            } else {
                System.out.println("cursor.moveToFirst() null");  //임시, 확인용
                String s = "회원가입을 축하합니다!#30 포인트 지급#남은 포인트 : 30";
                String sql = "INSERT INTO pointHistory (history) VALUES ('" + s + "');";
                db.execSQL(sql);
            }
        } else {
            String sql = "INSERT INTO pointHistory (history) VALUES ('" + str + "');";
            db.execSQL(sql);
        }

    }

    public ArrayList<String> getHistory() {

//        saveFile = new File(directory, filename);
        
        ArrayList<String> history = new ArrayList<>();

        cursor = db.rawQuery(" SELECT * FROM pointHistory ", null);
        //startManagingCursor(cursor);    // 엑티비티의 생명주기와 커서의 생명주기를 같게 한다.
        if (cursor.moveToFirst()) {
            history.add(cursor.getString(0));
            while (cursor.moveToNext()) {
                history.add(cursor.getString(0));
            }
        } else {
            System.out.println("cursor.moveToFirst()=null!");
        }

        return history;
    }

    private String getNowDateTime() {
        long now = System.currentTimeMillis(); // 현재시간 받아오기
        Date date = new Date(now); // Date 객체 생성
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm");
        String nowTime = sdf.format(date);

        return nowTime;
    }
}
