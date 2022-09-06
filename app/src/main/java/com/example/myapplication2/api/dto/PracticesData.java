package com.example.myapplication2.api.dto;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.myapplication2.DBHelper;
import com.google.gson.annotations.SerializedName;

public class PracticesData {

    DBHelper dbHelper;
    SQLiteDatabase db = null;

//    public PracticesData(Context context, Long id) {
//        this.id = id;
//
//        this.title = "임시 제목 (확인용)";
//
////        dbHelper = new DBHelper(context, 4);
////        db = dbHelper.getWritableDatabase();    // 읽기/쓰기 모드로 데이터베이스를 오픈
////
////
////        Cursor cursor = db.rawQuery(" SELECT * FROM tableName ", null);
////        //startManagingCursor(cursor);    // 엑티비티의 생명주기와 커서의 생명주기를 같게 한다.
////        //cursor.move(id.intValue());
////
////        System.out.println("111111111111111111");  // 임시, 확인용
////        cursor.moveToLast();
////        System.out.println("222222222222222222");  // 임시, 확인용
////
////        this.title = cursor.getString(1);  // 1 : content
////
////        this.audioPath = cursor.getString(10);  // 10 : filename(url)
////
////        //this.scope = cursor.getInt(8);  // 8 : starfill. 0 = PUBLIC, 1 = PRIVATE
////
////        int starfill = cursor.getInt(8);  // 8 : starfill. 0 = PUBLIC, 1 = PRIVATE
////        if (starfill == 0) {
////            this.scope = "PUBLIC";
////        } else {
////            this.scope = "PRIVATE";
////        }
////
////        AnalysisData analysisData = new AnalysisData();
////        int finished = cursor.getInt(9); // 9 : finished. 0 (unfinished), 1 (finished)
////        if (finished == 0) {
////            analysisData.setState("INCOMPLETE");
////        } else {
////            analysisData.setState("COMPLETE");
////        }
//
//
//    }
//

    @SerializedName("id")
    private Long id;

    @SerializedName("title")
    private String title;

//    @SerializedName("audioPath")
//    private String audioPath;

    @SerializedName("move_sensitivity")
    private int move_sensitivity;

    @SerializedName("eyes_sensitivity")
    private int eyes_sensitivity;

    @SerializedName("scope")
    private String scope; //PUBLIC, PRIVATE

    @SerializedName("sort")
    private String sort; //ONLINE, OFFLINE

    @SerializedName("user_id")
    private Long user_id;

    @SerializedName("gender")
    private String gender; //WOMEN, MEN

    @SerializedName("analysis")
    private AnalysisData analysis;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

//    public String getAudioPath() {
//        return audioPath;
//    }
//
//    public void setAudioPath(String audioPath) {
//        this.audioPath = audioPath;
//    }

    public int getMoveSensitivity() {
        return move_sensitivity;
    }

    public void setMoveSensitivity(int move_sensitivity) {
        this.move_sensitivity = move_sensitivity;
    }

    public int getEyesSensitivity() {
        return eyes_sensitivity;
    }

    public void setEyesSensitivity(int eyes_sensitivity) {
        this.eyes_sensitivity = eyes_sensitivity;
    }

    public String getScope() {
        return scope;
    }

    public void setScope(String scope) {
        this.scope = scope;
    }

    public String getSort() {
        return sort;
    }

    public void setSort(String sort) {
        this.sort = sort;
    }

    public Long getUserId() {
        return user_id;
    }

    public void setUserId(Long user_id) {
        this.user_id = user_id;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public AnalysisData getAnalysis() {
        return analysis;
    }

    public void setAnalysis(AnalysisData analysis) {
        this.analysis = analysis;
    }
}
