package com.example.myapplication2;

import android.content.ContentValues;
import android.content.Context;
import android.content.res.Resources;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBHelper extends SQLiteOpenHelper {

    static final String DATABASE_NAME = "test.db";
    //static final int DATABASE_VERSION = 2;

    public DBHelper(Context context, int version) {
        super(context, DATABASE_NAME, null, version);
    }
    /*
        public DBHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
            super(context, name, factory, version);
        }
    */
    @Override
    public void onCreate(SQLiteDatabase db) {  //  PRIMARY KEY AUTOINCREMENT
        db.execSQL("CREATE TABLE tableName ( mid INTEGER DEFAULT 0, content TEXT, year INTEGER, month INTEGER, date INTEGER, hour INTEGER, minute INTEGER, ampm TEXT, starfill INTEGER, finished INTEGER, filename TEXT, practice_id INTEGER );");
        db.execSQL("CREATE TABLE alarmTable ( alarmCount INTEGER )");
    }

    /* index
     * 0 : mid,  1 : content,  2 : year, 3 : month,  4 : date,  5 : hour,  6 : minute,  7 : ampm,  8 : starfill,  9 : finished, 10:filename(url), 11: practice_id
     * content,year,month,date,hour,minute,ampm,starfill,finished
     * */

    /*
     * starFill : 0 (empty = PUBLIC), 1 (fill = PRIVATE)
     * finished : 0 (unfinished), 1 (finished)
     * alarmBefore : 0 (none), 1 (minute), 2 (hour), 3 (day)
     * practice_id : 서버에 저장된 practice id
     * */

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS tableName");
        onCreate(db);
    }
}
