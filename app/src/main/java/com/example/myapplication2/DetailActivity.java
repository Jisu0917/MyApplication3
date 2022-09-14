package com.example.myapplication2;

import static com.example.myapplication2.MainActivity.updatePoint;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.myapplication2.api.RetrofitAPI;
import com.example.myapplication2.api.RetrofitClient;
import com.example.myapplication2.api.dto.FeedbacksData;
import com.example.myapplication2.api.dto.PostsData;
import com.example.myapplication2.api.dto.UserInfoData;
import com.example.myapplication2.api.objects.UserIdObject;

import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/* 댓글 등록 후 자동 업데이트 되도록 처리!!! */

public class DetailActivity extends AppCompatActivity {
    // 로그에 사용할 TAG
    final private String TAG = getClass().getSimpleName();

    Toolbar toolbar;
    ActionBar actionBar;

    // 사용할 컴포넌트 선언
    ImageView iv_profile;
    TextView tv_post_user_name, title_tv, content_tv, date_tv, practiceId_tv;
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
    Long postId, post_user_id, practiceId, practice_user_id;
    Long userId;

    ArrayList<FeedbacksData> feedbackOfUsersDataList = new ArrayList<>();
    ArrayList<FeedbacksData> feedbackOfFriendsDataList = new ArrayList<>();
    TreeMap<Long, FeedbacksData> feedbackDataMap = new TreeMap<Long, FeedbacksData>();

    static RetrofitAPI retrofitAPI;


    // 음원 파일 재생
    MediaPlayer mp;
    int pos;  //재생 멈춘 시점
    private Button btn_start, btn_pause, btn_restart, btn_stop;
    SeekBar sb;  //음악 재생위치를 나타내는 시크바
    boolean isPlaying = false;
    String url = "";

    class WavPlayThread extends Thread {
        @Override
        public void run() {
            while(isPlaying) {
                sb.setProgress(mp.getCurrentPosition());
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        setTitle("게시글 보기");
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        //툴바
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        actionBar = getSupportActionBar();
        actionBar.setDisplayShowCustomEnabled(true);
        actionBar.setDisplayShowTitleEnabled(false);//기본 제목을 없애줍니다.
        actionBar.setDisplayHomeAsUpEnabled(true);

//        //액션바에 뒤로가기 버튼 추가
//        ActionBar actionBar = getSupportActionBar();
//        actionBar.setDisplayHomeAsUpEnabled(true);

// ListActivity 에서 넘긴 변수들을 받아줌
//        board_seq = getIntent().getStringExtra("board_seq");
//        userid = getIntent().getStringExtra("userid");
        title = getIntent().getStringExtra("title");
        content = getIntent().getStringExtra("content");
        postId = getIntent().getLongExtra("postId", 0);
        practiceId = getIntent().getLongExtra("practiceId", 0);
        practice_user_id = getIntent().getLongExtra("practice_user_id", 0);

        userId = getIntent().getLongExtra("userId", 0);

// 컴포넌트 초기화
        iv_profile = (ImageView) findViewById(R.id.iv_profile);
        tv_post_user_name = (TextView) findViewById(R.id.tv_post_user_name);
        title_tv = findViewById(R.id.title_tv);
        content_tv = findViewById(R.id.content_tv);
        date_tv = findViewById(R.id.date_tv);
        practiceId_tv = findViewById(R.id.practiceId_tv);

        title_tv.setText(title);
        content_tv.setText(content);
        date_tv.setText(postId.toString());
        practiceId_tv.setText(practiceId.intValue() + "");


        comment_layout = findViewById(R.id.comment_layout);
//        comment_et = findViewById(R.id.comment_et);
        reg_button = findViewById(R.id.reg_button);
        del_button = findViewById(R.id.del_button);

        getPost();
        
        del_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //삭제하시겠습니까? 다이얼로그 띄우기
                AlertDialog.Builder builder = new AlertDialog.Builder(DetailActivity.this);
                builder.setTitle("삭제하시겠습니까?");
                builder.setPositiveButton("예",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                deletePost();
                                finish();
                            }
                        });
                builder.setNegativeButton("아니오",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {

                            }
                        });
                builder.show();
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


        // wav 파일 재생
        sb = (SeekBar) findViewById(R.id.seekBar);
        sb.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                if (seekBar.getMax() == i) {
                    btn_start.setVisibility(View.VISIBLE);
                    btn_pause.setVisibility(View.GONE);
                    btn_restart.setVisibility(View.GONE);
                    isPlaying = false;
                    mp.stop();
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                isPlaying = false;
                mp.pause();
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                isPlaying = true;
                int ttt = seekBar.getProgress();  //사용자가 움직여놓은 위치
                mp.seekTo(ttt);
                mp.start();
                new WavPlayThread().start();
            }
        });

        btn_start = (Button) findViewById(R.id.btn_start);
        btn_pause = (Button) findViewById(R.id.btn_pause);
        btn_restart = (Button) findViewById(R.id.btn_restart);

        btn_start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // MediaPlayer 객체 초기화, 재생
                //mp = MediaPlayer.create(getApplicationContext(), R.raw.test_wav_subway);  //임시, 확인용 - 서버에서 받아온 wav 파일 사용
                playWavFile();
                System.out.println("wav file url : " + url);  //임시, 확인용
                if (!url.equals("")) {
                    btn_start.setVisibility(View.GONE);
                    btn_pause.setVisibility(View.VISIBLE);
                    btn_restart.setVisibility(View.GONE);
                }
            }
        });

        btn_pause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pos = mp.getCurrentPosition();
                mp.pause();

                btn_start.setVisibility(View.GONE);
                btn_pause.setVisibility(View.GONE);
                btn_restart.setVisibility(View.VISIBLE);
            }
        });

        btn_restart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mp.seekTo(pos);
                mp.start();

                btn_start.setVisibility(View.GONE);
                btn_pause.setVisibility(View.VISIBLE);
                btn_restart.setVisibility(View.GONE);

                isPlaying = true;
                new WavPlayThread().start();
            }
        });
    }  // end of onCreate()

    @Override
    protected void onPause() {
        super.onPause();

//        Toast.makeText(DetailActivity.this, "onPause() 실행", Toast.LENGTH_SHORT).show();
        isPlaying = false;

        if (mp != null) {
            mp.release();
        }

        btn_start.setVisibility(View.VISIBLE);
        btn_pause.setVisibility(View.GONE);
        btn_restart.setVisibility(View.GONE);
    }

    private void playWavFile() {
        //https://sookpeech-wavfile.s3.ap-northeast-2.amazonaws.com/{practice_id}/{user_id}_{practice_id}.wav
        url = "https://sookpeech-wavfile.s3.ap-northeast-2.amazonaws.com/"+practiceId+"/"+practice_user_id+"_"+practiceId+".wav";
//        url = "http://mm.sookmyung.ac.kr/~sblim/lec/web-int/data/song.mp3";  //임시, 확인용

        // 미디어플레이어 설정
        mp = MediaPlayer.create(getApplicationContext(), Uri.parse(url));
        mp.setLooping(false);
        mp.start();  //음원 재생 시작

        int duration = mp.getDuration();  //음원의 재생시간(miliSecond)
        sb.setMax(duration);
        new WavPlayThread().start();
        isPlaying = true;
    }

    // 서버로부터 wav 파일 받아오기
//    private void getWavFile() {
//        System.out.println("서버로부터 wav 파일 받아오기 시작");
//
//        RetrofitClient4 retrofitClient = RetrofitClient4.getInstance();
//
//        if (retrofitClient!=null){
//            retrofitAPI = RetrofitClient4.getRetrofitAPI();
//            retrofitAPI.getWavFile(userId.intValue(), practiceId.intValue()).enqueue(new Callback<String>() {
//                @Override
//                public void onResponse(Call<String> call, Response<String> response) {
//                    Log.d("GET", "not success yet");
//                    if (response.isSuccessful()){
//                        url = response.body();
//                        if (!url.equals("")) {
//                            Log.d("GET_WAV_FILE", "GET SUCCESS");
//                            Log.d("GET_WAV_FILE", url);
//
//                            // 미디어플레이어 설정
//                            mp = MediaPlayer.create(getApplicationContext(), Uri.parse(url));
//                            mp.setLooping(false);
//                            mp.start();  //음원 재생 시작
//
//                            int duration = mp.getDuration();  //음원의 재생시간(miliSecond)
//                            sb.setMax(duration);
//                            new WavPlayThread().start();
//                            isPlaying = true;
//                        } else {
//                            System.out.println("GET_WAV_FILE : url is null...");
//                        }
//                    }
//                    else {
//                        System.out.println("@@@@ getWavFile : response is not successful...");
//                        System.out.println("@@@@ getWavFile : response code : " + response.code());  //404
//                    }
//                }
//
//                @Override
//                public void onFailure(Call<String> call, Throwable t) {
//                    Log.d("GET_WAV_FILE", "GET FAILED");
//                }
//            });
//        }
//    }


    private void InitData(){

// 해당 게시물의 데이터를 읽어오는 함수, 파라미터로 보드 번호를 넘김
        LoadBoard loadBoard = new LoadBoard();
        //loadBoard.execute(board_seq);
        loadBoard.execute(postId.toString());
    }

    private void getPost() {
        System.out.println("DetailActivity: getPost");  //임시, 확인용
        RetrofitClient retrofitClient = RetrofitClient.getInstance();

        if (retrofitClient!=null){
            retrofitAPI = RetrofitClient.getRetrofitAPI();
            retrofitAPI.getPost(postId).enqueue(new Callback<PostsData>() {
                @Override
                public void onResponse(Call<PostsData> call, Response<PostsData> response) {
                    PostsData post = response.body();
                    if (post != null) {
                        post_user_id = post.getUserId();

                        if (post_user_id.equals(userId)) {
                            reg_button.setVisibility(View.GONE);
                            del_button.setVisibility(View.VISIBLE);
                        }
                        else {
                            reg_button.setVisibility(View.VISIBLE);
                            del_button.setVisibility(View.GONE);
                        }

                        getUserInfo(new UserIdObject(post_user_id));
                    }
                }

                @Override
                public void onFailure(Call<PostsData> call, Throwable t) {
                    Log.d("GET_POST", "GET FAILED");
                }
            });
        }
    }

    private void getUserInfo(UserIdObject userIdObject){
        RetrofitClient retrofitClient = RetrofitClient.getInstance();

        if (retrofitClient!=null){
            retrofitAPI = RetrofitClient.getRetrofitAPI();
            retrofitAPI.getUserInfo(userIdObject.getId()).enqueue(new Callback<UserInfoData>() {
                @Override
                public void onResponse(Call<UserInfoData> call, Response<UserInfoData> response) {
                    UserInfoData userInfoData = response.body();
                    if (userInfoData!=null){
                        Log.d("GET_USERINFO", "GET SUCCESS");
                        Log.d("GET_USERINFO", "MainActivity: getUserInfo - response.body().getPoint()=" + response.body().getPoint());

                        String post_user_name = userInfoData.getName();
                        String post_user_picture = userInfoData.getPicture();

                        tv_post_user_name.setText(post_user_name);
                        if (post_user_picture!=null) Glide.with(DetailActivity.this).load(post_user_picture).into(iv_profile);
                    }
                }

                @Override
                public void onFailure(Call<UserInfoData> call, Throwable t) {
                    Log.d("GET_USERINFO", "GET FAILED");
                }
            });
        }
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
            getFeedbackOfFriends();
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
                        feedbackOfUsersDataList = response.body();
                        if (feedbackOfUsersDataList != null) {
                            Log.d("GET_USERS_FEEDBACKS", "GET SUCCESS");
                            Log.d("GET_USERS_FEEDBACKS", feedbackOfUsersDataList.toString());

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

    private void getFeedbackOfFriends() {
        System.out.println("서버에서 해당 게시물의 friend 댓글 가져오기 시작");

        RetrofitClient retrofitClient = RetrofitClient.getInstance();

        if (retrofitClient!=null){
            retrofitAPI = RetrofitClient.getRetrofitAPI();
            retrofitAPI.getFeedbackOfFriends(practiceId).enqueue(new Callback<ArrayList<FeedbacksData>>() {
                @Override
                public void onResponse(Call<ArrayList<FeedbacksData>> call, Response<ArrayList<FeedbacksData>> response) {
                    if (response.isSuccessful()){
                        feedbackOfFriendsDataList = response.body();
                        if (feedbackOfFriendsDataList != null) {
                            Log.d("GET_FRIENDS_FEEDBACKS", "GET SUCCESS");
                            Log.d("GET_FRIENDS_FEEDBACKS", feedbackOfFriendsDataList.toString());

                            setCommentView();
                        } else {
                            System.out.println("GET_FRIENDS_FEEDBACKS : feedbacksDataList is null...");
                        }
                    }
                    else {
                        System.out.println("@@@@ friends feedback get : response is not successful...");
                        System.out.println("@@@@ friends feedback get : response code : " + response.code());
                    }
                }

                @Override
                public void onFailure(Call<ArrayList<FeedbacksData>> call, Throwable throwable) {
                    Log.d("GET_FRIENDS_FEEDBACKS", "GET FAILED");
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


        if (feedbackOfUsersDataList != null && feedbackOfFriendsDataList != null) {
            if (feedbackOfUsersDataList.size() == 0 && feedbackOfFriendsDataList.size() == 0) {  //피드백이 1개도 없을 때
                View customView = layoutInflater.inflate(R.layout.custom_textview, null);

                ((TextView)customView.findViewById(R.id.custom_textView)).setText("아직 받은 피드백이 없습니다.");
                comment_layout.addView(customView);
            } else {  //피드백이 1개 이상 있을 때
                //User, Friend 피드백 리스트 합친 후 id순으로 정렬
                for (int i = 0; i < feedbackOfUsersDataList.size(); i++) {
                    feedbackDataMap.put(feedbackOfUsersDataList.get(i).getId(), feedbackOfUsersDataList.get(i));
                }
                for (int i = 0; i < feedbackOfFriendsDataList.size(); i++) {
                    feedbackDataMap.put(feedbackOfFriendsDataList.get(i).getId(), feedbackOfFriendsDataList.get(i));
                }
//                Object[] mapkey = feedbackDataMap.keySet().toArray();
//                Arrays.sort(mapkey);
//
                System.out.println("feedbackDataMap.keySet(): " + feedbackDataMap.keySet());  //임시, 확인용

                //뷰 구성하기
                int cmt_count = 1;
                for (Long nKey : feedbackDataMap.keySet()) {
                    View customView = layoutInflater.inflate(R.layout.custom_comment, null);
                    FeedbacksData feedback = feedbackDataMap.get(nKey);

//                        Long feedbackId = feedback.getId();
//                        String initiator = feedback.getInitiator(); //USER, FRIEND
                    int speed_score = feedback.getSpeed_score();
                    String speed_comment = feedback.getSpeed_comment();
                    int tone_score = feedback.getTone_score();
                    String tone_comment = feedback.getTone_comment();
                    int closing_score = feedback.getClosing_score();
                    String closing_comment = feedback.getClosing_comment();
                    String initiator = "";
                    if (feedback.getInitiator().equals("USER"))
                        initiator = "익명";
                    else if (feedback.getInitiator().equals("FRIEND"))
                        initiator = "친구";

                    //임시, 확인용
                    System.out.println("speed : " + speed_score + ", " + speed_comment);
                    System.out.println("tone : " + tone_score + ", " + tone_comment);
                    System.out.println("closing : " + closing_score + ", " + closing_comment);

                    ((TextView)customView.findViewById(R.id.cmt_count)).setText(initiator + "의 댓글 " + cmt_count);
                    ((TextView) customView.findViewById(R.id.cmt_speedscore_tv)).setText(numberToStars(speed_score));
                    ((TextView) customView.findViewById(R.id.cmt_speedcmt_tv)).setText(speed_comment);
                    ((TextView) customView.findViewById(R.id.cmt_tonescore_tv)).setText(numberToStars(tone_score));
                    ((TextView) customView.findViewById(R.id.cmt_tonecmt_tv)).setText(tone_comment);
                    ((TextView) customView.findViewById(R.id.cmt_closingscore_tv)).setText(numberToStars(closing_score));
                    ((TextView) customView.findViewById(R.id.cmt_closingcmt_tv)).setText(closing_comment);

                    // 댓글 레이아웃에 custom_comment 의 디자인에 데이터를 담아서 추가
                    comment_layout.addView(customView);

                    cmt_count++;
                }


//                int cmt_count = 1;
//                for (int i = 0; i < feedbackOfUsersDataList.size(); i++) {
//                    View customView = layoutInflater.inflate(R.layout.custom_comment, null);
//                    FeedbacksData feedback = feedbackOfUsersDataList.get(i);
//
////                        Long feedbackId = feedback.getId();
////                        String initiator = feedback.getInitiator(); //USER, FRIEND
//                    int speed_score = feedback.getSpeed_score();
//                    String speed_comment = feedback.getSpeed_comment();
//                    int tone_score = feedback.getTone_score();
//                    String tone_comment = feedback.getTone_comment();
//                    int closing_score = feedback.getClosing_score();
//                    String closing_comment = feedback.getClosing_comment();
//                    String initiator = "";
//                    if (feedback.getInitiator().equals("USER"))
//                        initiator = "익명";
//                    else if (feedback.getInitiator().equals("FRIEND"))
//                        initiator = "친구";
//
//                    //임시, 확인용
//                    System.out.println("speed : " + speed_score + ", " + speed_comment);
//                    System.out.println("tone : " + tone_score + ", " + tone_comment);
//                    System.out.println("closing : " + closing_score + ", " + closing_comment);
//
//                    ((TextView)customView.findViewById(R.id.cmt_count)).setText(initiator + "의 댓글 " + cmt_count);
//                    ((TextView) customView.findViewById(R.id.cmt_speedscore_tv)).setText(numberToStars(speed_score));
//                    ((TextView) customView.findViewById(R.id.cmt_speedcmt_tv)).setText(speed_comment);
//                    ((TextView) customView.findViewById(R.id.cmt_tonescore_tv)).setText(numberToStars(tone_score));
//                    ((TextView) customView.findViewById(R.id.cmt_tonecmt_tv)).setText(tone_comment);
//                    ((TextView) customView.findViewById(R.id.cmt_closingscore_tv)).setText(numberToStars(closing_score));
//                    ((TextView) customView.findViewById(R.id.cmt_closingcmt_tv)).setText(closing_comment);
//
//                    // 댓글 레이아웃에 custom_comment 의 디자인에 데이터를 담아서 추가
//                    comment_layout.addView(customView);
//
//                    cmt_count++;
//                }
            }

        } else {
            System.out.println("LoadCmt : onPostExecute : feedbackOfUsersDataList is null...");
        }
    }

    // (댓글) 숫자 to 별점 변환
    private String numberToStars(int num) {
        if (num >= 0 && num <= 5) {
            switch (num) {
                case 0:
                    return "☆☆☆☆☆";
                case 1:
                    return "★☆☆☆☆";
                case 2:
                    return "★★☆☆☆";
                case 3:
                    return "★★★☆☆";
                case 4:
                    return "★★★★☆";
                case 5:
                    return "★★★★★";
                default:
                    return "";
            }
        } else
            return "";
    }


    // 댓글 등록 다이얼로그 띄우기
    public void showCmtDialog(View view) {
        View dialogView = (View) View.inflate(
                this, R.layout.dialog_cmt, null);
        AlertDialog.Builder dig = new AlertDialog.Builder(this, R.style.Theme_Dialog);
        dig.setView(dialogView);
        dig.setTitle("연습에 대한 피드백을 입력하세요.");

        final RatingBar ratingBar_speed = (RatingBar) dialogView.findViewById(R.id.ratingbar_speed);
        final RatingBar ratingBar_tone = (RatingBar) dialogView.findViewById(R.id.ratingbar_tone);
        final RatingBar ratingBar_closing = (RatingBar) dialogView.findViewById(R.id.ratingbar_closing);

        final EditText et_speedcmt = (EditText)dialogView.findViewById(R.id.et_speedcmt);
        final EditText et_tonecmt = (EditText)dialogView.findViewById(R.id.et_tonecmt);
        final EditText et_closingcmt = (EditText)dialogView.findViewById(R.id.et_closingcmt);
        //editText.requestFocus();

        dig.setNegativeButton("닫기", null);
        dig.setPositiveButton("확인", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (et_speedcmt.getText().toString().equals("") ||
                        et_tonecmt.getText().toString().equals("") ||
                        et_closingcmt.getText().toString().equals("")
                ) {
                    Toast.makeText(getApplicationContext(), "입력하지 않은 항목이 있습니다.", Toast.LENGTH_SHORT).show();
                    /// 다이얼로그 닫히지 않게 처리!!!
                } else {
                    FeedbacksData feedback = new FeedbacksData();
                    feedback.setSpeed_score((int) ratingBar_speed.getRating());
                    feedback.setSpeed_comment(et_speedcmt.getText().toString());
                    feedback.setTone_score((int) ratingBar_tone.getRating());
                    feedback.setTone_comment(et_tonecmt.getText().toString());
                    feedback.setClosing_score((int) ratingBar_closing.getRating());
                    feedback.setClosing_comment(et_closingcmt.getText().toString());
                    feedback.setInitiator("USER");
                    feedback.setUserId(userId);
                    feedback.setPracticeId(practiceId);

                    System.out.println("Dialog 확인 : feedback - speedcmt: " + feedback.getSpeed_comment());  // 임시, 확인용;
                RegCmt regCmt = new RegCmt();
                regCmt.execute(feedback);
                }
            }
        });
        dig.setCancelable(false);

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

                        // 포인트 +3 지급
                        updatePoint(3, "plus", DetailActivity.this);

                        // 댓글 새로고침
                        LoadCmt loadCmt = new LoadCmt();
                        loadCmt.execute(postId.toString());

                        Toast.makeText(DetailActivity.this, "피드백이 등록되었습니다.", Toast.LENGTH_SHORT).show();

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