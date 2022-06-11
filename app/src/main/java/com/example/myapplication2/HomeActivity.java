package com.example.myapplication2;

import static com.example.myapplication2.MainActivity.tabWidget;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.example.myapplication2.api.objects.UserIdObject;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.File;
import java.util.ArrayList;

public class HomeActivity extends AppCompatActivity {
    final String TAG = "MainActivity";
    final int NO_INPUT = Integer.MAX_VALUE;

    FloatingActionButton fab_del, fab_add;

    DBHelper dbHelper;
    SQLiteDatabase db = null;
    Cursor cursor;

    ListView listview, listView2;
    View clickSource;
    View touchSource;
    int offset = 0;
    CustomChoiceListViewAdapter adapter;
    CustomListViewAdapter2 adapter2;
    TextView tvHello;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        tabWidget.setVisibility(View.VISIBLE);

        dbHelper = new DBHelper(this, 4);
        db = dbHelper.getWritableDatabase();    // 읽기/쓰기 모드로 데이터베이스를 오픈

        tvHello = (TextView)findViewById(R.id.textView_hello);
        listview = (ListView) findViewById(R.id.listview1);
        listView2 = (ListView)findViewById(R.id.listview2);
        listview.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(touchSource == null)
                    touchSource = v;

                if(v == touchSource) {
                    listView2.dispatchTouchEvent(event);
                    if(event.getAction() == MotionEvent.ACTION_UP) {
                        clickSource = v;
                        touchSource = null;
                    }
                }
                return false;
            }
        });
        listview.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                if(view == clickSource) {
                    try {
                        listView2.setSelectionFromTop(firstVisibleItem, view.getChildAt(0).getTop() + offset);
                    } catch (Exception e) {
                        Log.e(TAG, "listview.setOnScrollListener Err");
                    }
                }
            }

            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {}
        });

        listView2.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(touchSource == null)
                    touchSource = v;

                if(v == touchSource) {
                    listview.dispatchTouchEvent(event);
                    if(event.getAction() == MotionEvent.ACTION_UP) {
                        clickSource = v;
                        touchSource = null;
                    }
                }
                return false;
            }
        });
        listView2.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                if(view == clickSource)
                    listview.setSelectionFromTop(firstVisibleItem, view.getChildAt(0).getTop() + offset);
            }

            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {}
        });

        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                //Toast.makeText(HomeActivity.this, "클릭된 아이템 :"+i, Toast.LENGTH_SHORT).show();  // 임시, 확인용
                Intent intent = new Intent(HomeActivity.this, ShowPracticeActivity.class);
                intent.putExtra("ITEM_INDEX", i);
                startActivity(intent);
            }
        });


        listUpdate();

        fab_add = findViewById(R.id.fab_add);
        fab_add.show();
        fab_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HomeActivity.this, AddPracticeActivity.class);
                intent.putExtra("Home", "ADD");
                startActivity(intent);
            }
        });

        fab_del = findViewById(R.id.fab_del);
        fab_del.hide();
        fab_del.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteAllSelected();
            }
        });

        registerForContextMenu(listview);
    }

    @Override
    protected void onResume() {
        super.onResume();
        tabWidget.setVisibility(View.VISIBLE);

        listUpdate();
        tvHello = (TextView)findViewById(R.id.textView_hello);
        if (adapter.getCount() != 0) {
            tvHello.setVisibility(View.GONE);
        } else {
            tvHello.setVisibility(View.VISIBLE);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        tabWidget.setVisibility(View.VISIBLE);
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, final View v, ContextMenu.ContextMenuInfo menuInfo) {
        getMenuInflater().inflate(R.menu.context_menu, menu);
        super.onCreateContextMenu(menu, v, menuInfo);
    }

    public boolean onContextItemSelected(MenuItem item) {
        //AdapterContextMenuInfo
        //AdapterView가 onCreateContextMenu할때의 추가적인 menu 정보를 관리하는 클래스
        //ContextMenu로 등록된 AdapterView(여기서는 Listview)의 선택된 항목에 대한 정보를 관리하는 클래스
        AdapterView.AdapterContextMenuInfo info= (AdapterView.AdapterContextMenuInfo)item.getMenuInfo();

        int index = info.position; //AdapterView안에서 ContextMenu를 보여주는 항목의 위치

        ListView listView2 = (ListView)findViewById(R.id.listview2);

        switch( item.getItemId() ){
            case R.id.menu_done:
                cursor = db.rawQuery(" SELECT * FROM tableName ", null);
                startManagingCursor(cursor);    // 엑티비티의 생명주기와 커서의 생명주기를 같게 한다.
                cursor.moveToPosition(index);  // position에 해당하는 row로 가서

                int isFinished = cursor.getInt(9);  // finished 값을 읽어라. (0 또는 1)
                if (isFinished == 0) {
                    String sql = " UPDATE tableName SET finished = " + 1 + " WHERE mid = " + (index + 1);
                    db.execSQL(sql);
                } else if (isFinished == 1) {
                    String sql = " UPDATE tableName SET finished = " + 0 + " WHERE mid = " + (index + 1);
                    db.execSQL(sql);
                }
                listUpdate();
                break;

            case R.id.menu_edit:
                Intent intent = new Intent(HomeActivity.this, EditPracticeActivity.class);
                //intent.putExtra("TODO", "EDIT");
                intent.putExtra("INDEX", index);
                startActivity(intent);
                break;

            case R.id.menu_delete:
                int exAlarmID = NO_INPUT;
                cursor = db.rawQuery(" SELECT * FROM tableName ", null);
                startManagingCursor(cursor);    // 엑티비티의 생명주기와 커서의 생명주기를 같게 한다.
                while (cursor.moveToNext()) {
                    if (cursor.getInt(0) == (index + 1)) {  // mid
                        exAlarmID = cursor.getInt(11);
                        break;
                    }
                }
                delExAlarm(exAlarmID);
                String sql = " DELETE FROM tableName WHERE mid = " + (index + 1);
                db.execSQL(sql);
                listUpdate();
                listView2.setVisibility(View.GONE);
                break;
        }
        return true;
    };


    public void listUpdate() {
        deleteDuplicate();
        deleteEmptyVideo();
        cursor = db.rawQuery("SELECT * FROM tableName", null);
        startManagingCursor(cursor);    //엑티비티의 생명주기와 커서의 생명주기를 같게 한다.

        // Adapter 생성
        adapter = new CustomChoiceListViewAdapter() ;

        int i = 1;
        while (cursor.moveToNext()) {
            try {
                String sql1 = "UPDATE tableName SET mid = " + i + " WHERE content = '" + cursor.getString(1)
                        + "' AND year = " + cursor.getInt(2)
                        + " AND month = " + cursor.getInt(3) + " AND date = " + cursor.getInt(4)
                        + " AND hour = " + cursor.getInt(5) + " AND minute = " + cursor.getInt(6)
                        + " AND ampm = '" + cursor.getString(7) + "'";
                db.execSQL(sql1);
                i++;
            } catch (Exception e) {
                Log.e(TAG, "mId Setting Failed", e);
            }

            Drawable drawable, drawable1;
            int starFill = cursor.getInt(8);
            if (starFill == 1) {
                drawable = ContextCompat.getDrawable(this, R.drawable.ic_star_filled);
            } else {
                drawable = ContextCompat.getDrawable(this, R.drawable.ic_star_empty);
            }
            int isFinished = cursor.getInt(9);
            int bgColor = 0;
            if (isFinished == 1) {
                drawable1 = ContextCompat.getDrawable(this, R.drawable.ic_checked);
                bgColor = 0x60c9dcff;
            } else {
                drawable1 = ContextCompat.getDrawable(this, R.drawable.ic_unchecked);
                bgColor = 0xffffffff;  // white
            }
            adapter.addItem(bgColor, drawable1, drawable, cursor.getString(1));  // content
            String dateAndTime = "";
            String dateText = fillZero(cursor.getInt(2)) + "." + fillZero(cursor.getInt(3)) + "." + fillZero(cursor.getInt(4));  // year.month.date
            String timeText = fillZero(cursor.getInt(5)) + ":" + fillZero(cursor.getInt(6)) + " " + cursor.getString(7);  // hour:minute AmPm

            if (cursor.getInt(2) == NO_INPUT || cursor.getInt(3) == NO_INPUT || cursor.getInt(4) == NO_INPUT) {
                dateText = "";
            }
            if (cursor.getInt(5) == NO_INPUT || cursor.getInt(6) == NO_INPUT) {
                timeText = "";
            }

            dateAndTime += dateText + "  " + timeText;

            adapter.addItem2(dateAndTime);
        }
        listview.setAdapter(adapter);
        if (adapter.getCount() == 0) {
            tvHello.setVisibility(View.VISIBLE);
        } else {
            tvHello.setVisibility(View.GONE);
        }

//        System.out.println(getTableAsString(db, "tableName"));  // 임시, 확인용
    }

    private String fillZero(int num) {
        if (num < 10) {
            return "0" + num;
        }
        else return "" + num;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.actionbar_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle presses on the action bar items
        switch (item.getItemId()) {
            case R.id.action_btn_select:
                if (adapter.getCount() != 0) {
                    adapter2 = new CustomListViewAdapter2();

                    if (listView2.getVisibility() != View.VISIBLE) {
                        listView2.setVisibility(View.VISIBLE);
                        fab_add.hide();
                        fab_del.show();
                    } else {
                        listView2.setVisibility(View.GONE);
                        fab_del.hide();
                        fab_add.show();
                    }

                    cursor = db.rawQuery("SELECT * FROM tableName", null);
                    startManagingCursor(cursor);
                    while (cursor.moveToNext()) {
                        adapter2.addItem();
                    }
                    listView2.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
                    listView2.setAdapter(adapter2);
                }
                return true;

            case R.id.action_btn_delFinished:  // 완료된 항목 모두 삭제
                int exAlarmID = NO_INPUT;
                cursor = db.rawQuery(" SELECT * FROM tableName ", null);
                startManagingCursor(cursor);    // 엑티비티의 생명주기와 커서의 생명주기를 같게 한다.


                String sql = " DELETE FROM tableName WHERE finished = " + 1;
                db.execSQL(sql);

                if (listView2.getVisibility() == View.VISIBLE) {
                    listView2.setVisibility(View.GONE);
                    fab_del.hide();
                    fab_add.show();
                }

                listUpdate();

                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void deleteAllSelected() {  // fab_del onClickListener
        ArrayList<Integer> checkedIndexList = adapter2.getCheckedIndexList();
        for (int i = 0; i < checkedIndexList.size(); i++) {
            int exAlarmID = NO_INPUT;
            cursor = db.rawQuery(" SELECT * FROM tableName ", null);
            startManagingCursor(cursor);    // 엑티비티의 생명주기와 커서의 생명주기를 같게 한다.


            // 내부저장소에서 동영상 파일 삭제
            cursor.moveToPosition(checkedIndexList.get(i));  // position에 해당하는 row로 가서
            File delFile = new File(cursor.getString(10));
            delFile.delete();

            /*
            * 여러 개를 한꺼번에 삭제할 때,
            * 리스트에서 마지막 항목을 삭제하려고 하면
            * cursor.getString(10)에서 Index n requested, with a size of n 이라는 에러가 뜬다..
            * */



            String sql = " DELETE FROM tableName WHERE mid = " + (checkedIndexList.get(i) + 1);
            db.execSQL(sql);


        }
        adapter2.resetCheckedIndexList();

        listView2.setVisibility(View.GONE);
        listUpdate();
        fab_del.hide();
        fab_add.show();

        adapter2.notifyDataSetChanged();

        /*
        * delete할 때 db 정보만 지우는 게 아니라 영상 파일도 지워주기!!
        * */
    }

    private void delExAlarm(int exAlarmID) {
//        Intent intent = new Intent(getApplicationContext(), AlarmReceiver2.class);
//        boolean alarmUp = (PendingIntent.getBroadcast(getApplicationContext(), exAlarmID,
//                intent, PendingIntent.FLAG_NO_CREATE) != null);
//        if (alarmUp)
//        {
//            Log.d("delExAlarm", "Alarm is active");
//            AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
//            PendingIntent pendingIntent = PendingIntent.getBroadcast(getApplicationContext(),
//                    exAlarmID, intent, PendingIntent.FLAG_UPDATE_CURRENT);
//            alarmManager.cancel(pendingIntent);
//        }
    }

    // db 테이블 전체 출력 함수 (확인용)
//    public String getTableAsString(SQLiteDatabase db, String tableName) {
//        Log.d(TAG, "getTableAsString called");
//        String tableString = String.format("Table %s:\n", tableName);
//        Cursor allRows  = db.rawQuery("SELECT * FROM " + tableName, null);
//        if (allRows.moveToFirst() ){
//            String[] columnNames = allRows.getColumnNames();
//            do {
//                for (String name: columnNames) {
//                    tableString += String.format("%s: %s , ", name,
//                            allRows.getString(allRows.getColumnIndex(name)));
//                }
//                tableString += "\n";
//
//            } while (allRows.moveToNext());
//        }
//        return tableString;
//    }

    public void deleteDuplicate() {
        String sql = "DELETE FROM tableName WHERE rowid NOT IN (SELECT min(rowid) FROM tableName GROUP BY content, month, date, hour, minute)";
        db.execSQL(sql);
    }

    public void deleteEmptyVideo() {
        String sql = "DELETE FROM tableName WHERE filename = ''";
        db.execSQL(sql);
    }

}
