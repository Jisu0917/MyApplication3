package com.example.myapplication2;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.myapplication2.api.RetrofitAPI;
import com.example.myapplication2.api.RetrofitClient;
import com.example.myapplication2.api.dto.FriendsData;
import com.example.myapplication2.api.dto.UserInfoData;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

//import static com.example.myapplication2.MainActivity.tabWidget;

public class FriendActivity1 extends AppCompatActivity {
    final private String TAG = getClass().getSimpleName();

    static Long userId = MainActivity.userId;
    static RetrofitAPI retrofitAPI;

    ListView listView;
    FloatingActionButton fab_add_friends;

    FriendsData[] friends;
    ArrayList<String> friendStringList = new ArrayList<>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friend1);

        // 친구 목록 리스트
        listView = findViewById(R.id.listView);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                Toast.makeText(FriendActivity1.this, adapterView.getItemAtPosition(i)+ " 클릭", Toast.LENGTH_SHORT).show();
                adapterView.getTag();

                Intent intent = new Intent(FriendActivity1.this, PublicPracticeList.class);

                intent.putExtra("friend_id", "");

                startActivity(intent);

            }
        });

        // 친구 추가 버튼
        fab_add_friends = findViewById(R.id.fab_add_friends);
        fab_add_friends.show();
        fab_add_friends.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(FriendActivity1.this, AddFriendActivity.class);
                intent.putExtra("userId", userId);
                startActivity(intent);
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

        GetFriends getFriends = new GetFriends();
        getFriends.execute();
    }

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

//            friendStringList.clear();
//            Long friend_id;
//            String friend_name = "";
//            // ...
//            if (friends != null) {
//                for (int i=0; i<friends.length; i++) {
//                    friend_id = friends[0].getFriend_id();
//                    friend_name = friends[0].getFriend_name();
//
//                    friendStringList.add("id: @" + friend_id.intValue() + " / name: " + friend_name);
//                }
//            } else {
//                System.out.println("friends is null...");
//            }
//
//            ArrayAdapter arrayAdapter = new ArrayAdapter<String>(FriendActivity.this, android.R.layout.simple_list_item_1, friendStringList);
//            listView.setAdapter(arrayAdapter);
        }

        @Override
        protected String doInBackground(String... strings) {

            getUserInfo();
            
            return null;
        }

        private void getUserInfo(){
            RetrofitClient retrofitClient = RetrofitClient.getInstance();

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

                            friendStringList.clear();
                            Long friend_id;
                            String friend_name = "";
                            // ...
                            if (friends != null) {
                                for (int i=0; i<friends.length; i++) {
                                    friend_id = friends[0].getFriend_id();
                                    friend_name = friends[0].getFriend_name();

                                    friendStringList.add("id: @" + friend_id.intValue() + " / name: " + friend_name);
                                }
                            } else {
                                System.out.println("friends is null...");
                            }

                            ArrayAdapter arrayAdapter = new ArrayAdapter<String>(FriendActivity1.this, android.R.layout.simple_list_item_1, friendStringList);
                            listView.setAdapter(arrayAdapter);
                        }
                    }

                    @Override
                    public void onFailure(Call<UserInfoData> call, Throwable t) {
                        Log.d("GET_USERINFO", "GET FAILED");
                    }
                });
            }
        }
    }
}
