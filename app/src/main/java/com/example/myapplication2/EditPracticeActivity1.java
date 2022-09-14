package com.example.myapplication2;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.myapplication2.api.RetrofitAPI;
import com.example.myapplication2.api.RetrofitClient;
import com.example.myapplication2.api.dto.AnalysisData;
import com.example.myapplication2.api.dto.PracticesData;
import com.example.myapplication2.api.objects.UserIdObject;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EditPracticeActivity1 extends AppCompatActivity {
    Toolbar toolbar;
    ActionBar actionBar;

    Intent it;
    Cursor cursor;
    Long practice_id;
    PracticesData practicesData;

    EditText editText_title;
    TextView tv_scope;
    ImageView iv_scope;
    Button btn_cancel, btn_save;

    String scope = "", sort = "", gender="";
    int sensitivity;

    public static Context context;

    static Long userId;

    static RetrofitAPI retrofitAPI;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editpractice1);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        //툴바
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        actionBar = getSupportActionBar();
        actionBar.setDisplayShowCustomEnabled(true);
        actionBar.setDisplayShowTitleEnabled(false);//기본 제목을 없애줍니다.
        actionBar.setDisplayHomeAsUpEnabled(true);

        Intent it = getIntent();
        userId = it.getLongExtra("userId", 0);

        context = getApplicationContext();

        editText_title = (EditText)findViewById(R.id.editText_title);
        tv_scope = (TextView) findViewById(R.id.tv_scope);
        iv_scope = (ImageView)findViewById(R.id.iv_scope);

        btn_cancel = findViewById(R.id.btn_cancel);
        btn_save = findViewById(R.id.btn_save);

        it = getIntent();
        practice_id = it.getLongExtra("practice_id", 0);
        getPracticeInfo(practice_id);


        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        btn_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String title = editText_title.getText().toString();
                practicesData.setTitle(title);
                practicesData.setScope(scope);
                //영상 분석에 사용되는 항목(온,오프라인, 민감도, 성별)은 수정할 수 없게!
                practicesData.setSort(practicesData.getSort());
                practicesData.setMoveSensitivity(practicesData.getMoveSensitivity());
                practicesData.setEyesSensitivity(practicesData.getEyesSensitivity());
                practicesData.setGender(practicesData.getGender());

                if (!title.equals("")) {
                    updatePractice();

                    finish();
                } else {
                    Toast.makeText(getApplicationContext(), "내용을 입력하지 않았습니다.", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    // 서버에서 연습 정보 가져오기
    private void getPracticeInfo(Long practice_id) {
        System.out.println("분석이 완료된 특정 연습 정보 가져오기 시작");

        RetrofitClient retrofitClient = RetrofitClient.getInstance();

        if (retrofitClient!=null){
            retrofitAPI = RetrofitClient.getRetrofitAPI();
            retrofitAPI.getPracticeInfo(practice_id).enqueue(new Callback<PracticesData>() {
                @Override
                public void onResponse(Call<PracticesData> call, Response<PracticesData> response) {
                    Log.d("GET", "not success yet");
                    if (response.isSuccessful()){
                        Log.d("GET", "GET Success!");
                        Log.d("GET", ">>>response.body()="+response.body());

                        practicesData = response.body();
                        if (practicesData != null) {
                            scope = practicesData.getScope();
                            setEditView();
                        }
                    }
                    else {
                        System.out.println("@@@@ response is not successful...");
                        System.out.println("@@@@ response code : " + response.code());

                        Toast.makeText(EditPracticeActivity1.this, "연습 정보를 가져오는 데에 실패했습니다.", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                }

                @Override
                public void onFailure(Call<PracticesData> call, Throwable t) {
                    Log.d("GET", "GET Failed");
                    Log.d("GET", t.getMessage());

                    Toast.makeText(EditPracticeActivity1.this, "연습 정보를 가져오는 데에 실패했습니다.", Toast.LENGTH_SHORT).show();
                    finish();
                }
            });
        }
    }

    private void setEditView() {
        editText_title.setText(practicesData.getTitle());

        if (practicesData.getScope().equals("PRIVATE")) {
            tv_scope.setText("비공개");
            iv_scope.setImageResource(R.drawable.ic_star_filled);
            scope = "PRIVATE";
        } else {
            tv_scope.setText("공개");
            iv_scope.setImageResource(R.drawable.ic_star_empty);
            scope = "PUBLIC";
        }
    }

    private void updatePractice() {
        System.out.println("서버에 연습 업데이트 시작");

        RetrofitClient retrofitClient = RetrofitClient.getInstance();

        if (retrofitClient!=null){
            retrofitAPI = RetrofitClient.getRetrofitAPI();

            retrofitAPI.updatePractice(practice_id, practicesData).enqueue(new Callback<Long>() {
                @Override
                public void onResponse(Call<Long> call, Response<Long> response) {
                    Log.d("POST", "not success yet");
                    if (response.isSuccessful()){
                        Log.d("POST", "POST Success!");
                        Log.d("POST", ">>>response.body()="+response.body());
                    }
                    else {
                        System.out.println("@@@@ response is not successful...");
                        System.out.println("@@@@ response code : " + response.code());
                    }
                }

                @Override
                public void onFailure(Call<Long> call, Throwable t) {
                    Log.d("POST", "POST Failed");
                    Log.d("POST", t.getMessage());
                }
            });
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

    // 확인용
    private void printPractice(PracticesData practicesData) {
        System.out.println("id: " + practicesData.getId());
        System.out.println("title: " + practicesData.getTitle());
        System.out.println("user_id: " + practicesData.getUserId());
        System.out.println("scope: " + practicesData.getScope());
        System.out.println("sort: " + practicesData.getSort());
    }
}
