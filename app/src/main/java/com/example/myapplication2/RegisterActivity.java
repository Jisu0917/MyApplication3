package com.example.myapplication2;

import static com.example.myapplication2.MainActivity.userId;

import androidx.appcompat.app.AppCompatActivity;

import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.myapplication2.api.RetrofitAPI;
import com.example.myapplication2.api.RetrofitClient;
import com.example.myapplication2.api.dto.PostsData;
import com.example.myapplication2.api.dto.PracticesData;
import com.example.myapplication2.api.objects.UserIdObject;

import java.net.URL;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RegisterActivity extends AppCompatActivity {

    // 로그에 사용할 TAG 변수 선언
    final private String TAG = getClass().getSimpleName();

    // 사용할 컴포넌트 선언
    EditText title_et, content_et;
    Button reg_button;

    // 유저아이디 변수
    String useridToken = "";



    static String personName;
    static String idToken;
    static Long userId = MainActivity.userId;

    static RetrofitAPI retrofitAPI;
    static UserIdObject userIdObject;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

// ListActivity 에서 넘긴 userid 를 변수로 받음
        useridToken = getIntent().getStringExtra("userid");

// 컴포넌트 초기화
        title_et = findViewById(R.id.title_et);
        content_et = findViewById(R.id.content_et);
        reg_button = findViewById(R.id.reg_button);

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

            //임시, 확인용
            result = "success";

            if(result.equals("success")){
// 결과값이 success 이면
// 토스트 메시지를 뿌리고
// 이전 액티비티(ListActivity)로 이동,
// 이때 ListActivity 의 onResume() 함수 가 호출되며, 데이터를 새로 고침
                Toast.makeText(RegisterActivity.this, "등록되었습니다.", Toast.LENGTH_SHORT).show();
                finish();
            }else
            {
                Toast.makeText(RegisterActivity.this, result, Toast.LENGTH_SHORT).show();
            }

        }


        @Override
        protected String doInBackground(String... params) {

            //String userid = params[0];
            String title = params[1];
            String content = params[2];

            //////



            makeNewPost(title, content);

            //////


            //String server_url = "http://15.164.252.136/reg_board.php";


            //URL url;
            String response = "";
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

    private void makeNewPost(String title, String content) {
        System.out.println("서버에 게시물 업로드 시작");

        PostsData newpostdata = new PostsData();
        newpostdata.setUserId(userId);
        newpostdata.setPracticeId(8L);  // 임시, 확인용 - register activity에 어떤 연습인지 선택하는 기능 추가해야함!
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
                    }
                    else {
                        System.out.println("@@@@ response is not successful...");
                        System.out.println("@@@@ response code : " + response.code());  //500
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
}