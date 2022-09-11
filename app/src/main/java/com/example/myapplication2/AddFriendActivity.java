package com.example.myapplication2;

import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.myapplication2.api.RetrofitAPI;
import com.example.myapplication2.api.RetrofitClient;
import com.example.myapplication2.api.dto.FeedbacksData;
import com.example.myapplication2.api.dto.FriendIdCodeData;
import com.example.myapplication2.api.dto.UserInfoData;

import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AddFriendActivity extends AppCompatActivity {
    EditText editText_name;
    Button btn_search, btn_cancel;
    LinearLayout result_layout;

    String search_name = "";
    static Long userId = MainActivity.userId;
    static RetrofitAPI retrofitAPI;

    UserInfoData[] userInfoDataList;
    UserInfoData userInfoData;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_addfriend);
        setTitle("사용자 검색 및 친구 추가");
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        editText_name = (EditText) findViewById(R.id.editText_name);
        btn_search = (Button) findViewById(R.id.btn_search);
        btn_cancel = (Button) findViewById(R.id.btn_cancel);
        result_layout = (LinearLayout) findViewById(R.id.result_layout);

        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //setResult(111);  //cancel
                finish();
            }
        });

    }

    // btn_search
    public void search(View view) {
        search_name = editText_name.getText().toString();

        searchUserInfos(search_name);

        //hide keypad
        View view1 = getCurrentFocus();
        if (view1 != null) {
            InputMethodManager imm = (InputMethodManager) AddFriendActivity.this.getSystemService((Context.INPUT_METHOD_SERVICE));
            imm.hideSoftInputFromWindow(view1.getWindowToken(), 0);
        }
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
                        System.out.println("@@@@ GET_USER_INFO : response : " + response);
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
            if (userInfoDataList.length == 0) {  //검색된 회원이 1명도 없을 때
                View customView = layoutInflater.inflate(R.layout.custom_textview, null);
                
                ((TextView)customView.findViewById(R.id.custom_textView)).setText("검색된 회원이 없습니다.");
                result_layout.addView(customView);
            }
            
            else {  //검색된 회원이 존재할 때
                for (int i=0; i<userInfoDataList.length; i++) {
                    View customView = layoutInflater.inflate(R.layout.custom_friend_info, null);
                    UserInfoData userInfoData = userInfoDataList[i];

                    Long id = userInfoData.getId();
                    String name = userInfoData.getName();
                    String email = userInfoData.getEmail();
                    String picture = userInfoData.getPicture();

                    ((LinearLayout)customView.findViewById(R.id.container)).setTag(id+":"+email+":"+name);
                    ((TextView)customView.findViewById(R.id.tv_name)).setText(name);
                    ((TextView)customView.findViewById(R.id.tv_id)).setText("id: "+id.intValue());
                    ((TextView)customView.findViewById(R.id.tv_email)).setText(email);
                    if (picture!=null) Glide.with(this).load(picture).into((ImageView)customView.findViewById(R.id.profile_image));

                    result_layout.addView(customView);
                }
            }

        } else {
            System.out.println("userInfoDataList is null...");
        }
    }

    public void onClickFriend(View view) {
        //다이얼로그에서 친구 추가 (yes) 버튼 누르면
        String tag = (String) view.getTag();
        String[] tag_split = tag.split(":");
        Long id = Long.valueOf(tag_split[0]);
        String email = tag_split[1];
        String[] email_split = email.split("@");
        String emailID = email_split[0];
        String code = emailID + tag_split[0];
        String name = tag_split[2];

        if (id.equals(userId))
            Toast.makeText(this, "본인은 친구로 추가할 수 없습니다.", Toast.LENGTH_SHORT).show();
        else {  // 친구 추가 다이얼로그
            showFriendDialog(name, id, code);
        }
    }


    // 친구 추가하시겠습니까 다이얼로그 띄우기
    private void showFriendDialog(String friend_name, Long friend_id, String friendCode) {
        View dialogView = (View) View.inflate(
                this, R.layout.dialog_friend, null);
        AlertDialog.Builder dig = new AlertDialog.Builder(this, R.style.Theme_Dialog);
        dig.setView(dialogView);
        dig.setTitle("친구를 추가하시겠습니까?");

        final TextView textView = (TextView) dialogView.findViewById(R.id.tv_selectedname);
        textView.setText("선택한 사용자 이름 : " + friend_name);

        dig.setNegativeButton("취소", null);
        dig.setPositiveButton("확인", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                FriendIdCodeData friendIdCodeData = new FriendIdCodeData();
                friendIdCodeData.setFriend_id(friend_id);
                friendIdCodeData.setFriendCode(friendCode);

                makeFriend(friendIdCodeData);
            }
        });
        dig.show();
    }


    private void makeFriend(FriendIdCodeData friendIdCode) {
        System.out.println("서버에 친구 추가");

        RetrofitClient retrofitClient = RetrofitClient.getInstance();

        if (retrofitClient!=null){
            retrofitAPI = RetrofitClient.getRetrofitAPI();
            retrofitAPI.makeFriend(userId, friendIdCode).enqueue(new Callback<Long>() {
                @Override
                public void onResponse(Call<Long> call, Response<Long> response) {
                    Log.d("POST", "not success yet");
                    if (response.isSuccessful()){
                        Log.d("POST", "POST Success!");
                        Log.d("POST", ">>>response.body()="+response.body());

                        if (response.body() == -1) {
                            Toast.makeText(getApplicationContext(), "친구 추가에 실패하였습니다.", Toast.LENGTH_SHORT).show();
                            //setResult(222);
                        } else if (response.body() == -2) {
                            Toast.makeText(getApplicationContext(), "이미 추가된 친구입니다.", Toast.LENGTH_SHORT).show();
                            //setResult(222);
                        } else {
                            Toast.makeText(getApplicationContext(), "친구가 추가되었습니다.", Toast.LENGTH_SHORT).show();
                            //setResult(999);
                        }


                    }
                    else {
                        System.out.println("@@@@ response is not successful...");
                        System.out.println("@@@@ response code : " + response.code());  //400
                        //setResult(222);
                    }
                }

                @Override
                public void onFailure(Call<Long> call, Throwable t) {
                    Log.d("POST", "POST Failed");
                    Log.d("POST", t.getMessage());
                    //setResult(222);
                }
            });
        }
    }
}
