package com.example.myapplication2;

import static com.example.myapplication2.MainActivity.updatePoint;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.myapplication2.api.RetrofitAPI;
import com.example.myapplication2.api.RetrofitClient;
import com.example.myapplication2.api.dto.PostsData;
import com.example.myapplication2.api.dto.PracticesData;
import com.example.myapplication2.api.dto.UserInfoData;
import com.example.myapplication2.api.objects.UserIdObject;

import java.util.ArrayList;
import java.util.Collections;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RegisterActivity extends AppCompatActivity {

    // 로그에 사용할 TAG 변수 선언
    final private String TAG = getClass().getSimpleName();

    // 사용할 컴포넌트 선언
    EditText title_et, content_et;
    Button reg_button;
    Spinner spinner;

    // 유저아이디 변수
    String useridToken = "";

    static Long userId = MainActivity.userId;

    static int selected_practice_mid;
    static Long selected_practice_id;

    static RetrofitAPI retrofitAPI;

    PracticesData[] practicesDataList;
    ArrayList<PracticesData> practicesList;
    ArrayList<String> practiceTitleList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        setTitle("새 게시물 등록");

        //액션바에 뒤로가기 버튼 추가
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

// ListActivity 에서 넘긴 userid 를 변수로 받음
        useridToken = getIntent().getStringExtra("userid");

// 컴포넌트 초기화
        title_et = findViewById(R.id.title_et);
        content_et = findViewById(R.id.content_et);
        reg_button = findViewById(R.id.reg_button);
        spinner = (Spinner) findViewById(R.id.spinner);

        // Spinner 설정
        getUserInfo();

//        dbHelper = new DBHelper(this, 4);
//        db = dbHelper.getWritableDatabase();    // 읽기/쓰기 모드로 데이터베이스를 오픈
//        cursor = db.rawQuery("SELECT * FROM tableName", null);
//        startManagingCursor(cursor);    //엑티비티의 생명주기와 커서의 생명주기를 같게 한다.
//        cursor.moveToFirst();
//
//        //String[] items = {};  //연습 제목 리스트
//        ArrayList<String> itemList = new ArrayList<>();  //연습 제목 리스트
//        do {
//            itemList.add(cursor.getString(1));  //연습 제목
//        } while (cursor.moveToNext());



// 버튼 이벤트 추가
        reg_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //포인트 정보 확인 및 차감
                if (((UserPoint)getApplication()).getUserPoint() < 10)
                    Toast.makeText(RegisterActivity.this, "포인트가 부족하여 게시글을 등록할 수 없습니다.", Toast.LENGTH_SHORT).show();
                else {
                    // 게시물 등록 함수
                    RegBoard regBoard = new RegBoard();
                    regBoard.execute(useridToken, title_et.getText().toString(), content_et.getText().toString());
                }
            }
        });

    }

    class RegBoard extends AsyncTask<String, Void, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            Log.d(TAG, "onPreExecute");
        }


        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            Log.d(TAG, "onPostExecute, " + result);
        }


        @Override
        protected String doInBackground(String... params) {

            //String userid = params[0];
            String title = params[1];
            String content = params[2];

            //////


            String response = makeNewPost(title, content);

            return response;
        }
    }

    private void getUserInfo(){
        System.out.println("HomeActivity1: getUserInfo");  //임시, 확인용
        userId = MainActivity.userId;
        RetrofitClient retrofitClient = RetrofitClient.getInstance();

        practicesDataList = null;
        practiceTitleList = new ArrayList<>();

        if (retrofitClient!=null){
            retrofitAPI = RetrofitClient.getRetrofitAPI();
            System.out.println("userId: " + userId);  //임시, 확인용
            retrofitAPI.getUserInfo(userId).enqueue(new Callback<UserInfoData>() {
                @Override
                public void onResponse(Call<UserInfoData> call, Response<UserInfoData> response) {
                    UserInfoData userInfoData = response.body();
                    if (userInfoData!=null){
                        Log.d("RegisterActivity: GET_USERINFO", "GET SUCCESS");
                        Log.d("RegisterActivity: GET_USERINFO", ">>>response.body()=" + response.body());

                        practicesDataList = userInfoData.getPractices();

                        // 분석 완료된 연습만 게시글을 올릴 수 있도록
//                        for (PracticesData practicesData : practicesDataList) {
//                            if ((practicesData.getAnalysis().getState()).equals("COMPLETE"))
//                                practiceTitleList.add(practicesData.getTitle());
//                        }

                        for (PracticesData practicesData : practicesDataList) {
                            practiceTitleList.add(practicesData.getTitle());
                        }

                        System.out.println("practiceTitleList : " + practiceTitleList);  //임시, 확인용

                        ArrayAdapter<String> adapter = new ArrayAdapter<String>(
                                RegisterActivity.this, android.R.layout.simple_spinner_item, practiceTitleList);
                        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        spinner.setAdapter(adapter);

                        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                            @Override
                            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                                //Toast.makeText(RegisterActivity.this, "spinner 선택", Toast.LENGTH_SHORT).show();  //임시, 확인용
                                selected_practice_id = practicesDataList[i].getId();

//                selected_practice_mid = i;
//                cursor.moveToPosition(i);
//                selected_practice_id = cursor.getInt(11);

                                //Toast.makeText(RegisterActivity.this, practiceTitleList.get(i) + ", " + selected_practice_id, Toast.LENGTH_SHORT).show();  //임시, 확인용
                            }

                            @Override
                            public void onNothingSelected(AdapterView<?> adapterView) {

                            }
                        });

                    }
                }

                @Override
                public void onFailure(Call<UserInfoData> call, Throwable t) {
                    Log.d("RegisterActivity: GET_USERINFO", "GET FAILED");
                }
            });
        }
    }

    private String makeNewPost(String title, String content) {
        System.out.println("서버에 게시물 업로드 시작");

        final String[] res = {"fail"};

        PostsData newpostdata = new PostsData();
        newpostdata.setUserId(userId);
        newpostdata.setPracticeId((long) selected_practice_id);
        newpostdata.setTitle(title);
        newpostdata.setContent(content);
        //newpostdata.setPractices();


        RetrofitClient retrofitClient = RetrofitClient.getInstance();

        if (retrofitClient!=null){
            retrofitAPI = RetrofitClient.getRetrofitAPI();
            retrofitAPI.makeNewPost(newpostdata).enqueue(new Callback<Long>() {
                @Override
                public void onResponse(Call<Long> call, Response<Long> response) {
                    Log.d("POST", "not success yet");
                    if (response.isSuccessful()){
                        Log.d("POST", "POST Success!");
                        Log.d("POST", ">>>response.body()="+response.body());

                        res[0] = "success";

                        Toast.makeText(RegisterActivity.this, "등록되었습니다.", Toast.LENGTH_SHORT).show();

                        // 포인트 -10 차감
                        updatePoint(10, "minus", RegisterActivity.this);

                        finish();
                    }
                    else {
                        System.out.println("@@@@ response is not successful...");
                        System.out.println("@@@@ response code : " + response.code());  //500

                        res[0] = "fail";
                    }
                }

                @Override
                public void onFailure(Call<Long> call, Throwable t) {
                    Log.d("POST", "POST Failed");
                    Log.d("POST", t.getMessage());

                    res[0] = "fail";
                }
            });
        }

        return res[0];
    }
}