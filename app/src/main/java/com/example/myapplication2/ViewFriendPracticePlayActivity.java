package com.example.myapplication2;

import static com.example.myapplication2.MainActivity.updatePoint;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.MediaController;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.myapplication2.api.RetrofitAPI;
import com.example.myapplication2.api.RetrofitClient;
import com.example.myapplication2.api.dto.FeedbacksData;

import java.util.ArrayList;
import java.util.TreeMap;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ViewFriendPracticePlayActivity extends AppCompatActivity {
    final private String TAG = getClass().getSimpleName();

    Toolbar toolbar;
    ActionBar actionBar;

    Intent it;
    Long practice_id, practice_user_id;
    String practice_title, practice_state, practice_scope, practice_sort, friend_name, parent_activity;

    TextView tv_practiceTitle, tv_scope, tv_sort;
    VideoView videoView;
    Button btn_showAnalysis, reg_button;

    LinearLayout comment_layout;
    ArrayList<FeedbacksData> feedbackOfUsersDataList = new ArrayList<>();
    ArrayList<FeedbacksData> feedbackOfFriendsDataList = new ArrayList<>();
    TreeMap<Long, FeedbacksData> feedbackDataMap = new TreeMap<Long, FeedbacksData>();

    static RetrofitAPI retrofitAPI;

    static Long userId = MainActivity.userId;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_viewpractice_play_friend);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        //툴바
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        actionBar = getSupportActionBar();
        actionBar.setDisplayShowCustomEnabled(true);
        actionBar.setDisplayShowTitleEnabled(false);//기본 제목을 없애줍니다.
//        actionBar.setDisplayHomeAsUpEnabled(true);

        tv_practiceTitle = (TextView) findViewById(R.id.tv_practice_title);
        tv_scope = (TextView) findViewById(R.id.tv_scope);
        tv_sort = (TextView) findViewById(R.id.tv_sort);
        videoView = (VideoView) findViewById(R.id.videoView);
        btn_showAnalysis = (Button) findViewById(R.id.btn_show_analysis);
        comment_layout = findViewById(R.id.comment_layout);
        reg_button = (Button) findViewById(R.id.reg_button);

        // Practice Title
        it = getIntent();
        practice_id = it.getLongExtra("practice_id", 0);
        practice_title = it.getStringExtra("practice_title");
        practice_state = it.getStringExtra("practice_state");
        practice_scope = it.getStringExtra("practice_scope");
        practice_sort = it.getStringExtra("practice_sort");
        friend_name = it.getStringExtra("friend_name");
        setTitle("친구 " + friend_name + "의 연습 정보 보기");
        practice_user_id = it.getLongExtra("practice_user_id", 0);
//        parent_activity = it.getStringExtra("PRENT ACTIVITY");

        TextView tv_toolber = (TextView) toolbar.findViewById(R.id.tv_toolbar);
        tv_toolber.setText("친구 " + friend_name + "의 연습 정보 보기");

        reg_button.setVisibility(View.VISIBLE);

        tv_practiceTitle.setText(practice_title);
        tv_scope.setText(practice_scope);
        tv_sort.setText(practice_sort);

        MediaController mc = new MediaController(ViewFriendPracticePlayActivity.this); // 비디오 컨트롤 가능하게(일시정지, 재시작 등)
        videoView.setMediaController(mc);
        videoView.setVideoURI(Uri.parse("https://sookpeech-wavfile.s3.ap-northeast-2.amazonaws.com/video_"+practice_user_id+"_"+practice_id+".mp4"));    // 선택한 비디오 경로 비디오뷰에 셋
        videoView.requestFocus();
        videoView.start();  // 비디오뷰 시작

        btn_showAnalysis.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (practice_state.equals("COMPLETE")) {
                    Intent intent = new Intent(ViewFriendPracticePlayActivity.this, ViewAnalysisActivity.class);
                    intent.putExtra("practice_id", practice_id);
                    intent.putExtra("practice_title", practice_title);
                    intent.putExtra("practice_user_id", practice_user_id);
                    startActivity(intent);
                } else {
                    Toast.makeText(ViewFriendPracticePlayActivity.this, "분석이 완료되지 않았습니다.", Toast.LENGTH_SHORT).show();
                }
            }
        });

        getFeedbackOfUsers();  //피드백 불러오기
        getFeedbackOfFriends();

    }  //end of onCreate()

    private void getFeedbackOfUsers() {
        System.out.println("서버에서 해당 게시물의 user 댓글 가져오기 시작");

        RetrofitClient retrofitClient = RetrofitClient.getInstance();

        if (retrofitClient!=null){
            retrofitAPI = RetrofitClient.getRetrofitAPI();
            retrofitAPI.getFeedbackOfUsers(practice_id).enqueue(new Callback<ArrayList<FeedbacksData>>() {
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
            retrofitAPI.getFeedbackOfFriends(practice_id).enqueue(new Callback<ArrayList<FeedbacksData>>() {
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
        LayoutInflater layoutInflater = LayoutInflater.from(ViewFriendPracticePlayActivity.this);
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


    /* 피드백 등록하기 버튼 클릭 */
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
                    feedback.setInitiator("FRIEND");
                    feedback.setUserId(userId);
                    feedback.setPracticeId(practice_id);

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

            String response = "";

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
                        updatePoint(3, "plus", ViewFriendPracticePlayActivity.this);

                        getFeedbackOfUsers();  //피드백 불러오기
                        getFeedbackOfFriends();

                        Toast.makeText(ViewFriendPracticePlayActivity.this, "피드백이 등록되었습니다.", Toast.LENGTH_SHORT).show();

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
