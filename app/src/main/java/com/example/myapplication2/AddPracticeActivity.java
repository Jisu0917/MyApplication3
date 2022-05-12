package com.example.myapplication2;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TabHost;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.util.Calendar;
import java.util.Locale;
import java.util.TimeZone;

public class AddPracticeActivity extends AppCompatActivity {
    final int NO_INPUT = Integer.MAX_VALUE;
    Intent it;
    String todo;
    Cursor cursor;
    int index;

    EditText editText_content;
    ImageView imageView_star;
    int isStarFilled = 0;
    ListView listView;
    Button btn_cancel, btn_record;
    Spinner dropdown;
    String drop_item;

    DBHelper dbHelper;
    SQLiteDatabase db = null;

    int curYear, curMonth, curDate, curHour, curMinute;
    String AmPm;

    //final static MainActivity mainactivity = new MainActivity();
    //private static TabHost myTabHost = mainactivity.myTabHost;

    TabHost myTabHost = MainActivity.myTabHost;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_addpractice);

        editText_content = (EditText)findViewById(R.id.editText_practice_title);
        imageView_star = (ImageView)findViewById(R.id.imageVie_star);
        listView = findViewById(R.id.listview1);
        btn_cancel = findViewById(R.id.btn_cancel);
        btn_record = findViewById(R.id.btn_record);

        dbHelper = new DBHelper(this, 4);
        db = dbHelper.getWritableDatabase();    // 읽기/쓰기 모드로 데이터베이스를 오픈

        // 년월일시분초
        Calendar now = Calendar.getInstance(TimeZone.getTimeZone("Asia/Seoul"), Locale.KOREA);
        curYear = now.get(Calendar.YEAR);
        curMonth = now.get(Calendar.MONTH) + 1; // Note: zero based!
        curDate = now.get(Calendar.DAY_OF_MONTH);
        curHour = now.get(Calendar.HOUR_OF_DAY);
        curMinute = now.get(Calendar.MINUTE);

        AmPm = "AM";
        String today = fillZero(curMonth) + "월 " + fillZero(curDate) + "일";
        if (curHour > 12) {
            curHour -= 12;
            AmPm = "PM";
        } else if (curHour == 12) {
            AmPm = "PM";
        } else if (curHour == 0) {
            curHour = 12;
        }

        it = getIntent();
        todo = it.getStringExtra("TODO");

//            if (cursor.getInt(7) == 1) {
//                isStarFilled = 1;
//                imageView_star.setImageResource(R.drawable.ic_star_filled);
//            } else {
//                isStarFilled = 0;
//                imageView_star.setImageResource(R.drawable.ic_star_empty);
//            }

        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

    }

    // xml에서 호출
    public void insert(View v) {
        String content = editText_content.getText().toString();
        if (content.contains("'")) {
            content = content.replaceAll("'", "\'\'");
        }
        if (!content.equals("")) {
            String sql = "INSERT INTO tableName (content,year,month,date,hour,minute,ampm,starfill,finished,filename) VALUES ('" + content + "', " + curYear + ", " + curMonth +", " + curDate + ", " + curHour + ", " + curMinute + ", '" + AmPm + "', " + isStarFilled +", " + 0 + ", '');";
            db.execSQL(sql);

            //finish();

            // Record Activity에 방금 db에 추가한 practice의 인덱스 넘겨주기
            cursor = db.rawQuery(" SELECT * FROM tableName ", null);
            startManagingCursor(cursor);    // 엑티비티의 생명주기와 커서의 생명주기를 같게 한다.
            cursor.moveToLast();
            Intent intent = new Intent(AddPracticeActivity.this, RecordActivity.class);
            intent.putExtra("PRACTICE_INDEX", cursor.getInt(0));
            startActivity(intent);
            finish();
//            myTabHost.setCurrentTab(0); // Record 탭으로 전환
        } else {
            Toast.makeText(getApplicationContext(), "내용을 입력하지 않았습니다.", Toast.LENGTH_SHORT).show();
        }
    }

    // xml에서 호출
    public void setStarFilled(View view) {
        if (isStarFilled == 0) {
            imageView_star.setImageResource(R.drawable.ic_star_filled);
            isStarFilled = 1;
        } else if (isStarFilled == 1) {
            imageView_star.setImageResource(R.drawable.ic_star_empty);
            isStarFilled = 0;
        }
    }

    private String fillZero(int num) {
        if (num < 10) {
            return "0" + num;
        }
        else return "" + num;
    }

}
