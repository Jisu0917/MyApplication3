package com.example.myapplication2;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.myapplication2.api.RetrofitAPI;
import com.example.myapplication2.api.RetrofitClient;
import com.example.myapplication2.api.dto.AnalysisContentData;
import com.example.myapplication2.api.dto.AnalysisData;
import com.example.myapplication2.api.dto.PracticesData;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Date;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ViewAnalysisActivity extends AppCompatActivity {
    Toolbar toolbar;
    ActionBar actionBar;

    String CAPTURE_PATH = "/sookpeech_analysis_result";
    public static String CAPTURE_DIR = "sookpeech_analysis_result";
    Long practice_id, practice_user_id;
    String practice_title = "";

    PracticesData practicesData;
    AnalysisData analysisData;
    AnalysisContentData analysisContentData;
    String gender;

    LinearLayout linearLayout;
    ScrollView scrollView;

    TextView tv_analysis_total, tv_analysis_gesture, tv_analysis_gaze, tv_analysis_face, tv_analysis_pose, tv_analysis_speed,
            tv_analysis_volume, tv_analysis_pitch, tv_analysis_conclusion;
    ImageView good_or_warning_gesture, good_or_warning_gaze, good_or_warning_face, good_or_warning_pose, good_or_warning_speed,
            good_or_warning_volume, good_or_warning_pitch, good_or_warning_conclusion;
    RatingBar ratingBar;
    static int warnings = 0;

    ImageView iv_chart_gesture, iv_chart_gaze, iv_chart_pose, iv_chart_speed, iv_chart_volume_pitch,
            iv_chart_conclusion;

    static Long userId = MainActivity.userId;

    static RetrofitAPI retrofitAPI;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_viewanalysis);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        //툴바
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        actionBar = getSupportActionBar();
        actionBar.setDisplayShowCustomEnabled(true);
        actionBar.setDisplayShowTitleEnabled(false);//기본 제목을 없애줍니다.
//        actionBar.setDisplayHomeAsUpEnabled(true);

        final Intent it = getIntent();
        practice_id = it.getLongExtra("practice_id", 0);
        practice_title = it.getStringExtra("practice_title");
        practice_user_id = it.getLongExtra("practice_user_id", 0);
        
        setTitle(practice_title + "의 분석 결과");

        TextView tv_toolber = (TextView) toolbar.findViewById(R.id.tv_toolbar);
        tv_toolber.setText(practice_title + "의 분석 결과");

        linearLayout = (LinearLayout) findViewById(R.id.linearlayout_analysis);
        scrollView = (ScrollView) findViewById(R.id.scrollView);

        tv_analysis_total = (TextView) findViewById(R.id.tv_analysis_total);
        tv_analysis_gesture = (TextView) findViewById(R.id.tv_analysis_gesture);
        tv_analysis_gaze = (TextView) findViewById(R.id.tv_analysis_gaze);
        tv_analysis_face = (TextView) findViewById(R.id.tv_analysis_face);
        tv_analysis_pose = (TextView) findViewById(R.id.tv_analysis_pose);
        tv_analysis_speed = (TextView) findViewById(R.id.tv_analysis_speed);
        tv_analysis_volume = (TextView) findViewById(R.id.tv_analysis_volume);
        tv_analysis_pitch = (TextView) findViewById(R.id.tv_analysis_pitch);
        tv_analysis_conclusion = (TextView) findViewById(R.id.tv_analysis_conclusion);

        good_or_warning_gesture = (ImageView) findViewById(R.id.good_or_warning_gesture);
        good_or_warning_gaze = (ImageView) findViewById(R.id.good_or_warning_gaze);
        good_or_warning_face = (ImageView) findViewById(R.id.good_or_warning_face);
        good_or_warning_pose = (ImageView) findViewById(R.id.good_or_warning_pose);
        good_or_warning_speed = (ImageView) findViewById(R.id.good_or_warning_speed);
        good_or_warning_volume = (ImageView) findViewById(R.id.good_or_warning_volume);
        good_or_warning_pitch = (ImageView) findViewById(R.id.good_or_warning_pitch);
        good_or_warning_conclusion = (ImageView) findViewById(R.id.good_or_warning_conclusion);

        iv_chart_gesture = (ImageView) findViewById(R.id.iv_chart_gesture);
        //iv_chart_gaze = (ImageView) findViewById(R.id.iv_chart_gaze);
        iv_chart_pose = (ImageView) findViewById(R.id.iv_chart_pose);
        iv_chart_speed = (ImageView) findViewById(R.id.iv_chart_speed);
        iv_chart_volume_pitch = (ImageView) findViewById(R.id.iv_chart_pitch);
        iv_chart_conclusion = (ImageView) findViewById(R.id.iv_chart_conclusion);

        ratingBar = (RatingBar) findViewById(R.id.ratingbarStyle);

        getPracticeInfo(practice_id);  // 분석 결과 서버로부터 받아오기
    }

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

                        setAnalysisResult();  // 분석 결과 텍스트, 이미지 아이콘 셋팅하기
                    }
                    else {
                        System.out.println("@@@@ response is not successful...");
                        System.out.println("@@@@ response code : " + response.code());

                        Toast.makeText(ViewAnalysisActivity.this, "분석 결과를 가져오는 데에 실패했습니다.", Toast.LENGTH_SHORT).show();
                        finish();
                        overridePendingTransition(R.anim.slide_down,R.anim.slide_up);
                    }
                }

                @Override
                public void onFailure(Call<PracticesData> call, Throwable t) {
                    Log.d("GET", "POST Failed");
                    Log.d("GET", t.getMessage());

                    Toast.makeText(ViewAnalysisActivity.this, "분석 결과를 가져오는 데에 실패했습니다.", Toast.LENGTH_SHORT).show();
                    finish();
                    overridePendingTransition(R.anim.slide_down,R.anim.slide_up);
                }
            });
        }
    }

    private void setAnalysisResult() {
        warnings = 0;

        gender = practicesData.getGender();
        analysisData = practicesData.getAnalysis();
        analysisContentData = analysisData.getAnalysisContent();

        /* 분석 결과 - 텍스트 셋팅하기 */
        tv_analysis_gesture.setText(analysisContentData.getFirstDuration()+"초 동안 얼굴 위로 손 제스처가 감지되었습니다.\n" +
                analysisContentData.getSecondDuration()+"초 동안 팔짱 끼기 제스처가 감지되었습니다.\n" +
                analysisContentData.getThirdDuration()+"초 동안 허리에 손 제스처가 감지되었습니다.\n" +
                "감지된 제스처는 일반적으로 권장하지 않는 제스처이므로 다른 제스처를 추천합니다.");
        tv_analysis_gaze.setText("회원님이 선택하신 시선 움직임 민감도는 "+practicesData.getEyesSensitivity()+"이며, " +
                "영상의 총 길이는 "+analysisContentData.getTotalDuration()+"초이고,\n회원님의 분석 결과 "+
                analysisContentData.getScriptDuration()+"초 동안 대본을 보는 시선 분산이 감지되었습니다.\n"+
                "또한 "+analysisContentData.getAroundDuration()+"초 동안 주변을 보는 시선 분산이 감지되었습니다.\n"+
                "발표시 시선은 청중을 향하는 것이 좋습니다.");  // 온라인, 오프라인 ?  //임시, 확인용
        tv_analysis_face.setText(analysisContentData.getFaceMoveDuration() + "초 동안 얼굴 움직임 감지되었습니다.\n" +
                "온라인 발표시에는 얼굴 움직임을 최소화하고 화면을 응시하는 것이 좋습니다.");
        tv_analysis_pose.setText("회원님이 선택하신 자세 민감도는 "+practicesData.getMoveSensitivity()+"이며, 총 영상 길이 "+
                analysisContentData.getTotalDuration()+"초 중 "+analysisContentData.getInclinedDuration()+"초 동안 자세가 기울어졌습니다."+
                "\n두 어깨의 수평을 맞추는 자세를 권장합니다.");
        tv_analysis_speed.setText("평균적인 말하기 속도는 1분당 96단어 이상 124 단어 미만이며, 현재 회원님의 말하기 속도는 1분당 "+
                analysisContentData.getSpeed()+"단어입니다.\n"
                + evaluateSpeed(analysisContentData.getSpeed()));  // speed 값에 따라 다른 평가 제시
        tv_analysis_volume.setText("적절한 목소리의 크기 변화율은 "+ standardShimmer() +"이며, 현재 회원님의 목소리 크기 변화율은 "+
                analysisContentData.getShimmer()+"%입니다.\n"
                + evaluateShimmer(analysisContentData.getShimmer()) +
                "\n중요한 부분에서 힘 있는 목소리로 연습하시기 바랍니다.");  // shimmer 값에 따라 다른 평가 제시
        tv_analysis_pitch.setText("적절한 목소리의 높낮이 변화율은 "+ standardJitter() +"이며, 현재 회원님의 목소리 높낮이 변화율은 "+
                analysisContentData.getJitter()+"%입니다.\n"
                + evaluateJitter(analysisContentData.getJitter()) +
                "\n내용에 따라 목소리 높낮이를 다양하게 사용하며 내용을 효과적으로 전달하시기 바랍니다.");  // jitter 값에 따라 다른 평가 제시
        tv_analysis_conclusion.setText("전체 문장 중 "+analysisContentData.getClosingRemarks()+"%의 문장에서 맺음말이 인식되었습니다.\n"+
                "맺음말이 인식되지 않은 경우, 말 끝을 흐리는 등 발음이 부정확하였거나 문장이 끝난 이후 충분한 공백을 두지 않았을 수 있습니다.\n" +
                "맺음말이 부정확한 경우 청중들의 이해도와 발표의 전달력을 떨어트릴 수 있으므로, 유의하여 연습하시기 바랍니다.");

        /* 분석 결과 - 이미지 아이콘 셋팅하기 */
        if (isGood("gesture")) {
            good_or_warning_gesture.setImageResource(R.drawable.ic_good);
        } else {
            good_or_warning_gesture.setImageResource(R.drawable.ic_warning);
            warnings++;
        }
        if (isGood("gaze")) {
            good_or_warning_gaze.setImageResource(R.drawable.ic_good);
        } else {
            good_or_warning_gaze.setImageResource(R.drawable.ic_warning);
            warnings++;
        }
        if (isGood("face")) {
            good_or_warning_face.setImageResource(R.drawable.ic_good);
        } else {
            good_or_warning_face.setImageResource(R.drawable.ic_warning);
            warnings++;
        }
        if (isGood("pose")) {
            good_or_warning_pose.setImageResource(R.drawable.ic_good);
        } else {
            good_or_warning_pose.setImageResource(R.drawable.ic_warning);
            warnings++;
        }
        if (isGood("speed")) {
            good_or_warning_speed.setImageResource(R.drawable.ic_good);
        } else {
            good_or_warning_speed.setImageResource(R.drawable.ic_warning);
            warnings++;
        }
        if (isGood("volume")) {
            good_or_warning_volume.setImageResource(R.drawable.ic_good);
        } else {
            good_or_warning_volume.setImageResource(R.drawable.ic_warning);
            warnings++;
        }
        if (isGood("pitch")) {
            good_or_warning_pitch.setImageResource(R.drawable.ic_good);
        } else {
            good_or_warning_pitch.setImageResource(R.drawable.ic_warning);
            warnings++;
        }
        if (isGood("conclusion")) {
            good_or_warning_conclusion.setImageResource(R.drawable.ic_good);
        } else {
            good_or_warning_conclusion.setImageResource(R.drawable.ic_warning);
            warnings++;
        }

        // 종합 평가 별점
        /* 기준 : 8개 항목 중 warning
        * 0개 : 5.0
        * 1개 : 4.5
        * 2개 : 4.0
        * 3개 : 3.5
        * 4개 : 3.0
        * 5개 : 2.5
        * 6개 : 2.0
        * 7개 : 1.5
        * 8개 : 1.0
        * 영상 길이가 5초 미만 : 0.5
        * */
        if (analysisContentData.getTotalDuration() < 5) {
            ratingBar.setRating(0.5f);
            tv_analysis_total.setText("영상의 총 길이는 "+analysisContentData.getTotalDuration()+"초입니다.\n"+
                    "영상이 너무 짧아 평가가 제대로 이루어지지 않을 수 있습니다.");
        } else {
            switch (warnings) {
                case 0:
                    ratingBar.setRating(5.0f);
                    tv_analysis_total.setText("영상의 총 길이는 "+analysisContentData.getTotalDuration()+"초입니다.\n"+
                            "분석 결과, 전반적으로 훌륭한 수준입니다. 8개의 항목 모두 안정적인 결과를 보이고 있습니다. 자신감 있게 실전에 임하시기를 바랍니다.");
                    break;
                case 1:
                    ratingBar.setRating(4.5f);
                    tv_analysis_total.setText("영상의 총 길이는 "+analysisContentData.getTotalDuration()+"초입니다.\n"+
                        "분석 결과, 전반적으로 양호한 수준입니다. 7개의 항목에서 안정적인 결과를 보이고 있습니다. 자신감 있게 실전에 임하시기를 바랍니다.");
                    break;
                case 2:
                    ratingBar.setRating(4.0f);
                    tv_analysis_total.setText("영상의 총 길이는 "+analysisContentData.getTotalDuration()+"초입니다.\n"+
                        "분석 결과, 전반적으로 양호한 수준입니다. 6개의 항목에서 안정적인 결과를 보이고 있습니다. 2개의 항목을 보완하여 실전에 임하시기를 바랍니다.");
                    break;
                case 3:
                    ratingBar.setRating(3.5f);
                    tv_analysis_total.setText("영상의 총 길이는 "+analysisContentData.getTotalDuration()+"초입니다.\n"+
                            "분석 결과, 전반적으로 조금 미흡한 수준입니다. 5개의 항목에서 안정적인 결과를 보이고 있지만 3개의 항목은 보완할 필요가 있습니다. 조금 더 연습한 후 실전에 임하시기를 바랍니다.");
                    break;
                case 4:
                    ratingBar.setRating(3.0f);
                    tv_analysis_total.setText("영상의 총 길이는 "+analysisContentData.getTotalDuration()+"초입니다.\n"+
                            "분석 결과, 전반적으로 미흡한 수준입니다. 4개의 항목에서 안정적인 결과를 보이고 있지만 4개의 항목은 보완할 필요가 있습니다. 반복적으로 연습한 후 실전에 임하시기를 바랍니다.");
                    break;
                case 5:
                    ratingBar.setRating(2.5f);
                    tv_analysis_total.setText("영상의 총 길이는 "+analysisContentData.getTotalDuration()+"초입니다.\n"+
                            "분석 결과, 전반적으로 미흡한 수준입니다. 3개의 항목에서 안정적인 결과를 보이고 있지만 5개의 항목은 보완할 필요가 있습니다. 반복적으로 연습한 후 실전에 임하시기를 바랍니다.");
                    break;
                case 6:
                    ratingBar.setRating(2.0f);
                    tv_analysis_total.setText("영상의 총 길이는 "+analysisContentData.getTotalDuration()+"초입니다.\n"+
                            "분석 결과, 전반적으로 매우 미흡한 수준입니다. 2개의 항목에서 안정적인 결과를 보이고 있지만 6개의 항목은 보완할 필요가 있습니다. 반복적으로 연습한 후 실전에 임하시기를 바랍니다.");
                    break;
                case 7:
                    ratingBar.setRating(1.5f);
                    tv_analysis_total.setText("영상의 총 길이는 "+analysisContentData.getTotalDuration()+"초입니다.\n"+
                            "분석 결과, 전반적으로 매우 미흡한 수준입니다. 1개의 항목에서 안정적인 결과를 보이고 있지만 7개의 항목은 보완할 필요가 있습니다. 반복적으로 연습한 후 실전에 임하시기를 바랍니다.");
                    break;
                case 8:
                    ratingBar.setRating(1.0f);
                    tv_analysis_total.setText("영상의 총 길이는 "+analysisContentData.getTotalDuration()+"초입니다.\n"+
                            "분석 결과, 전반적으로 매우 미흡한 수준입니다. 8개의 항목 모두 불안정한 결과를 보이고 있어 보완할 필요가 있습니다. 반복적으로 연습한 후 실전에 임하시기를 바랍니다.");
                    break;
            }
        }

        /* 차트 이미지 표시하기 */
        final Bitmap[] bitmap = new Bitmap[5];
        Thread uThread = new Thread() {
            @Override
            public void run(){
                try{
                    /* gesture */
                    // 이미지 URL 경로
                    URL url = new URL("https://sookpeech-wavfile.s3.ap-northeast-2.amazonaws.com/"+practice_user_id+"/"+practice_id+"/movement.png");

                    // web에서 이미지를 가져와 ImageView에 저장할 Bitmap을 만든다.
                    HttpURLConnection conn = (HttpURLConnection)url.openConnection();
                    conn.setDoInput(true); // 서버로부터 응답 수신
                    conn.connect(); //연결된 곳에 접속할 때 (connect() 호출해야 실제 통신 가능함)

                    InputStream is = conn.getInputStream(); //inputStream 값 가져오기
                    bitmap[0] = BitmapFactory.decodeStream(is); // Bitmap으로 변환

                    /* pose */
                    // 이미지 URL 경로
                    url = new URL("https://sookpeech-wavfile.s3.ap-northeast-2.amazonaws.com/"+practice_user_id+"/"+practice_id+"/movement_detail.png");

                    // web에서 이미지를 가져와 ImageView에 저장할 Bitmap을 만든다.
                    conn = (HttpURLConnection)url.openConnection();
                    conn.setDoInput(true); // 서버로부터 응답 수신
                    conn.connect(); //연결된 곳에 접속할 때 (connect() 호출해야 실제 통신 가능함)

                    is = conn.getInputStream(); //inputStream 값 가져오기
                    bitmap[1] = BitmapFactory.decodeStream(is); // Bitmap으로 변환

                    /* speed */
                    // 이미지 URL 경로
                    url = new URL("https://sookpeech-wavfile.s3.ap-northeast-2.amazonaws.com/"+practice_user_id+"/"+practice_id+"/speed.png");

                    // web에서 이미지를 가져와 ImageView에 저장할 Bitmap을 만든다.
                    conn = (HttpURLConnection)url.openConnection();
                    conn.setDoInput(true); // 서버로부터 응답 수신
                    conn.connect(); //연결된 곳에 접속할 때 (connect() 호출해야 실제 통신 가능함)

                    is = conn.getInputStream(); //inputStream 값 가져오기
                    bitmap[2] = BitmapFactory.decodeStream(is); // Bitmap으로 변환

                    /* volume_pitch */
                    // 이미지 URL 경로
                    url = new URL("https://sookpeech-wavfile.s3.ap-northeast-2.amazonaws.com/"+practice_user_id+"/"+practice_id+"/shimmer_jitter.png");

                    // web에서 이미지를 가져와 ImageView에 저장할 Bitmap을 만든다.
                    conn = (HttpURLConnection)url.openConnection();
                    conn.setDoInput(true); // 서버로부터 응답 수신
                    conn.connect(); //연결된 곳에 접속할 때 (connect() 호출해야 실제 통신 가능함)

                    is = conn.getInputStream(); //inputStream 값 가져오기
                    bitmap[3] = BitmapFactory.decodeStream(is); // Bitmap으로 변환

                    /* conclusion */
                    // 이미지 URL 경로
                    url = new URL("https://sookpeech-wavfile.s3.ap-northeast-2.amazonaws.com/"+practice_user_id+"/"+practice_id+"/closing_remarks.png");

                    // web에서 이미지를 가져와 ImageView에 저장할 Bitmap을 만든다.
                    conn = (HttpURLConnection)url.openConnection();
                    conn.setDoInput(true); // 서버로부터 응답 수신
                    conn.connect(); //연결된 곳에 접속할 때 (connect() 호출해야 실제 통신 가능함)

                    is = conn.getInputStream(); //inputStream 값 가져오기
                    bitmap[4] = BitmapFactory.decodeStream(is); // Bitmap으로 변환

                }catch (MalformedURLException e){
                    e.printStackTrace();
                }catch (IOException e) {
                    e.printStackTrace();
                }
            }
        };

        uThread.start(); // 작업 Thread 실행

        try{
            //메인 Thread는 별도의 작업 Thread가 작업을 완료할 때까지 대기해야 한다.
            //join() 호출하여 별도의 작업 Thread가 종료될 때까지 메인 Thread가 기다리도록 한다.
            //join() 메서드는 InterruptedException을 발생시킨다.
            uThread.join();

            //작업 Thread에서 이미지를 불러오는 작업을 완료한 뒤
            //UI 작업을 할 수 있는 메인 Thread에서 ImageView에 이미지 지정
//            iv_chart_gesture.setVisibility(View.VISIBLE);
            iv_chart_pose.setVisibility(View.VISIBLE);
            iv_chart_speed.setVisibility(View.VISIBLE);
            iv_chart_volume_pitch.setVisibility(View.VISIBLE);
            iv_chart_conclusion.setVisibility(View.VISIBLE);

            iv_chart_gesture.setImageBitmap(bitmap[0]);
            iv_chart_pose.setImageBitmap(bitmap[1]);
            iv_chart_speed.setImageBitmap(bitmap[2]);
            iv_chart_volume_pitch.setImageBitmap(bitmap[3]);
            iv_chart_conclusion.setImageBitmap(bitmap[4]);
        } catch (InterruptedException e){
            e.printStackTrace();
        }
    }

    // 평가 함수 (이미지 아이콘 good) - true : good, false : warning
    // 일단 다 0초 이상 감지되면 warning으로 설정해두었음!! 목소리 변화율은 20%로 설정해두었음!!! 맺음말은 80%로!
    private boolean isGood(String standard) {
        switch (standard) {
            case "gesture":
                return analysisContentData.getFirstDuration() + analysisContentData.getSecondDuration() + analysisContentData.getThirdDuration() < 3.0;
            case "gaze":
                return analysisContentData.getScriptDuration() + analysisContentData.getAroundDuration() < 2.0;
            case "face":
                return analysisContentData.getFaceMoveDuration() < 1.0;
            case "pose":
                return analysisContentData.getInclinedDuration() < 1.0;
            case "speed":
                return analysisContentData.getSpeed() >= 96 && analysisContentData.getSpeed() <= 124;
            case "volume":
                return isGoodShimmer(analysisContentData.getShimmer());
            case "pitch":
                return isGoodJitter(analysisContentData.getJitter());
            case "conclusion":
                return analysisContentData.getClosingRemarks() >= 70;
        }

        return false;
    }

    private String standardShimmer() {
        if (gender.equals("MEN"))
            return "남성은 10.132% 이상 13.526% 미만";
        else if (gender.equals("WOMEN"))
            return "여성은 7.393% 이상 12.221% 미만";
        return "";
    }

    private String standardJitter() {
        if (gender.equals("MEN"))
            return "남성은 2.159% 이상 3.023% 미만";
        else if (gender.equals("WOMEN"))
            return "여성은 1.599% 이상 2.310% 미만";
        return "";
    }

    private String evaluateSpeed(float speed) {
        String res = "";

        if (speed < 96) {  //느림
            res = "말하기 속도가 느린 경우 오히려 청중들의 집중도를 떨어트릴 우려가 있으므로, 조금 더 빨리 말할 수 있도록 연습하시기 바랍니다.";
        } else if (speed < 124) {  //적정
            res = "현재 회원님의 말하기 속도는 청중들의 이해도를 높일 수 있는 적정한 말하기 속도 범위에 위치하고 있습니다.";
        } else {  //빠름
            res = "말하기 속도가 빠른 경우 청중들의 이해도와 집중도를 떨어트릴 우려가 있으므로, 조금 호흡을 길게 가질 수 있도록 연습하시기 바랍니다.";
        }

        return res;
    }

    private String evaluateShimmer(float shimmer) {
        String res = "";

        if (gender.equals("MEN")) {
            if (shimmer < 10.132) {  //단조로움
                res = "목소리의 크기 변화가 다소 단조로운 것으로 측정되었으며, 단조로운 목소리 크기 변화는 청중들을 쉽게 지루하게 만들 수 있습니다. 중요한 부분에서 힘 있는 목소리로 연습하시기 바랍니다.";
            } else if (shimmer < 13.526) {  //적정
                res = "회원님의 음성은 적정한 목소리의 크기 변화를 보이고 있으며, 강조할 내용에서의 충분한 목소리 크기 변화는 청중들의 집중도와 흥미를 높일 수 있습니다.";
            } else {  //너무 산만(?)함
                res = "회원님의 음성은 산만한 목소리의 크기 변화를 보이고 있으며, 강조할 내용에서만 목소리 크기 변화를 주셔야 청중들의 집중도와 흥미를 높일 수 있습니다.";
            }
        } else if (gender.equals("WOMEN")) {
            if (shimmer < 7.393) {  //단조로움
                res = "목소리의 크기 변화가 다소 단조로운 것으로 측정되었으며, 단조로운 목소리 크기 변화는 청중들을 쉽게 지루하게 만들 수 있습니다. 중요한 부분에서 힘 있는 목소리로 연습하시기 바랍니다.";
            } else if (shimmer < 12.221) {  //적정
                res = "회원님의 음성은 적정한 목소리의 크기 변화를 보이고 있으며, 강조할 내용에서의 충분한 목소리 크기 변화는 청중들의 집중도와 흥미를 높일 수 있습니다.";
            } else {  //너무 산만(?)함
                res = "회원님의 음성은 산만한 목소리의 크기 변화를 보이고 있으며, 강조할 내용에서만 목소리 크기 변화를 주셔야 청중들의 집중도와 흥미를 높일 수 있습니다.";
            }
        }

        return res;
    }

    private boolean isGoodShimmer(float shimmer) {
        if (gender.equals("MEN")) {
            return shimmer >= 10.132 && shimmer < 13.526;
        } else if (gender.equals("WOMEN")) {
            return shimmer >= 7.393 && shimmer < 12.221;
        }
        else return false;
    }

    private String evaluateJitter(float jitter) {
        String res = "";

        if (gender.equals("MEN")) {
            if (jitter < 2.159) {  //단조로움
                res = "목소리의 높낮이 변화가 다소 단조로운 편으로 측정되었습니다. 목소리 높낮이가 단조로울 경우 좋은 내용이더라도 지루하게 느껴질 수 있습니다. 내용에 따라 목소리 높낮이를 다양하게 사용하며 내용을 효과적으로 전달하시기 바랍니다.";
            } else if (jitter < 3.023) {  //적정
                res = "회원님의 음성은 적정한 목소리의 높낮이 변화를 보이고 있으며, 강조할 내용에서의 충분한 목소리 높낮이 강조는 청중들의 몰입감을 높일 수 있습니다.";
            } else {  //너무 산만(?)함
                res = "회원님의 음성은 다소 산만한 목소리의 높낮이 변화를 보이고 있으며, 강조할 내용에서만 목소리 높낮이 강조를 사용하셔야 청중들의 몰입감을 높일 수 있습니다.";
            }
        } else if (gender.equals("WOMEN")) {
            if (jitter < 1.599) {  //단조로움
                res = "목소리의 높낮이 변화가 다소 단조로운 편으로 측정되었습니다. 목소리 높낮이가 단조로울 경우 좋은 내용이더라도 지루하게 느껴질 수 있습니다. 내용에 따라 목소리 높낮이를 다양하게 사용하며 내용을 효과적으로 전달하시기 바랍니다.";
            } else if (jitter < 2.310) {  //적정
                res = "회원님의 음성은 적정한 목소리의 높낮이 변화를 보이고 있으며, 강조할 내용에서의 충분한 목소리 높낮이 강조는 청중들의 몰입감을 높일 수 있습니다.";
            } else {  //너무 산만(?)함
                res = "회원님의 음성은 다소 산만한 목소리의 높낮이 변화를 보이고 있으며, 강조할 내용에서만 목소리 높낮이 강조를 사용하셔야 청중들의 몰입감을 높일 수 있습니다.";
            }
        }
        return res;
    }

    private boolean isGoodJitter(float jitter) {
        if (gender.equals("MEN")) {
            return jitter >= 2.159 && jitter < 3.023;
        } else if (gender.equals("WOMEN")) {
            return jitter >= 1.599 && jitter < 2.310;
        }
        else return false;
    }

    /* 이미지 스크롤 저장 */
    public void download_img(View v) {
//        ProgressDialog dialog = new ProgressDialog(getActivity());
//        dialog.setMessage("Saving...");
//        dialog.show();

        Bitmap bitmap = getBitmapFromView(scrollView,scrollView.getChildAt(0).getHeight(),scrollView.getChildAt(0).getWidth());
        try {
//            File defaultFile = new File(Environment.getExternalStorageDirectory().getAbsolutePath()+CAPTURE_PATH);
//            if (!defaultFile.exists())
//                defaultFile.mkdirs();


            //////
            // 버전 30 이상에서 작동하도록 수정
            File directory;
            if (Build.VERSION.SDK_INT >= 30){
                System.out.println("android version >= 30");

                File destination = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
                if (!destination.exists()) { // 원하는 경로에 폴더가 있는지 확인
                    destination.mkdirs();
                    Log.d("ViewAnalysisActivity", "destination Created");
                }

                directory = new File(destination + File.separator+CAPTURE_DIR);
                if (!directory.exists()) { // 원하는 경로에 폴더가 있는지 확인
                    directory.mkdirs();
                    Log.d("ViewAnalysisActivity", "Directory Created");
                }
            } else{
                directory = new File(Environment.getExternalStorageDirectory() + File.separator+CAPTURE_DIR);
                if (!directory.exists()) { // 원하는 경로에 폴더가 있는지 확인
                    directory.mkdirs();
                    Log.d("ViewAnalysisActivity", "Directory Created");
                }
            }

            //////

            String filename = "Sookpeech_"+ practice_id + "_" + practice_title + "_"+ getNowTime() +".jpg";
            File file = new File(directory,filename);
            if (file.exists()) {
                file.delete();
                file = new File(directory,filename);
            }

            System.out.println("file: " + file);  //임시, 확인용

           FileOutputStream output = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, output);
            output.flush();
            output.close();

//            dialog.dismiss();

            Toast.makeText(ViewAnalysisActivity.this, "이미지를 다운로드 했습니다.", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            e.printStackTrace();
//            dialog.dismiss();
            Toast.makeText(ViewAnalysisActivity.this, "이미지를 다운로드 하는 데 실패했습니다.", Toast.LENGTH_SHORT).show();
        }
    }

    //create bitmap from the view
    private Bitmap getBitmapFromView(View view,int height,int width) {
        Bitmap bitmap = Bitmap.createBitmap(width+190, height,Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        Drawable bgDrawable =view.getBackground();
        if (bgDrawable!=null)
            bgDrawable.draw(canvas);
        else
            canvas.drawColor(Color.WHITE);
        view.draw(canvas);
        return bitmap;
    }

    private String getNowTime() {
        String nowTime = "";
        Date date = new Date();
        nowTime += (date.getYear() + 1900) + "" + (date.getMonth() + 1) + "" + date.getDate() + "_" + date.getHours() + "" + date.getMinutes() + "" + date.getSeconds();

        return nowTime;
    }

}
