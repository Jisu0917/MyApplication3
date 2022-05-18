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
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class EditPracticeActivity extends AppCompatActivity {
    Intent it;
    Cursor cursor;
    int index;

    EditText editText_content;

    ImageView imageView_star;
    int isStarFilled = 0;
    ListView listView;
    Button btn_cancel, btn_save;

    DBHelper dbHelper;
    SQLiteDatabase db = null;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editpractice);

        editText_content = (EditText)findViewById(R.id.editText_practice_title);
        imageView_star = (ImageView)findViewById(R.id.imageVie_star);
        listView = findViewById(R.id.listview1);
        btn_cancel = findViewById(R.id.btn_cancel);
        btn_save = findViewById(R.id.btn_save);

        dbHelper = new DBHelper(this, 4);
        db = dbHelper.getWritableDatabase();    // 읽기/쓰기 모드로 데이터베이스를 오픈

        it = getIntent();
        index = it.getIntExtra("INDEX", 0);
        cursor = db.rawQuery(" SELECT * FROM tableName ", null);
        startManagingCursor(cursor);    // 엑티비티의 생명주기와 커서의 생명주기를 같게 한다.
        cursor.moveToPosition(index);  // position에 해당하는 row로 가서
        editText_content.setText(cursor.getString(1));


        if (cursor.getInt(8) == 1) {
            isStarFilled = 1;
            imageView_star.setImageResource(R.drawable.ic_star_filled);
        } else {
            isStarFilled = 0;
            imageView_star.setImageResource(R.drawable.ic_star_empty);
        }


        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        btn_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String content = editText_content.getText().toString();
                if (!content.equals("")) {
                    db.execSQL("UPDATE tableName SET content = '" + content + "' WHERE mid = " + (index + 1));
                    db.execSQL("UPDATE tableName SET starfill = " + isStarFilled + " WHERE mid = " + (index + 1));
                    finish();
                } else {
                    Toast.makeText(getApplicationContext(), "내용을 입력하지 않았습니다.", Toast.LENGTH_SHORT).show();
                }
            }
        });
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
}
