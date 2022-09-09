package com.example.myapplication2;

import android.app.Application;
import android.os.Environment;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class UserPoint extends Application {
    private Long userId;
    private int user_point;
    private File saveFile;
    //private final Map<String, Integer> history = new HashMap<>();

    public UserPoint() {
        //create txt file and directory
        // 파일 생성
        saveFile = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/sookpeech"); // 저장 경로
        // 폴더 생성
        if(!saveFile.exists()){
            // 폴더 없을 경우
            saveFile.mkdir(); // 폴더 생성
        }
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
        }
        else if (instruction.equals("minus")) {
            this.user_point -= point;
        }

        addHistoryToFile(point, instruction);
    }

    private void addHistoryToFile(int point, String instruction) {
        String str = "";
        if (instruction.equals("plus"))
            str = getNowDateTime() + "#" + point+" 포인트 적립" + "#남은 포인트 : " + user_point;
        else if (instruction.equals("minus"))
            str = getNowDateTime() + "#" + point+" 포인트 차감" + "#남은 포인트 : " + user_point;

        //write on the file
        try {
            BufferedWriter buf = new BufferedWriter(new FileWriter(saveFile+"/user"+userId+"_history.txt", true));
            buf.append(str);
            buf.newLine(); // 개행
            buf.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public ArrayList<String> getHistory() {
        ArrayList<String> history = new ArrayList<>();
        if(saveFile.exists()) { // 폴더 있을 경우
            String line = null; // 한줄씩 읽기
            try {
                BufferedReader buf = new BufferedReader(new FileReader(saveFile+"/user"+userId+"_history.txt"));
                while ((line = buf.readLine()) != null) {
                    history.add(line);
                }
                buf.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
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
