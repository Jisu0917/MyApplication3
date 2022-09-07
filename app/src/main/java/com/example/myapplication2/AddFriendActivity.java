package com.example.myapplication2;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.myapplication2.api.RetrofitAPI;
import com.example.myapplication2.api.RetrofitClient;
import com.example.myapplication2.api.dto.FriendIdCodeData;
import com.example.myapplication2.api.dto.UserInfoData;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AddFriendActivity extends AppCompatActivity {
    EditText editText_name;
    Button btn_search, btn_cancel;
    LinearLayout result_layout;

    String search_name = "";
    Long userId = MainActivity.userId;
    static RetrofitAPI retrofitAPI;

    UserInfoData[] userInfoDataList;
    UserInfoData userInfoData;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_addfriend);
        setTitle("회원 검색 및 친구 추가");

        editText_name = (EditText) findViewById(R.id.editText_name);
        btn_search = (Button) findViewById(R.id.btn_search);
        btn_cancel = (Button) findViewById(R.id.btn_cancel);
        result_layout = (LinearLayout) findViewById(R.id.result_layout);

        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setResult(111);  //cancel
                finish();
            }
        });

    }

    // btn_search
    public void search(View view) {
//        setResult(333);  //임시, 확인용
        
        search_name = editText_name.getText().toString();

        searchUserInfos(search_name);
    }

    private void searchUserInfos(String name) {
        System.out.println("서버에 회원 이름 검색 시작");

        RetrofitClient retrofitClient = RetrofitClient.getInstance();

        if (retrofitClient!=null){
            retrofitAPI = RetrofitClient.getRetrofitAPI();
            retrofitAPI.searchUserInfos(name).enqueue(new Callback<UserInfoData[]>() {
                @Override
                public void onResponse(Call<UserInfoData[]> call, Response<UserInfoData[]> response) {
                    if (response.isSuccessful()){
                        userInfoDataList = response.body();
                        if (userInfoDataList != null) {
                            Log.d("GET_USER_INFO", "GET SUCCESS");
                            Log.d("GET_USER_INFO", userInfoDataList.toString());

                            setResultView();
                        } else {
                            System.out.println("GET_USER_INFO : userInfoDataList is null...");
                        }
                    }
                    else {
                        System.out.println("@@@@ GET_USER_INFO : response is not successful...");
                        System.out.println("@@@@ GET_USER_INFO : response code : " + response.code());  //400
                    }
                }

                @Override
                public void onFailure(Call<UserInfoData[]> call, Throwable t) {
                    Log.d("GET_USER_INFO", "GET FAILED");
                }
            });
        }
    }

    private void searchUserInfo(String name) {
        System.out.println("서버에 회원 이름 검색 시작");

        RetrofitClient retrofitClient = RetrofitClient.getInstance();

        if (retrofitClient!=null){
            retrofitAPI = RetrofitClient.getRetrofitAPI();
            retrofitAPI.searchUserInfo(name).enqueue(new Callback<UserInfoData>() {
                @Override
                public void onResponse(Call<UserInfoData> call, Response<UserInfoData> response) {
                    if (response.isSuccessful()){
                        userInfoData = response.body();
                        if (userInfoData != null) {
                            Log.d("GET_USER_INFO", "GET SUCCESS");
                            Log.d("GET_USER_INFO", userInfoData.toString());

                            setResultView();
                        } else {
                            System.out.println("GET_USER_INFO : userInfoDataList is null...");
                        }
                    }
                    else {
                        System.out.println("@@@@ GET_USER_INFO : response is not successful...");
                        System.out.println("@@@@ GET_USER_INFO : response code : " + response.code());  //400
                    }
                }

                @Override
                public void onFailure(Call<UserInfoData> call, Throwable t) {
                    Log.d("GET_USER_INFO", "GET FAILED");
                }
            });
        }
    }

    private void setResultView() {
        result_layout.removeAllViews();

        LayoutInflater layoutInflater = LayoutInflater.from(AddFriendActivity.this);
        //LayoutInflater layoutInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);


        if (userInfoDataList != null) {
            for (int i=0; i<userInfoDataList.length; i++) {
                View customView = layoutInflater.inflate(R.layout.custom_friend_info, null);
                UserInfoData userInfoData = userInfoDataList[i];

                Long id = userInfoData.getId();
                String name = userInfoData.getName();
                String email = userInfoData.getEmail();
                String picture = userInfoData.getPicture();

                ((LinearLayout)customView.findViewById(R.id.container)).setTag(id+":"+email);
                ((TextView)customView.findViewById(R.id.tv_name)).setText(name);
                ((TextView)customView.findViewById(R.id.tv_id)).setText("id: "+id.intValue());
                ((TextView)customView.findViewById(R.id.tv_email)).setText(email);
                if (picture!=null) Glide.with(this).load(picture).into((ImageView)customView.findViewById(R.id.profile_image));

                result_layout.addView(customView);
            }

        } else {
            System.out.println("userInfoDataList is null...");
        }
    }

    public void onClickFriend(View view) {
        Toast.makeText(this, "onClickFriend dialog 띄우기 - 친구 추가하시겠습니까?", Toast.LENGTH_SHORT).show();  //임시, 확인용

        //다이얼로그에서 친구 추가 (yes) 버튼 누르면
        String tag = (String) view.getTag();
        String[] tag_split = tag.split(":");
        Long id = Long.valueOf(tag_split[0]);
        String email = tag_split[1];
        String[] email_split = email.split("@");
        String emailID = email_split[0];
        String code = emailID + tag_split[0];

        Toast.makeText(this, "id: " + id + ", code: " + code, Toast.LENGTH_SHORT).show();  //임시, 확인용

        FriendIdCodeData friendIdCodeData = new FriendIdCodeData();
        friendIdCodeData.setFriend_id(id);
        friendIdCodeData.setFriendCode(code);

        makeFriend(friendIdCodeData);
    }

    private void makeFriend(FriendIdCodeData friendIdCode) {
        System.out.println("서버에 친구 추가");

        RetrofitClient retrofitClient = RetrofitClient.getInstance();

        if (retrofitClient!=null){
            retrofitAPI = RetrofitClient.getRetrofitAPI();
            retrofitAPI.makeFriend(friendIdCode).enqueue(new Callback<Long>() {
                @Override
                public void onResponse(Call<Long> call, Response<Long> response) {
                    Log.d("POST", "not success yet");
                    if (response.isSuccessful()){
                        Log.d("POST", "POST Success!");
                        Log.d("POST", ">>>response.body()="+response.body());

                        Toast.makeText(getApplicationContext(), "친구가 추가되었습니다.", Toast.LENGTH_SHORT).show();
                        setResult(999);
                        finish();
                    }
                    else {
                        System.out.println("@@@@ response is not successful...");
                        System.out.println("@@@@ response code : " + response.code());
                        setResult(222);
                    }
                }

                @Override
                public void onFailure(Call<Long> call, Throwable t) {
                    Log.d("POST", "POST Failed");
                    Log.d("POST", t.getMessage());
                    setResult(222);
                }
            });
        }
    }
}
