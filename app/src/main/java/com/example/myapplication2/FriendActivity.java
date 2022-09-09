package com.example.myapplication2;

import static java.lang.Thread.sleep;

import android.content.Intent;
import android.os.AsyncTask;
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

import com.bumptech.glide.Glide;
import com.example.myapplication2.api.RetrofitAPI;
import com.example.myapplication2.api.RetrofitClient;
import com.example.myapplication2.api.dto.FriendsData;
import com.example.myapplication2.api.dto.UserInfoData;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FriendActivity extends AppCompatActivity {
    final private String TAG = getClass().getSimpleName();

    static Long userId = MainActivity.userId;
    static RetrofitAPI retrofitAPI;

    LinearLayout friendlist_layout;
    FloatingActionButton fab_add_friends;

    FriendsData[] friends;
    ArrayList<String> friendStringList = new ArrayList<>();

    ArrayList<UserInfoData> userInfoDataList = new ArrayList<>();
//    UserInfoData[] userInfoDataList;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friend);

        userInfoDataList = new ArrayList<>();

        friendlist_layout = (LinearLayout) findViewById(R.id.friendlist_layout);

        // 친구 추가 버튼
        fab_add_friends = findViewById(R.id.fab_add_friends);
        fab_add_friends.show();
        fab_add_friends.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(FriendActivity.this, AddFriendActivity.class);
                intent.putExtra("userId", userId);
                startActivity(intent);
                //startActivityForResult(intent, 0);
            }
        });

        GetFriends getFriends = new GetFriends();
        getFriends.execute();
    }

    @Override
    protected void onResume() {
        super.onResume();

        // 임시, 확인용
        System.out.println("onResume() 실행");

        userInfoDataList = new ArrayList<>();

        GetFriends getFriends = new GetFriends();
        getFriends.execute();
    }

//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//
//        if (requestCode==0) {
//            if (resultCode==333) {  //친구 추가 성공
//                Log.d(TAG, "onActivityResult : list reload");
//                userInfoDataList = new ArrayList<>();
//
//                GetFriends getFriends = new GetFriends();
//                getFriends.execute();
//            }
//        }
//    }

    class GetFriends extends AsyncTask<String, Void, String> {

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
        protected String doInBackground(String... strings) {

            getUserInfo();

            return null;
        }

        private void getUserInfo(){
            RetrofitClient retrofitClient = RetrofitClient.getInstance();

            friends = null;

            if (retrofitClient!=null){
                retrofitAPI = RetrofitClient.getRetrofitAPI();
                retrofitAPI.getUserInfo(userId).enqueue(new Callback<UserInfoData>() {
                    @Override
                    public void onResponse(Call<UserInfoData> call, Response<UserInfoData> response) {
                        UserInfoData userInfoData = response.body();
                        if (userInfoData!=null){
                            Log.d("GET_USERINFO", "GET SUCCESS");
                            Log.d("GET_USERINFO", ">>>response.body()=" + response.body());

                            friends = userInfoData.getFriends();

                            Long friend_id;
                            // ...
                            if (friends != null) {
                                boolean isLast = false;
                                for (int i=0; i<friends.length; i++) {
                                    friend_id = friends[i].getFriend_id();

                                    if (i == friends.length - 1) {  //마지막 친구일 때
                                        isLast = true;
                                    }

                                    getFriendUserInfo(friend_id, isLast);
                                }
                                //setFriendListView();
                            } else {
                                System.out.println("friends is null...");
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<UserInfoData> call, Throwable t) {
                        Log.d("GET_USERINFO", "GET FAILED");
                    }
                });
            }
        }

        private void getFriendUserInfo(Long user_id, boolean isLast){
            RetrofitClient retrofitClient = RetrofitClient.getInstance();

            if (retrofitClient!=null){
                retrofitAPI = RetrofitClient.getRetrofitAPI();
                retrofitAPI.getUserInfo(user_id).enqueue(new Callback<UserInfoData>() {
                    @Override
                    public void onResponse(Call<UserInfoData> call, Response<UserInfoData> response) {
                        UserInfoData userInfoData = response.body();
                        if (userInfoData != null) {
                            Log.d("GET_FRIEND_USERINFO", "GET SUCCESS");
                            Log.d("GET_FRIEND_USERINFO", ">>>response.body()=" + response.body());

                            // 중복 추가 X
                            boolean isDuplicated = false;
                            Long id = userInfoData.getId();
                            if (userInfoDataList != null) {
                                for (int i = 0; i < userInfoDataList.size(); i++) {
                                    if ((userInfoDataList.get(i).getId()).equals(id)) {  //기존 id와 일치하면
                                        isDuplicated = true;
                                        break;
                                    }
                                }
                                if (!isDuplicated)
                                    userInfoDataList.add(userInfoData);
                            }

                            if (isLast) {
                                if (friends.length == userInfoDataList.size())
                                    setFriendListView();
                                else {
                                    System.out.println("리스트 길이가 다름!!! - friends.length: " + friends.length +", userInfoDataList.size(): " + userInfoDataList.size());  //임시, 확인용
                                    //Toast.makeText(getApplicationContext(), "리스트 길이가 다름!!!", Toast.LENGTH_SHORT).show();  //임시, 확인용
                                }
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<UserInfoData> call, Throwable t) {
                        Log.d("GET_FRIEND_USERINFO", "GET FAILED");
                    }
                });
            }
        }
    }

    private void setFriendListView() {
        friendlist_layout.removeAllViews();

        LayoutInflater layoutInflater = LayoutInflater.from(FriendActivity.this);
        //LayoutInflater layoutInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);


        if (userInfoDataList != null) {
            System.out.println("userInfoDataList : " + userInfoDataList.toString());  //임시, 확인용 - 하나여야 하는데 두 개가 들어있네...
            for (int i=0; i<userInfoDataList.size(); i++) {
                View customView = layoutInflater.inflate(R.layout.custom_friend_info, null);
                UserInfoData userInfoData = userInfoDataList.get(i);

                Long id = userInfoData.getId();
                String name = userInfoData.getName();
                String email = userInfoData.getEmail();
                String picture = userInfoData.getPicture();

                ((LinearLayout)customView.findViewById(R.id.container)).setTag(id+":"+email+":"+name);
                ((TextView)customView.findViewById(R.id.tv_name)).setText(name);
                ((TextView)customView.findViewById(R.id.tv_id)).setText("id: "+id.intValue());
                ((TextView)customView.findViewById(R.id.tv_email)).setText(email);
                if (picture!=null) Glide.with(this).load(picture).into((ImageView)customView.findViewById(R.id.profile_image));

                friendlist_layout.addView(customView);
            }

        } else {
            System.out.println("userInfoDataList is null...");
        }
    }

    // 친구 목록에서 특정 친구를 클릭했을 때
    public void onClickFriend(View view) {
        String tag = (String) view.getTag();
        String[] tag_split = tag.split(":");
        Long id = Long.valueOf(tag_split[0]);
        String name = tag_split[2];

        //Toast.makeText(this, "id: " + id + ", name: " + name, Toast.LENGTH_SHORT).show();  //임시, 확인용

        Intent intent = new Intent(FriendActivity.this, PublicPracticeList.class);
        intent.putExtra("friend_id", id);
        intent.putExtra("friend_name", name);
        startActivity(intent);
    }
}