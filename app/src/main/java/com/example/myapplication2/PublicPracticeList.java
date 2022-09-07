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
import androidx.appcompat.app.AppCompatActivity;
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
    static Long userId = MainActivity.userId;
    static RetrofitAPI retrofitAPI;

    Long friend_id;
    String friend_name;
    PracticesData[] practicesDataList;
    ArrayList<PracticesData> publicPracticesList;

    LinearLayout practicelist_layout;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_public_practicelist);

        Intent it = getIntent();
        friend_id = it.getLongExtra("friend_id", 0L);
        friend_name = it.getStringExtra("friend_name");
        setTitle("친구 " + friend_name +"의 공개된 연습 목록");

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
                Drawable drawable;
                if (state.equals("COMPLETE"))
                    drawable = ContextCompat.getDrawable(this, R.drawable.ic_100percent);
                else drawable = ContextCompat.getDrawable(this, R.drawable.ic_loading);

                ((LinearLayout)customView.findViewById(R.id.container)).setTag(id+":"+title+":"+sort);
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
        Toast.makeText(this, "onClickPractice 실행", Toast.LENGTH_SHORT).show();  //임시, 확인용
    }
}
