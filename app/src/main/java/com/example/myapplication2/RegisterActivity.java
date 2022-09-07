package com.example.myapplication2;

import static com.example.myapplication2.MainActivity.userId;

import androidx.appcompat.app.AppCompatActivity;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
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
import com.example.myapplication2.api.objects.UserIdObject;

import java.net.URL;
import java.util.ArrayList;

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


    static String personName;
    static String idToken;
    static Long userId = MainActivity.userId;
    static int selected_practice_mid;
    static int selected_practice_id;

    static RetrofitAPI retrofitAPI;
    static UserIdObject userIdObject;

    DBHelper dbHelper;
    SQLiteDatabase db = null;
    Cursor cursor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        setTitle("새 게시물 등록");

// ListActivity 에서 넘긴 userid 를 변수로 받음
        useridToken = getIntent().getStringExtra("userid");

// 컴포넌트 초기화
        title_et = findViewById(R.id.title_et);
        content_et = findViewById(R.id.content_et);
        reg_button = findViewById(R.id.reg_button);
        spinner = findViewById(R.id.spinner);

        // Spinner 설정
        dbHelper = new DBHelper(this, 4);
        db = dbHelper.getWritableDatabase();    // 읽기/쓰기 모드로 데이터베이스를 오픈
        cursor = db.rawQuery("SELECT * FROM tableName", null);
        startManagingCursor(cursor);    //엑티비티의 생명주기와 커서의 생명주기를 같게 한다.
        cursor.moveToFirst();

        //String[] items = {};  //연습 제목 리스트
        ArrayList<String> itemList = new ArrayList<>();  //연습 제목 리스트
        do {
            itemList.add(cursor.getString(1));  //연습 제목
        } while (cursor.moveToNext());


        ArrayAdapter<String> adapter = new ArrayAdapter<String>(
                this, android.R.layout.simple_spinner_item, itemList);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                selected_practice_mid = i;
                cursor.moveToPosition(i);
                selected_practice_id = cursor.getInt(11);

                Toast.makeText(RegisterActivity.this, itemList.get(i) + ", " + selected_practice_id, Toast.LENGTH_SHORT).show();  //임시, 확인용
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });


// 버튼 이벤트 추가
        reg_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
// 게시물 등록 함수
                RegBoard regBoard = new RegBoard();
                regBoard.execute(useridToken, title_et.getText().toString(), content_et.getText().toString());
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

//            if(result.equals("success")){
//// 결과값이 success 이면
//// 토스트 메시지를 뿌리고
//// 이전 액티비티(ListActivity)로 이동,
//// 이때 ListActivity 의 onResume() 함수 가 호출되며, 데이터를 새로 고침
//                Toast.makeText(RegisterActivity.this, "등록되었습니다.", Toast.LENGTH_SHORT).show();
//                finish();
//            } else
//            {
//                Toast.makeText(RegisterActivity.this, result, Toast.LENGTH_SHORT).show();
//            }

        }


        @Override
        protected String doInBackground(String... params) {

            //String userid = params[0];
            String title = params[1];
            String content = params[2];

            //////



            String response = makeNewPost(title, content);

            //////


            //String server_url = "http://15.164.252.136/reg_board.php";


            //URL url;
//            String response = "";
//            try {
//                url = new URL(server_url);
//
//                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
//                conn.setReadTimeout(15000);
//                conn.setConnectTimeout(15000);
//                conn.setRequestMethod("POST");
//                conn.setDoInput(true);
//                conn.setDoOutput(true);
//                Uri.Builder builder = new Uri.Builder()
//                        .appendQueryParameter("userid", userid)
//                        .appendQueryParameter("title", title)
//                        .appendQueryParameter("content", content);
//                String query = builder.build().getEncodedQuery();
//
//                OutputStream os = conn.getOutputStream();
//                BufferedWriter writer = new BufferedWriter(
//                        new OutputStreamWriter(os, "UTF-8"));
//                writer.write(query);
//                writer.flush();
//                writer.close();
//                os.close();
//
//                conn.connect();
//                int responseCode=conn.getResponseCode();
//
//                if (responseCode == HttpsURLConnection.HTTP_OK) {
//                    String line;
//                    BufferedReader br=new BufferedReader(new InputStreamReader(conn.getInputStream()));
//                    while ((line=br.readLine()) != null) {
//                        response+=line;
//                    }
//                }
//                else {
//                    response="";
//
//                }
//            } catch (Exception e) {
//                e.printStackTrace();
//            }

            return response;
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