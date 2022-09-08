package com.example.myapplication2;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.media.ThumbnailUtils;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.myapplication2.api.RetrofitAPI;
import com.example.myapplication2.api.RetrofitClient;
import com.example.myapplication2.api.dto.FeedbacksData;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ViewPracticeActivity extends AppCompatActivity {
    Intent it;
    Long practice_id;
    String practice_title, practice_state;

    TextView tv_practiceTitle;
    ImageView videoThumbnail;
    ImageButton btn_play;
    Button btn_showAnalysis;

    DBHelper dbHelper;
    SQLiteDatabase db = null;
    Cursor cursor;

    String fileUrl = "";

    LinearLayout comment_layout;
    ArrayList<FeedbacksData> feedbacksDataList = new ArrayList<>();

    static RetrofitAPI retrofitAPI;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_viewpractice);

        tv_practiceTitle = (TextView) findViewById(R.id.tv_practice_title);
        videoThumbnail = (ImageView) findViewById(R.id.imageview_thumbnail);
        btn_play = (ImageButton) findViewById(R.id.btn_play);
        btn_showAnalysis = (Button) findViewById(R.id.btn_show_analysis);
        comment_layout = findViewById(R.id.comment_layout);

        // Practice Title
        it = getIntent();
        practice_id = it.getLongExtra("practice_id", 0);
        practice_title = it.getStringExtra("practice_title");
        practice_state = it.getStringExtra("practice_state");

        tv_practiceTitle.setText(practice_title);

        //fileUrl 가져오기
        dbHelper = new DBHelper(this, 4);
        db = dbHelper.getWritableDatabase();    // 읽기/쓰기 모드로 데이터베이스를 오픈
        cursor = db.rawQuery(" SELECT * FROM practiceTable", null);
        startManagingCursor(cursor);    // 엑티비티의 생명주기와 커서의 생명주기를 같게 한다.
//        cursor.moveToPosition(practice_index);
        cursor.moveToFirst();
        while (cursor.moveToNext()) {
            System.out.println("practiceTable - practice_id : " + cursor.getInt(0) + ", file_url: " + cursor.getString(1));
            if (cursor.getInt(0) == practice_id.intValue())
                fileUrl = cursor.getString(1);
        }
        if (fileUrl.equals(""))
            Toast.makeText(ViewPracticeActivity.this, "fileUrl is empty..", Toast.LENGTH_SHORT).show();  //임시, 확인용


        // 비디오 썸네일 보이기
        try {
            // 썸네일 추출후 리사이즈해서 다시 비트맵 생성
            Bitmap bitmap = ThumbnailUtils.createVideoThumbnail(fileUrl, MediaStore.Video.Thumbnails.MICRO_KIND);
            Bitmap thumbnail = ThumbnailUtils.extractThumbnail(bitmap, 300, 300);
            videoThumbnail.setImageBitmap(thumbnail);

        } catch (Exception e) {
            e.printStackTrace();
        }

        btn_play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Toast.makeText(ShowPracticeActivity.this, "play 버튼 클릭", Toast.LENGTH_SHORT).show(); //임시, 확인용
                Intent intent = new Intent(ViewPracticeActivity.this, PlayPracticeActivity.class);
                intent.putExtra("practice_id", practice_id);
                intent.putExtra("fileUrl", fileUrl);
                startActivity(intent);
            }
        });

        btn_showAnalysis.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (practice_state.equals("COMPLETE")) {
                    Intent intent = new Intent(ViewPracticeActivity.this, ViewAnalysisActivity.class);
                    intent.putExtra("practice_id", practice_id);
                    intent.putExtra("practice_title", practice_title);
                    startActivity(intent);
                } else {
                    Toast.makeText(ViewPracticeActivity.this, "분석이 완료되지 않았습니다.", Toast.LENGTH_SHORT).show();
                }
            }
        });

        getFeedbackOfUsers();  //피드백 불러오기

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
        LayoutInflater layoutInflater = LayoutInflater.from(ViewPracticeActivity.this);
        //LayoutInflater layoutInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);


        if (feedbacksDataList != null) {
            int cmt_count = 1;
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

                ((TextView)customView.findViewById(R.id.cmt_count)).setText("댓글 " + cmt_count);
                ((TextView)customView.findViewById(R.id.cmt_speedscore_tv)).setText(numberToStars(speed_score));
                ((TextView)customView.findViewById(R.id.cmt_speedcmt_tv)).setText(speed_comment);
                ((TextView)customView.findViewById(R.id.cmt_tonescore_tv)).setText(numberToStars(tone_score));
                ((TextView)customView.findViewById(R.id.cmt_tonecmt_tv)).setText(tone_comment);
                ((TextView)customView.findViewById(R.id.cmt_closingscore_tv)).setText(numberToStars(closing_score));
                ((TextView)customView.findViewById(R.id.cmt_closingcmt_tv)).setText(closing_comment);

                // 댓글 레이아웃에 custom_comment 의 디자인에 데이터를 담아서 추가
                comment_layout.addView(customView);

                cmt_count++;
            }

        } else {
            System.out.println("LoadCmt : onPostExecute : feedbacksDataList is null...");
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
}
