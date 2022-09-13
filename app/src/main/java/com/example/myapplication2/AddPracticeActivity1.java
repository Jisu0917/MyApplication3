package com.example.myapplication2;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
//import androidx.appcompat.widget.Toolbar;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.widget.Toolbar;

import java.util.Calendar;
import java.util.Locale;
import java.util.TimeZone;

public class AddPracticeActivity1 extends AppCompatActivity {
    final int NO_INPUT = Integer.MAX_VALUE;

    Toolbar toolbar;
    ActionBar actionBar;
    
    EditText editText_title;
    TextView tv_scope;
    ImageView iv_scope;
    Spinner sort_spinner, sensitivity_spinner, gender_spinner;
    Button btn_cancel, btn_record;

    String scope = "PRIVATE", sort = "", gender="";
    int sensitivity;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_addpractice1);
        setTitle("새 연습 추가");
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        //툴바
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        actionBar = getSupportActionBar();
        actionBar.setDisplayShowCustomEnabled(true);
        actionBar.setDisplayShowTitleEnabled(false);//기본 제목을 없애줍니다.
        actionBar.setDisplayHomeAsUpEnabled(true);

        editText_title = (EditText)findViewById(R.id.editText_title);
        tv_scope = (TextView) findViewById(R.id.tv_scope);
        iv_scope = (ImageView)findViewById(R.id.iv_scope);
        sort_spinner = findViewById(R.id.sort_spinner);
        sensitivity_spinner = findViewById(R.id.sensitivity_spinner);
        gender_spinner = findViewById(R.id.gender_spinner);

        // Sort Spinner
        String[] itemList1 = {"ONLINE", "OFFLINE"};

        ArrayAdapter<String> adapter1 = new ArrayAdapter<String>(
                this, android.R.layout.simple_spinner_item, itemList1);
        adapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sort_spinner.setAdapter(adapter1);

        sort_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                sort = itemList1[i];
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        // Sensitivity Spinner
        String[] itemList2 = {"1", "2", "3", "4", "5", "6", "7", "8", "9", "10"};

        ArrayAdapter<String> adapter2 = new ArrayAdapter<String>(
                this, android.R.layout.simple_spinner_item, itemList2);
        adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sensitivity_spinner.setAdapter(adapter2);

        sensitivity_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                sensitivity = Integer.parseInt(itemList2[i]);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        // Gender Spinner
        String[] itemList3 = {"WOMEN", "MEN"};

        ArrayAdapter<String> adapter3 = new ArrayAdapter<String>(
                this, android.R.layout.simple_spinner_item, itemList3);
        adapter3.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        gender_spinner.setAdapter(adapter3);

        gender_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                gender = itemList3[i];
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        btn_cancel = findViewById(R.id.btn_cancel);
        btn_record = findViewById(R.id.btn_record);


        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

    }

    // xml에서 호출
    public void insert(View v) {
        String title = editText_title.getText().toString();
        if (title.contains("'")) {
            title = title.replaceAll("'", "\'\'");
        }
        if (!title.equals("")) {
            //포인트 정보 확인
            if (((UserPoints)getApplication()).getUserPoint() < 10) {
                Toast.makeText(AddPracticeActivity1.this, "포인트가 부족하여 연습을 시작할 수 없습니다.", Toast.LENGTH_SHORT).show();
                finish();
            }
            else {
                Intent intent = new Intent(AddPracticeActivity1.this, RecordActivity1.class);
                intent.putExtra("title", title);
                intent.putExtra("scope", scope);
                intent.putExtra("sort", sort);
                intent.putExtra("sensitivity", sensitivity);
                intent.putExtra("gender", gender);
                startActivity(intent);
                finish();
            }
        } else {
            Toast.makeText(getApplicationContext(), "내용을 입력하지 않았습니다.", Toast.LENGTH_SHORT).show();
        }
    }

    // xml에서 호출
    public void setScopeImg(View view) {
        if (scope.equals("PUBLIC")) {
            tv_scope.setText("비공개");
            iv_scope.setImageResource(R.drawable.ic_star_filled);
            scope = "PRIVATE";
        } else if (scope.equals("PRIVATE")) {
            tv_scope.setText("공개");
            iv_scope.setImageResource(R.drawable.ic_star_empty);
            scope = "PUBLIC";
        }
    }
}
