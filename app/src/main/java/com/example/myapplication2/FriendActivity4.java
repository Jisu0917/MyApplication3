package com.example.myapplication2;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.bumptech.glide.Glide;
import com.example.myapplication2.api.RetrofitAPI;
import com.example.myapplication2.api.RetrofitClient;
import com.example.myapplication2.api.dto.FriendsData;
import com.example.myapplication2.api.dto.UserInfoData;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.TreeMap;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FriendActivity4 extends AppCompatActivity {
    final private String TAG = getClass().getSimpleName();

    Toolbar toolbar;
    ActionBar actionBar;

    static Long userId;
    static RetrofitAPI retrofitAPI;

    LinearLayout friendlist_layout;
    FloatingActionButton fab_add_friends;

    FriendsData[] friends;

    ArrayList<UserInfoData> friendInfoList;
    TreeMap<Long, UserInfoData> friendInfoDataMap = new TreeMap<Long, UserInfoData>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friend);
        setTitle("내 친구 목록");
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        //툴바
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        actionBar = getSupportActionBar();
        actionBar.setDisplayShowCustomEnabled(true);
        actionBar.setDisplayShowTitleEnabled(false);//기본 제목을 없애줍니다.

        Intent it = getIntent();
        userId = it.getLongExtra("id", 0);


        friendlist_layout = (LinearLayout) findViewById(R.id.friendlist_layout);

        getUserInfo();

        // 친구 추가 버튼
        fab_add_friends = findViewById(R.id.fab_add_friends);
        fab_add_friends.show();
        fab_add_friends.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(FriendActivity4.this, AddFriendActivity.class);
                intent.putExtra("userId", userId);
                startActivity(intent);
                //startActivityForResult(intent, 0);
            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();

        // 임시, 확인용
        System.out.println("onResume() 실행");

        getUserInfo();
    }

    // 액션 바 아이콘 메뉴
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.actionbar_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle presses on the action bar items
        if (item.getItemId() == R.id.action_btn_reload) {
            getUserInfo();  //새로고침

            System.out.println("친구 리스트를 업데이트 합니다.");  //임시, 확인용

            return true;
        }
        return super.onOptionsItemSelected(item);
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

    private void getUserInfo(){
        System.out.println("FriendActivity : getUserInfo Called");  //임시, 확인용

        RetrofitClient retrofitClient = RetrofitClient.getInstance();

        friends = null;

        if (retrofitClient!=null){
            retrofitAPI = RetrofitClient.getRetrofitAPI();
            retrofitAPI.getUserInfo(userId).enqueue(new Callback<UserInfoData>() {
                @Override
                public void onResponse(Call<UserInfoData> call, Response<UserInfoData> response) {
                    UserInfoData userInfoData = response.body();
                    if (userInfoData!=null) {
                        Log.d("GET_USERINFO", "GET SUCCESS");
                        Log.d("GET_USERINFO", ">>>response.body()=" + response.body());

                        friends = userInfoData.getFriends();
                        System.out.println("friends.length : " + friends.length);  //임시, 확인용

                        friendInfoList = new ArrayList<>();
                        getFriendInfo(friends.length);
                    } else {
                            System.out.println("friends is null...");
                    }
                }

                @Override
                public void onFailure(Call<UserInfoData> call, Throwable t) {
                    Log.d("GET_USERINFO", "GET FAILED");
                }
            });
        }
    }

    private void getFriendInfo(int n) {
        System.out.println("FriendActivity : getFriendInfo("+ n + ")  Called");  //임시, 확인용

        if (n == 0) {
            System.out.println("재귀함수 getFriendInfo() 종료");

            System.out.println("재귀함수 getFriendInfo() 종료 후 friendInfoList : " + friendInfoList);  //임시, 확인용

            setFriendView();
            return;
        }

        Long friend_id = friends[n-1].getFriend_id();
        System.out.println("재귀함수 getFriendInfo(" + n + ") : friend_id=" + friend_id);  //임시, 확인용

        RetrofitClient retrofitClient = RetrofitClient.getInstance();

        if (retrofitClient!=null){
            retrofitAPI = RetrofitClient.getRetrofitAPI();
            retrofitAPI.getUserInfo(friend_id).enqueue(new Callback<UserInfoData>() {
                @Override
                public void onResponse(Call<UserInfoData> call, Response<UserInfoData> response) {
                    if (response.isSuccessful()) {
                        UserInfoData friendInfoData = response.body();
                        if (friendInfoData != null) {
                            Log.d("getFriendInfo", "GET SUCCESS");
                            Log.d("getFriendInfo", ">>>response.body()=" + response.body());

                            System.out.println("재귀함수 getFriendInfo(" + n + ") : friendInfoData=" + friendInfoData);  //임시 확인용

                            friendInfoList.add(friendInfoData);

                            getFriendInfo(n - 1);
                        } else {
                            System.out.println("재귀함수 getFriendInfo(" + n + ") : friendInfoData is null...");  //임시 확인용
                        }
                    } else {
                        System.out.println("FriendActivity : getFriendInfo("+ n + ") : @@@@ response is not successful...");
                        System.out.println("FriendActivity : getFriendInfo("+ n + ") : @@@@ response code : " + response.code());  //500
                    }
                }

                @Override
                public void onFailure(Call<UserInfoData> call, Throwable t) {
                    Log.d("getFriendInfo", "GET FAILED");
                }
            });
        }

    }

    private void setFriendView() {
        friendlist_layout.removeAllViews();

        LayoutInflater layoutInflater = LayoutInflater.from(FriendActivity4.this);
        //LayoutInflater layoutInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        if (friendInfoList != null) {
            //중복 제거 & id(친구의 user_id)순으로 정렬
            for (int i = 0; i < friendInfoList.size(); i++) {
                System.out.println("friendInfoList.get(i).getId() : " + friendInfoList.get(i).getId());  //임시, 확인용
                friendInfoDataMap.put(friendInfoList.get(i).getId(), friendInfoList.get(i));
            }
            if (friendInfoDataMap.size() == 0) {
                View customView = layoutInflater.inflate(R.layout.custom_textview, null);

                ((TextView)customView.findViewById(R.id.custom_textView)).setText("+ 버튼을 눌러 친구를 추가하세요.");
                friendlist_layout.addView(customView);
            } else {
                for (Long nKey : friendInfoDataMap.keySet()) {
                    View customView = layoutInflater.inflate(R.layout.custom_friend_info, null);
                    UserInfoData userInfoData = friendInfoDataMap.get(nKey);

                    Long id = userInfoData.getId();
                    String name = userInfoData.getName();
                    String email = userInfoData.getEmail();
                    String picture = userInfoData.getPicture();

                    ((LinearLayout) customView.findViewById(R.id.container)).setTag(id + ":" + email + ":" + name);
                    ((TextView) customView.findViewById(R.id.tv_name)).setText(name);
                    ((TextView) customView.findViewById(R.id.tv_id)).setText("id: " + id.intValue());
                    ((TextView) customView.findViewById(R.id.tv_email)).setText(email);
                    if (picture != null)
                        Glide.with(this).load(picture).into((ImageView) customView.findViewById(R.id.profile_image));

                    friendlist_layout.addView(customView);
                }
            }
        } else {
            System.out.println("friendInfoList is null...");
        }
    }

    // 친구 목록에서 특정 친구를 클릭했을 때
    public void onClickFriend(View view) {
        String tag = (String) view.getTag();
        String[] tag_split = tag.split(":");
        Long id = Long.valueOf(tag_split[0]);
        String name = tag_split[2];

        //Toast.makeText(this, "id: " + id + ", name: " + name, Toast.LENGTH_SHORT).show();  //임시, 확인용

        Intent intent = new Intent(FriendActivity4.this, PublicPracticeList.class);
        intent.putExtra("friend_id", id);
        intent.putExtra("friend_name", name);

        intent.putExtra("userId", userId);
        startActivity(intent);
    }
}