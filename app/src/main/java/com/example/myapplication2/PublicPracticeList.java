package com.example.myapplication2;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;

import com.bumptech.glide.Glide;
import com.example.myapplication2.api.RetrofitAPI;
import com.example.myapplication2.api.RetrofitClient;
import com.example.myapplication2.api.dto.PracticesData;
import com.example.myapplication2.api.dto.UserInfoData;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PublicPracticeList extends AppCompatActivity {
    Toolbar toolbar;
    ActionBar actionBar;

    static Long userId = MainActivity.userId;
    static RetrofitAPI retrofitAPI;

    Long friend_id;
    String friend_name;
    PracticesData[] practicesDataList;
    ArrayList<PracticesData> publicPracticesList;

    LinearLayout practicelist_layout;

    Long practice_user_id;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_public_practicelist);

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

        Intent it = getIntent();
        friend_id = it.getLongExtra("friend_id", 0L);
        friend_name = it.getStringExtra("friend_name");
        setTitle("친구 " + friend_name +"의 공개된 연습 목록");

        TextView tv_toolber = (TextView) toolbar.findViewById(R.id.tv_toolbar);
        tv_toolber.setText("친구 " + friend_name +"의 공개된 연습 목록");

        practicelist_layout = (LinearLayout) findViewById(R.id.practicelist_layout);

        getUserInfo();
    }

    private void getUserInfo(){
        RetrofitClient retrofitClient = RetrofitClient.getInstance();

        practicesDataList = null;
        publicPracticesList = new ArrayList<>();

        if (retrofitClient!=null){
            retrofitAPI = RetrofitClient.getRetrofitAPI();
            retrofitAPI.getUserInfo(friend_id).enqueue(new Callback<UserInfoData>() {
                @Override
                public void onResponse(Call<UserInfoData> call, Response<UserInfoData> response) {
                    UserInfoData userInfoData = response.body();
                    if (userInfoData!=null){
                        Log.d("GET_USERINFO", "GET SUCCESS");
                        Log.d("GET_USERINFO", ">>>response.body()=" + response.body());

                        practicesDataList = userInfoData.getPractices();

                        for (PracticesData practicesData : practicesDataList) {
                            if ((practicesData.getScope()).equals("PUBLIC"))
                                publicPracticesList.add(practicesData);
                        }

                        setPracticeListView();
                    }
                }

                @Override
                public void onFailure(Call<UserInfoData> call, Throwable t) {
                    Log.d("GET_USERINFO", "GET FAILED");
                }
            });
        }
    }

    // 서버에서 연습 정보 가져오기
    private void getPracticeInfo(Long practice_id) {
        System.out.println("특정 연습 정보 가져오기 시작");

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

                        PracticesData practicesData = response.body();
                        if (practicesData != null) {
                            practice_user_id = practicesData.getUserId();
                        }
                    }
                    else {
                        System.out.println("@@@@ response is not successful...");
                        System.out.println("@@@@ response code : " + response.code());

                        Toast.makeText(PublicPracticeList.this, "연습 정보를 가져오는 데에 실패했습니다.", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                }

                @Override
                public void onFailure(Call<PracticesData> call, Throwable t) {
                    Log.d("GET", "GET Failed");
                    Log.d("GET", t.getMessage());

                    Toast.makeText(PublicPracticeList.this, "연습 정보를 가져오는 데에 실패했습니다.", Toast.LENGTH_SHORT).show();
                    finish();
                }
            });
        }
    }

    private void setPracticeListView() {
        practicelist_layout.removeAllViews();

        LayoutInflater layoutInflater = LayoutInflater.from(PublicPracticeList.this);
        //LayoutInflater layoutInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);


        if (publicPracticesList != null) {
            System.out.println("publicPracticesList : " + publicPracticesList.toString());  //임시, 확인용
            for (int i=0; i<publicPracticesList.size(); i++) {
                View customView = layoutInflater.inflate(R.layout.custom_practice_info, null);
                PracticesData practicesData = publicPracticesList.get(i);

                Long id = practicesData.getId();
                String title = practicesData.getTitle();
                String sort = practicesData.getSort();
                String scope = practicesData.getScope();
                String state = practicesData.getAnalysis().getState();  //COMPLETE, INCOMPLETE
                //Long practice_user_id = practicesData.getUserId();
                getPracticeInfo(id);

                Drawable drawable;
                if (state.equals("COMPLETE"))
                    drawable = ContextCompat.getDrawable(this, R.drawable.ic_100percent);
                else drawable = ContextCompat.getDrawable(this, R.drawable.ic_loading);

                ((LinearLayout)customView.findViewById(R.id.container)).setTag(id+":"+title+":"+state+":"+scope+":"+sort);
                ((TextView)customView.findViewById(R.id.tv_title)).setText(title);
                ((TextView)customView.findViewById(R.id.tv_id)).setText("id: "+id.intValue());
                ((TextView)customView.findViewById(R.id.tv_sort)).setText(sort);
                ((TextView)customView.findViewById(R.id.tv_scope)).setText(scope);
                ((ImageView)customView.findViewById(R.id.iv_finished)).setImageDrawable(drawable);

                practicelist_layout.addView(customView);
            }

        } else {
            System.out.println("publicPracticesList is null...");
        }
    }

    public void onClickPractice(View view) {
        String tag = (String) view.getTag();
        System.out.println("tag = " + tag);  //임시, 확인용
        String[] tag_split = tag.split(":");
        Long id = Long.valueOf(tag_split[0]);
        String title = tag_split[1];
        String state = tag_split[2];
        String scope = tag_split[3];
        String sort = tag_split[4];

        //Toast.makeText(this, "id: " + id + ", title: " + title, Toast.LENGTH_SHORT).show();  //임시, 확인용

        Intent intent = new Intent(PublicPracticeList.this, ViewFriendPracticePlayActivity.class);
        intent.putExtra("practice_id", id);
        intent.putExtra("practice_title", title);
        intent.putExtra("practice_state", state);
        intent.putExtra("practice_scope", scope);
        intent.putExtra("practice_sort", sort);
        intent.putExtra("friend_name", friend_name);
        intent.putExtra("practice_user_id", practice_user_id);

        intent.putExtra("PRENT ACTIVITY", "PublicPracticeList");
        startActivity(intent);
    }
}
