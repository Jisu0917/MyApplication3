package com.example.myapplication2;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.myapplication2.api.RetrofitAPI;
import com.example.myapplication2.api.RetrofitClient;
import com.example.myapplication2.api.dto.FeedbacksData;
import com.example.myapplication2.api.dto.PostsData;
import com.example.myapplication2.api.dto.PracticesData;
import com.example.myapplication2.api.objects.UserIdObject;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

import javax.net.ssl.HttpsURLConnection;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/* 댓글 등록 후 자동 업데이트 되도록 처리!!! */

public class DetailActivity extends AppCompatActivity {

    // 로그에 사용할 TAG
    final private String TAG = getClass().getSimpleName();

    // 사용할 컴포넌트 선언
    TextView title_tv, content_tv, date_tv;
    LinearLayout comment_layout;
//    EditText comment_et;
    Button reg_button, del_button;

//    // 선택한 게시물의 번호
//    String board_seq = "";
//
//    // 유저아이디 변수
//    String userid = "";

    String title = "";
    String content = "";
    Long postId, practiceId;
    Long userId = MainActivity.userId;

    ArrayList<FeedbacksData> feedbacksDataList = new ArrayList<>();


    static String personName;
    static String idToken;

    static RetrofitAPI retrofitAPI;
    static UserIdObject userIdObject;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

// ListActivity 에서 넘긴 변수들을 받아줌
//        board_seq = getIntent().getStringExtra("board_seq");
//        userid = getIntent().getStringExtra("userid");
        title = getIntent().getStringExtra("title");
        content = getIntent().getStringExtra("content");
        postId = getIntent().getLongExtra("postId", 0);
        practiceId = getIntent().getLongExtra("practiceId", 0);

// 컴포넌트 초기화
        title_tv = findViewById(R.id.title_tv);
        content_tv = findViewById(R.id.content_tv);
        date_tv = findViewById(R.id.date_tv);

        title_tv.setText(title);
        content_tv.setText(content);
        date_tv.setText(postId.toString());


        comment_layout = findViewById(R.id.comment_layout);
//        comment_et = findViewById(R.id.comment_et);
        reg_button = findViewById(R.id.reg_button);
        del_button = findViewById(R.id.del_button);
        
        del_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                deletePost();
                finish();
            }
        });

// 등록하기 버튼을 눌렀을 때 댓글 등록 함수 호출
//        reg_button.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                RegCmt regCmt = new RegCmt();
////                regCmt.execute(userid, comment_et.getText().toString(), board_seq);
//            }
//        });

// 해당 게시물의 데이터 불러오기
        InitData();
    }


    private void InitData(){

// 해당 게시물의 데이터를 읽어오는 함수, 파라미터로 보드 번호를 넘김
        LoadBoard loadBoard = new LoadBoard();
        //loadBoard.execute(board_seq);
        loadBoard.execute(postId.toString());
    }

    private void deletePost() {
        System.out.println("서버에서 선택한 게시글 삭제 시작");

        RetrofitClient retrofitClient = RetrofitClient.getInstance();

        if (retrofitClient != null) {
            retrofitAPI = RetrofitClient.getRetrofitAPI();
            retrofitAPI.deletePostId(postId).enqueue(new Callback<Long>() {
                @Override
                public void onResponse(Call<Long> call, Response<Long> response) {
                    Log.d("DELETE", "not success yet");
                    if (response.isSuccessful()) {
                        Log.d("DELETE", "DELETE Success!");
                        Log.d("DELETE", ">>>response.body()=" + response.body());
                    } else {
                        System.out.println("@@@@ response is not successful...");
                        System.out.println("@@@@ response code : " + response.code());
                    }
                }

                @Override
                public void onFailure(Call<Long> call, Throwable t) {
                    Log.d("DELETE", "POST Failed");
                    Log.d("DELETE", t.getMessage());
                }
            });
        }
    }


    class LoadBoard extends AsyncTask<String, Void, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            Log.d(TAG, "LoadBoard : onPreExecute");
        }


        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            Log.d(TAG, "LoadBoard : onPostExecute, " + result);
//            try {
//// 결과값이 JSONArray 형태로 넘어오기 때문에
//// JSONArray, JSONObject 를 사용해서 파싱
//                JSONArray jsonArray = null;
//                jsonArray = new JSONArray(result);
//
//                for(int i=0;i<jsonArray.length();i++){
//                    JSONObject jsonObject = jsonArray.getJSONObject(i);
//
//// Database 의 데이터들을 변수로 저장한 후 해당 TextView 에 데이터 입력
//                    String title = jsonObject.optString("title");
//                    String content = jsonObject.optString("content");
//                    String crt_dt = jsonObject.optString("crt_dt");
//
//                    title_tv.setText(title);
//                    content_tv.setText(content);
//                    date_tv.setText(crt_dt);
//
//                }
//
//// 해당 게시물에 대한 댓글 불러오는 함수 호출, 파라미터로 게시물 번호 넘김
//                LoadCmt loadCmt = new LoadCmt();
////                loadCmt.execute(board_seq);
//
//            } catch (JSONException e) {
//                e.printStackTrace();
//            }

            // 해당 게시물에 대한 댓글 불러오는 함수 호출, 파라미터로 게시물 번호 넘김
                LoadCmt loadCmt = new LoadCmt();
                loadCmt.execute(postId.toString());

        }


        @Override
        protected String doInBackground(String... params) {

            String postid = params[0];

// 호출할 php 파일 경로
            //String server_url = "http://15.164.252.136/load_board_detail.php";


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
//                        .appendQueryParameter("board_seq", board_seq);
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


    // 게시물의 댓글을 읽어오는 함수
    class LoadCmt extends AsyncTask<String, Void, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            Log.d(TAG, "LoadCmt : onPreExecute");
        }


        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            Log.d(TAG, "LoadCmt : onPostExecute, " + result);
//
//// 댓글을 뿌릴 LinearLayout 자식뷰 모두 제거
//            comment_layout.removeAllViews();
//
//            //// JSONArray, JSONObject 로 받은 데이터 파싱
////                JSONArray jsonArray = null;
////                jsonArray = new JSONArray(result);
//
//// custom_comment 를 불러오기 위한 객체
//            System.out.println("111111111111111");  // 임시, 확인용
//            LayoutInflater layoutInflater = LayoutInflater.from(DetailActivity.this);
//            //LayoutInflater layoutInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
//
//
//            if (feedbacksDataList != null) {
//                System.out.println("2222222222222222");  // 임시, 확인용
//                System.out.println("feedbacksDataList : " + feedbacksDataList.toString());  // 임시, 확인용
//                for (int i=0; i<feedbacksDataList.size(); i++) {
//                    System.out.println("33333333333333333333");  // 임시, 확인용
//                    View customView = layoutInflater.inflate(R.layout.custom_comment, null);
//                    System.out.println("4444444444444444444444");  // 임시, 확인용
//                    FeedbacksData feedback = feedbacksDataList.get(i);
//
////                        Long feedbackId = feedback.getId();
////                        String initiator = feedback.getInitiator(); //USER, FRIEND
//                    int speed_score = feedback.getSpeed_score();
//                    String speed_comment = feedback.getSpeed_comment();
//                    int tone_score = feedback.getTone_score();
//                    String tone_comment = feedback.getTone_comment();
//                    int closing_score = feedback.getClosing_score();
//                    String closing_comment = feedback.getClosing_comment();
//
//                    System.out.println("44444444444444444");  // 임시, 확인용
//
//                    //임시, 확인용
//                    System.out.println("speed : "+speed_score + ", " + speed_comment);
//                    System.out.println("tone : "+tone_score + ", " + tone_comment);
//                    System.out.println("closing : "+closing_score + ", " + closing_comment);
//
//                    ((TextView)customView.findViewById(R.id.cmt_speedscore_tv)).setText(speed_score);
//                    ((TextView)customView.findViewById(R.id.cmt_speedcmt_tv)).setText(speed_comment);
//                    ((TextView)customView.findViewById(R.id.cmt_tonescore_tv)).setText(tone_score);
//                    ((TextView)customView.findViewById(R.id.cmt_tonecmt_tv)).setText(tone_comment);
//                    ((TextView)customView.findViewById(R.id.cmt_closingscore_tv)).setText(closing_score);
//                    ((TextView)customView.findViewById(R.id.cmt_closingcmt_tv)).setText(closing_comment);
//
//                    // 댓글 레이아웃에 custom_comment 의 디자인에 데이터를 담아서 추가
//                    comment_layout.addView(customView);
//
//
//                }
//
//            } else {
//                System.out.println("LoadCmt : onPostExecute : feedbacksDataList.size is 0...");
//            }


//                for(int i=0;i<jsonArray.length();i++){
//
//// custom_comment 의 디자인을 불러와서 사용
//                    View customView = layoutInflater.inflate(R.layout.custom_comment, null);
//                    JSONObject jsonObject = jsonArray.getJSONObject(i);
//
//                    String userid= jsonObject.optString("userid");
//                    String content = jsonObject.optString("content");
//                    String crt_dt = jsonObject.optString("crt_dt");
//
//                    ((TextView)customView.findViewById(R.id.cmt_userid_tv)).setText(userid);
//                    ((TextView)customView.findViewById(R.id.cmt_content_tv)).setText(content);
//                    ((TextView)customView.findViewById(R.id.cmt_date_tv)).setText(crt_dt);
//
//// 댓글 레이아웃에 custom_comment 의 디자인에 데이터를 담아서 추가
//                    comment_layout.addView(customView);
//                }

        }


        @Override
        protected String doInBackground(String... params) {

            ////
            getFeedbackOfUsers();
            ////


//            String board_seq = params[0];
//            String server_url = "http://15.164.252.136/load_cmt.php";


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
//                        .appendQueryParameter("board_seq", board_seq);
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

    private void getFeedbackOfUsers() {
        System.out.println("서버에서 해당 게시물의 user 댓글 가져오기 시작");

        RetrofitClient retrofitClient = RetrofitClient.getInstance();

        if (retrofitClient!=null){
            retrofitAPI = RetrofitClient.getRetrofitAPI();
            retrofitAPI.getFeedbackOfUsers(practiceId).enqueue(new Callback<ArrayList<FeedbacksData>>() {
                @Override
                public void onResponse(Call<ArrayList<FeedbacksData>> call, Response<ArrayList<FeedbacksData>> response) {
                    if (response.isSuccessful()){
                        feedbacksDataList = response.body();
                        if (feedbacksDataList != null) {
                            Log.d("GET_USERS_FEEDBACKS", "GET SUCCESS");
                            Log.d("GET_USERS_FEEDBACKS", feedbacksDataList.toString());

                            setCommentView();
                        } else {
                            System.out.println("GET_USERS_FEEDBACKS : feedbacksDataList is null...");
                        }
                    }
                    else {
                        System.out.println("@@@@ user feedback get : response is not successful...");
                        System.out.println("@@@@ user feedback get : response code : " + response.code());
                    }
                }

                @Override
                public void onFailure(Call<ArrayList<FeedbacksData>> call, Throwable throwable) {
                    Log.d("GET_USERS_FEEDBACKS", "GET FAILED");
                }
            });
        }
    }

    private void setCommentView() {
        comment_layout.removeAllViews();

        //// JSONArray, JSONObject 로 받은 데이터 파싱
//                JSONArray jsonArray = null;
//                jsonArray = new JSONArray(result);

// custom_comment 를 불러오기 위한 객체
        LayoutInflater layoutInflater = LayoutInflater.from(DetailActivity.this);
        //LayoutInflater layoutInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);


        if (feedbacksDataList != null) {
            for (int i=0; i<feedbacksDataList.size(); i++) {
                View customView = layoutInflater.inflate(R.layout.custom_comment, null);
                FeedbacksData feedback = feedbacksDataList.get(i);

//                        Long feedbackId = feedback.getId();
//                        String initiator = feedback.getInitiator(); //USER, FRIEND
                int speed_score = feedback.getSpeed_score();
                String speed_comment = feedback.getSpeed_comment();
                int tone_score = feedback.getTone_score();
                String tone_comment = feedback.getTone_comment();
                int closing_score = feedback.getClosing_score();
                String closing_comment = feedback.getClosing_comment();

                //임시, 확인용
                System.out.println("speed : "+speed_score + ", " + speed_comment);
                System.out.println("tone : "+tone_score + ", " + tone_comment);
                System.out.println("closing : "+closing_score + ", " + closing_comment);

                ((TextView)customView.findViewById(R.id.cmt_speedscore_tv)).setText(String.valueOf(speed_score));
                ((TextView)customView.findViewById(R.id.cmt_speedcmt_tv)).setText(speed_comment);
                ((TextView)customView.findViewById(R.id.cmt_tonescore_tv)).setText(String.valueOf(tone_score));
                ((TextView)customView.findViewById(R.id.cmt_tonecmt_tv)).setText(tone_comment);
                ((TextView)customView.findViewById(R.id.cmt_closingscore_tv)).setText(String.valueOf(closing_score));
                ((TextView)customView.findViewById(R.id.cmt_closingcmt_tv)).setText(closing_comment);

                // 댓글 레이아웃에 custom_comment 의 디자인에 데이터를 담아서 추가
                comment_layout.addView(customView);


            }

        } else {
            System.out.println("LoadCmt : onPostExecute : feedbacksDataList is null...");
        }
    }


    // 댓글 등록 다이얼로그 띄우기
    public void showCmtDialog(View view) {
        View dialogView = (View) View.inflate(
                this, R.layout.dialog_cmt, null);
        AlertDialog.Builder dig = new AlertDialog.Builder(this, R.style.Theme_Dialog);
        dig.setView(dialogView);
        dig.setTitle("FEEDBACK");

        final EditText et_speedscore = (EditText)dialogView.findViewById(R.id.et_speedscore);
        final EditText et_speedcmt = (EditText)dialogView.findViewById(R.id.et_speedcmt);
        final EditText et_tonescore = (EditText)dialogView.findViewById(R.id.et_tonescore);
        final EditText et_tonecmt = (EditText)dialogView.findViewById(R.id.et_tonecmt);
        final EditText et_closingscore = (EditText)dialogView.findViewById(R.id.et_closingscore);
        final EditText et_closingcmt = (EditText)dialogView.findViewById(R.id.et_closingcmt);
        //editText.requestFocus();

        dig.setNegativeButton("닫기", null);
        dig.setPositiveButton("확인", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (et_speedscore.getText().toString().equals("") ||
                        et_speedcmt.getText().toString().equals("") ||
                        et_tonescore.getText().toString().equals("") ||
                        et_tonecmt.getText().toString().equals("") ||
                        et_closingscore.getText().toString().equals("") ||
                        et_closingcmt.getText().toString().equals("")
                ) {
                    Toast.makeText(getApplicationContext(), "입력하지 않은 항목이 있습니다.", Toast.LENGTH_SHORT).show();
                    /// 다이얼로그 닫히지 않게 처리!!!
                } else {
                    FeedbacksData feedback = new FeedbacksData();
                    feedback.setSpeed_score(Integer.parseInt(et_speedscore.getText().toString()));
                    feedback.setSpeed_comment(et_speedcmt.getText().toString());
                    feedback.setTone_score(Integer.parseInt(et_tonescore.getText().toString()));
                    feedback.setTone_comment(et_tonecmt.getText().toString());
                    feedback.setClosing_score(Integer.parseInt(et_closingscore.getText().toString()));
                    feedback.setClosing_comment(et_closingcmt.getText().toString());
                    feedback.setInitiator("USER");  // 임시, 확인용
                    feedback.setUserId(userId);
                    feedback.setPracticeId(practiceId);

                    System.out.println("Dialog 확인 : feedback - speedcmt: " + feedback.getSpeed_comment());  // 임시, 확인용;
                RegCmt regCmt = new RegCmt();
                regCmt.execute(feedback);
                }
            }
        });
        dig.show();
    }

    // 댓글을 등록하는 함수
    class RegCmt extends AsyncTask<FeedbacksData, Void, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            Log.d(TAG, "RegCmt : onPreExecute");
        }


        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            Log.d(TAG, "RegCmt : onPostExecute, " + result);

// 결과값이 성공으로 나오면
            if(result.equals("success")){

//댓글 입력창의 글자는 공백으로 만듦
//                comment_et.setText("");

// 소프트 키보드 숨김처리
//                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
//                imm.hideSoftInputFromWindow(comment_et.getWindowToken(), 0);

// 토스트메시지 출력
                Toast.makeText(DetailActivity.this, "댓글이 등록되었습니다.", Toast.LENGTH_SHORT).show();

// 댓글 불러오는 함수 호출
                LoadCmt loadCmt = new LoadCmt();
                loadCmt.execute(postId.toString());
            }else
            {
                Toast.makeText(DetailActivity.this, result, Toast.LENGTH_SHORT).show();
            }
        }


        @Override
        protected String doInBackground(FeedbacksData... params) {

            FeedbacksData feedbacksData = params[0];

            ////
            makeNewFeedback(feedbacksData);
            ////

//            String userid = params[0];
//            String content = params[1];
//            String board_seq = params[2];

            //String server_url = "http://15.164.252.136/reg_comment.php";


            URL url;
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
//                        .appendQueryParameter("content", content)
//                        .appendQueryParameter("board_seq", board_seq);
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

    private void makeNewFeedback(FeedbacksData feedback) {
        System.out.println("서버에 피드백(댓글) 업로드 시작");

        RetrofitClient retrofitClient = RetrofitClient.getInstance();

        if (retrofitClient!=null){
            retrofitAPI = RetrofitClient.getRetrofitAPI();
            retrofitAPI.makeNewFeedback(feedback).enqueue(new Callback<Long>() {
                @Override
                public void onResponse(Call<Long> call, Response<Long> response) {
                    Log.d("POST", "not success yet");
                    if (response.isSuccessful()){
                        Log.d("POST", "POST Success!");
                        Log.d("POST", ">>>response.body()="+response.body());
                    }
                    else {
                        System.out.println("@@@@ feedback post : response is not successful...");
                        System.out.println("@@@@ feedback post : response code : " + response.code());  //500
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