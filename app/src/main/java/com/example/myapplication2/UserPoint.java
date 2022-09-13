package com.example.myapplication2;

import android.app.Application;
import android.os.Build;
import android.os.Environment;
import android.util.Log;

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
    public static String HISTORY_DIR = "sookpeech_history";

    private Long userId;
    private int user_point;
    private File directory, saveFile;
    private String filename;
    //private final Map<String, Integer> history = new HashMap<>();

    public UserPoint() {
        //create txt file and directory
//        // 파일 생성
//        saveFile = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/sookpeech"); // 저장 경로
//        // 폴더 생성
//        if(!saveFile.exists()){
//            // 폴더 없을 경우
//            saveFile.mkdir(); // 폴더 생성
//        }

        //////
        // 버전 30 이상에서 작동하도록 수정
        if (Build.VERSION.SDK_INT >= 30){
            System.out.println("android version >= 30");

            File destination = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS);
            if (!destination.exists()) { // 원하는 경로에 폴더가 있는지 확인
                destination.mkdirs();
                Log.d("UserPoint", "destination Created");
            }

            directory = new File(destination + File.separator+HISTORY_DIR);
            if (!directory.exists()) { // 원하는 경로에 폴더가 있는지 확인
                directory.mkdirs();  // 해당 유저의 history 파일이 없으면
                Log.d("UserPoint", "Directory Created");
            }
        } else{
            directory = new File(Environment.getExternalStorageDirectory() + File.separator+HISTORY_DIR);
            if (!directory.exists()) { // 원하는 경로에 폴더가 있는지 확인
                directory.mkdirs();  // 해당 유저의 history 파일이 없으면
                Log.d("UserPoint", "Directory Created");
            }
        }

        //////
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
        } else if (instruction.equals("join")) {
            addHistoryToFile("join");
        }

//        addHistoryToFile(point, instruction);
        addHistoryToFile(getHistoryStr(point, instruction));
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

        if (str.equals("join")) {
            System.out.println("addHistoryToFile : join");  //임시, 확인용
            File f = new File(directory + File.separator + "user"+userId+"_history.txt");
            if (!f.exists()) {
                System.out.println("addHistoryToFile : join : f.exists() false");  //임시, 확인용

                this.filename = "user"+userId+"_history.txt";
                saveFile = new File(directory, filename);

                //write on the file
                try {
                    BufferedWriter buf = new BufferedWriter(new FileWriter(saveFile, true));
                    buf.append("회원가입을 축하합니다!#30 포인트 지급#남은 포인트 : 30");
                    buf.newLine(); // 개행
                    buf.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else {

                System.out.println("addHistoryToFile : join : f.exists() true");  //임시, 확인용
            }
        } else {

//        String str = "";
//        if (instruction.equals("plus"))
//            str = getNowDateTime() + "#" + point+" 포인트 적립" + "#남은 포인트 : " + user_point;
//        else if (instruction.equals("minus"))
//            str = getNowDateTime() + "#" + point+" 포인트 차감" + "#남은 포인트 : " + user_point;

            this.filename = "user" + userId + "_history.txt";
            saveFile = new File(directory, filename);

            //write on the file
            try {
                BufferedWriter buf = new BufferedWriter(new FileWriter(saveFile, true));
                buf.append(str);
                buf.newLine(); // 개행
                buf.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public ArrayList<String> getHistory() {
        this.filename = "user"+userId+"_history.txt";
        saveFile = new File(directory, filename);
        
        ArrayList<String> history = new ArrayList<>();
        if(saveFile.exists()) { // 폴더 있을 경우
            String line = null; // 한줄씩 읽기
            try {
                BufferedReader buf = new BufferedReader(new FileReader(saveFile));
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
