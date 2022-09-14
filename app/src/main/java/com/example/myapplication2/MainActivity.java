package com.example.myapplication2;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.TabActivity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TabHost;
import android.widget.TabWidget;
import android.widget.TextView;
import android.widget.Toast;

import com.example.myapplication2.api.RetrofitAPI;
import com.example.myapplication2.api.RetrofitClient;
import com.example.myapplication2.api.dto.LoginRequestDto;
import com.example.myapplication2.api.dto.PointData;
import com.example.myapplication2.api.dto.PracticesData;
import com.example.myapplication2.api.dto.UserInfoData;
import com.example.myapplication2.api.objects.UserIdObject;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.tabs.TabLayout;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends TabActivity implements GoogleApiClient.OnConnectionFailedListener {
    static String EXTERNAL_STORAGE_PATH = "";

    public static Context context_main;
    public static TabHost myTabHost = null;
    TabHost.TabSpec spec;

    static TabWidget tabWidget;
    static Button login_btn;
    static LinearLayout linearLayout_hello;

    private final int RC_SIGN_IN = 123;
    GoogleSignInClient mGoogleSignInClient;
    GoogleApiClient mGoogleApiClient;

    static String personName;
    static String idToken;
    static Long userId;

    static RetrofitAPI retrofitAPI;
    static UserIdObject userIdObject;

    static Intent intent2, intent3, intent4, intent5;

    TabHost.TabSpec tabSpec1, tabSpec2, tabSpec3, tabSpec4;
    View view1, view2, view3, view4;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        context_main = this;

        // For Record Activity
        String state = Environment.getExternalStorageState();
        if (!state.equals(Environment.MEDIA_MOUNTED)) {
            Toast.makeText(getApplicationContext(), "외장 메모리가 마운트 되지 않았습니다.", Toast.LENGTH_LONG).show();
        } else {
            EXTERNAL_STORAGE_PATH = Environment.getExternalStorageDirectory().getAbsolutePath();
        }


        login_btn = (Button)findViewById(R.id.login_button);
        linearLayout_hello = (LinearLayout) findViewById(R.id.Linearlayout_hello);
        linearLayout_hello.setVisibility(View.GONE);

        checkDangerousPermissions();

        //로그인 옵션 설정
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.server_client_id))
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(MainActivity.this, gso);

        //로그인 한 적 있을 경우 silentSignIn 실행
        mGoogleSignInClient.silentSignIn().addOnCompleteListener(
                this,
                new OnCompleteListener<GoogleSignInAccount>() {
                    @Override
                    public void onComplete(@NonNull Task<GoogleSignInAccount> task) {
                        Log.d("LOGIN", "silentSignIn");
                        handleSignInResult(task);
                    }
                }
        );
        /////
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();
        mGoogleApiClient.connect();
        /////


        tabWidget = (TabWidget)findViewById(android.R.id.tabs);

        myTabHost = getTabHost();

//        Intent intent1 = new Intent(MainActivity.this, RecordActivity.class);
        intent2 = new Intent(MainActivity.this, CommunityActivity.class);
//        intent2 = new Intent(MainActivity.this, ListActivity.class);
        intent3 = new Intent(MainActivity.this, HomeActivity1.class);
        intent4 = new Intent(MainActivity.this, FriendActivity4.class);
        intent5 = new Intent(MainActivity.this, SettingsActivity.class);

        //TabHost 아이콘 넣기
        LayoutInflater vi1 = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        LayoutInflater vi2 = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        LayoutInflater vi3 = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        LayoutInflater vi4 = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        view1 = (View) vi1.inflate(R.layout.tab_row_item, null);
        view2 = (View) vi2.inflate(R.layout.tab_row_item, null);
        view3 = (View) vi3.inflate(R.layout.tab_row_item, null);
        view4 = (View) vi4.inflate(R.layout.tab_row_item, null);

        LinearLayout layout1 = (LinearLayout) view1.findViewById(R.id.Linearlayout01);
        LinearLayout layout2 = (LinearLayout) view2.findViewById(R.id.Linearlayout01);
        LinearLayout layout3 = (LinearLayout) view3.findViewById(R.id.Linearlayout01);
        LinearLayout layout4 = (LinearLayout) view4.findViewById(R.id.Linearlayout01);

//        layout1.setBackgroundResource(R.drawable.selector);
//        layout2.setBackgroundResource(R.drawable.selector);
//        layout3.setBackgroundResource(R.drawable.selector);
//        layout4.setBackgroundResource(R.drawable.selector);
        layout1.setBackgroundColor(ContextCompat.getColor(this, R.color.white));
        layout2.setBackgroundColor(ContextCompat.getColor(this, R.color.white));
        layout3.setBackgroundColor(ContextCompat.getColor(this, R.color.white));
        layout4.setBackgroundColor(ContextCompat.getColor(this, R.color.white));

        ImageView iv1 = (ImageView) view1.findViewById(R.id.icon);
        ImageView iv2 = (ImageView) view2.findViewById(R.id.icon);
        ImageView iv3 = (ImageView) view3.findViewById(R.id.icon);
        ImageView iv4 = (ImageView) view4.findViewById(R.id.icon);

        iv1.setImageResource(R.drawable.ic_community);
        iv2.setImageResource(R.drawable.ic_home);
        iv3.setImageResource(R.drawable.ic_friend);
        iv4.setImageResource(R.drawable.ic_settings);

        TextView tv1 = (TextView) view1.findViewById(R.id.text);
        TextView tv2 = (TextView) view2.findViewById(R.id.text);
        TextView tv3 = (TextView) view3.findViewById(R.id.text);
        TextView tv4 = (TextView) view4.findViewById(R.id.text);

        tv1.setText("POSTS");
        tv2.setText("HOME");
        tv3.setText("FRIEND");
        tv4.setText("MY PAGE");


        //TabWidget 색상 변경
        myTabHost.setOnTabChangedListener(new TabHost.OnTabChangeListener() {

            @Override
            public void onTabChanged(String tabId) {

                for (int i = 0; i < myTabHost.getTabWidget().getChildCount(); i++) {
                    myTabHost.getTabWidget().getChildAt(i).setBackgroundColor(Color.parseColor("#ffffff")); // unselected
                }

                myTabHost.getTabWidget().getChildAt(myTabHost.getCurrentTab()).setBackgroundColor(Color.parseColor("#609cbfff")); // selected
            }
        });

        // 로그인 버튼 클릭 시
        login_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent signInIntent = mGoogleSignInClient.getSignInIntent();
                startActivityForResult(signInIntent, RC_SIGN_IN);
            }
        });

    }  // end of onCreate

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN){
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            Log.d("LOGIN", "OnClickSignIn");
            handleSignInResult(task);
        }
    }

    private void handleSignInResult(Task<GoogleSignInAccount> completedTask){
        try{
            GoogleSignInAccount account = completedTask.getResult(ApiException.class);

            // TODO: 성공적으로 로그인 한 경우, UI 업데이트
            if (account!=null){
                Log.d("LOGIN", "success");
                idToken = account.getIdToken();
                personName = account.getDisplayName();
                Log.d("idToken=", idToken);
                Log.d("personName=", personName);
                login_btn.setVisibility(View.GONE);
                linearLayout_hello.setVisibility(View.GONE);


                postUserLogin(idToken);

            }
        } catch (ApiException e){
            String TAG = "MainActivity";
            Log.w(TAG, "signInResult:failed code=" + e.getStatusCode());
            //TODO: 로그인 되어 있지 않은 경우 UI
            login_btn.setVisibility(View.VISIBLE);

        }
    }

//    //-- 권한 요청 --//
//    private void checkDangerousPermissions() {
//        String[] permissions = { Manifest.permission.READ_EXTERNAL_STORAGE,
//                Manifest.permission.WRITE_EXTERNAL_STORAGE,
//                Manifest.permission.CAMERA,
//                Manifest.permission.RECORD_AUDIO };
//
//        for (int i = 0; i < permissions.length; i++) {
//            int permissionCheck = ContextCompat.checkSelfPermission(this, permissions[i]);
//            if (permissionCheck != PackageManager.PERMISSION_GRANTED) {  // 권한이 허용되지 않은 경우
//                // 권한을 재요청 하는 경우 ('다시 묻지 않기' 체크박스가 자동 추가됨)
//                if (ActivityCompat.shouldShowRequestPermissionRationale(this, permissions[i])) {
//                    ActivityCompat.requestPermissions(this, permissions, 1);
//                }
//                // 권한을 처음 요청하는 경우
//                else {
//                    ActivityCompat.requestPermissions(this, permissions, 1);
//                }
//            } else {
//                break;
//            }
//        }
//    }
//
//    // 사용자의 권한 승인/거절에 대한 대응 (요청이 거절되면 grantResults는 null값)
//    @Override
//    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
//        if (requestCode == 1) {
//            for (int i = 0; i < permissions.length; i++) {
//                if (grantResults[i] == PackageManager.PERMISSION_GRANTED) {
//                    Toast.makeText(this, permissions[i] + " 권한이 승인됨.", Toast.LENGTH_LONG).show();
//                } else {
//                    Toast.makeText(this, "권한 요청을 승인해야 사용 가능합니다.", Toast.LENGTH_LONG).show();
//                }
//                return;
//            }
//        }
//    }


    private void checkDangerousPermissions() {
        String[] permissions = {
                Manifest.permission.RECORD_AUDIO,
                Manifest.permission.CAMERA,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.INTERNET,
                Manifest.permission.VIBRATE
        };

        int permissionCheck = PackageManager.PERMISSION_GRANTED;
        for (int i = 0; i < permissions.length; i++) {
            permissionCheck = ContextCompat.checkSelfPermission(this, permissions[i]);
            if (permissionCheck == PackageManager.PERMISSION_DENIED) {
                break;
            }
        }

        if (permissionCheck == PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(this, "권한 있음", Toast.LENGTH_SHORT).show();
        } else {
            ActivityCompat.requestPermissions(this, permissions, 1);
        }

        Animation anim;
        anim = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.wave);
        linearLayout_hello.setVisibility(View.VISIBLE);
        linearLayout_hello.startAnimation(anim);
    }

    //send ID Token to server and validate
    private void postUserLogin(String idToken){
        RetrofitClient retrofitClient = RetrofitClient.getInstance();

        if (retrofitClient!=null){
            retrofitAPI = RetrofitClient.getRetrofitAPI();
            retrofitAPI.postLoginToken(new LoginRequestDto(idToken, -1L)).enqueue(new Callback<LoginRequestDto>() {
                @Override
                public void onResponse(Call<LoginRequestDto> call, Response<LoginRequestDto> response) {
                    Log.d("POST", "not success yet");
                    if (response.isSuccessful()){
                        Log.d("POST", "POST Success!");
                        Log.d("POST", ">>>user_id="+response.body().getUser_id().toString());
                        userIdObject = new UserIdObject(response.body().getUser_id());
                        getUserInfo(userIdObject);

                        /////
                        userId = response.body().getUser_id();
                        ((UserPoints)getApplication()).setUserId(userId);

                        ((UserPoints)getApplication()).updateUserPoint(0, "join");  //회원가입 30 포인트 지급


//                        spec = myTabHost.newTabSpec("Community").setIndicator("POSTS").setContent(intent2);
//                        myTabHost.addTab(spec);
//
//                        spec = myTabHost.newTabSpec("Home").setIndicator("HOME").setContent(intent3);
//                        myTabHost.addTab(spec);
//
//                        spec = myTabHost.newTabSpec("Friend").setIndicator("FRIEND").setContent(intent4);
//                        myTabHost.addTab(spec);
//
//                        spec = myTabHost.newTabSpec("Settings").setIndicator("MY PAGE", getResources().getDrawable(R.drawable.selector)).setContent(intent5);
//                        myTabHost.addTab(spec);
//
//                        myTabHost.setCurrentTab(1);  //메인 Tab 지정

                        tabSpec1 = myTabHost.newTabSpec("POSTS");
                        tabSpec2 = myTabHost.newTabSpec("HOME");
                        tabSpec3 = myTabHost.newTabSpec("FRIEND");
                        tabSpec4 = myTabHost.newTabSpec("MY PAGE");

                        tabSpec1.setIndicator(view1);
                        tabSpec1.setContent(intent2);
                        tabSpec2.setIndicator(view2);
                        tabSpec2.setContent(intent3);
                        tabSpec3.setIndicator(view3);
                        tabSpec3.setContent(intent4);
                        tabSpec4.setIndicator(view4);
                        tabSpec4.setContent(intent5);

                        myTabHost.addTab(tabSpec1);
                        myTabHost.addTab(tabSpec2);
                        myTabHost.addTab(tabSpec3);
                        myTabHost.addTab(tabSpec4);

                        myTabHost.getTabWidget().setCurrentTab(1);
                        myTabHost.setCurrentTab(1);  //메인 Tab 지정
                    }
                }

                @Override
                public void onFailure(Call<LoginRequestDto> call, Throwable t) {
                    Log.d("POST", "POST Failed");
                    Log.d("POST", t.getMessage());
                }
            });
        }
    }

    //get user info by user_id
    private void getUserInfo(UserIdObject userIdObject){
        RetrofitClient retrofitClient = RetrofitClient.getInstance();

        if (retrofitClient!=null){
            retrofitAPI = RetrofitClient.getRetrofitAPI();
            retrofitAPI.getUserInfo(userIdObject.getId()).enqueue(new Callback<UserInfoData>() {
                @Override
                public void onResponse(Call<UserInfoData> call, Response<UserInfoData> response) {
                    UserInfoData userInfoData = response.body();
                    if (userInfoData!=null){
                        Log.d("GET_USERINFO", "GET SUCCESS");
                        Log.d("GET_USERINFO", "MainActivity: getUserInfo - response.body().getPoint()=" + response.body().getPoint());

                        putInfoToIntents(response.body());

                        ((UserPoints)getApplication()).setUserPoint(response.body().getPoint());
                    }
                }

                @Override
                public void onFailure(Call<UserInfoData> call, Throwable t) {
                    Log.d("GET_USERINFO", "GET FAILED");
                }
            });
        }
    }

    private void putInfoToIntents(UserInfoData userInfoData){
        Log.d("PUT_INTENTS", userInfoData.getEmail());
        // TODO: intent3 - HomeActivity1 <- Practices
        intent3.putExtra("practices", userInfoData.getPractices());

        // TODO: intent4 - FriendActivity4 <- Friends

        // intent5 - SettingsActivity <- basic Userinfo
        intent5.putExtra("id", userInfoData.getId());
        intent5.putExtra("name", userInfoData.getName());
        intent5.putExtra("email", userInfoData.getEmail());
        intent5.putExtra("picture", userInfoData.getPicture());
        //intent5.putExtra("point", ((UserPoint)getApplication()).getUserPoint());
        intent5.putExtra("num_of_practices", userInfoData.getPractices().length);
        intent5.putExtra("num_of_posts", userInfoData.getPosts().length);
        intent5.putExtra("num_of_friends", userInfoData.getFriends().length);
        intent5.putExtra("num_of_feedbacks", userInfoData.getFeedbacks().length);

        // intent2 - CommunityActivity <- basic Userinfo
        intent2.putExtra("id", userInfoData.getId());

        // intent3 - HomeActivity1 <- basic Userinfo
        intent3.putExtra("id", userInfoData.getId());

        // intent4 - FriendActivity4 <- basic Userinfo
        intent4.putExtra("id", userInfoData.getId());
    }

    // 포인트 업데이트 함수
    static public void updatePoint(int point, String insturction, Context context) {
        System.out.println("서버에 포인트 업데이트 시작");

        PointData pointData = new PointData();
        pointData.setUserId(userId);
        pointData.setPoint(point);
        pointData.setInstruction(insturction);

        RetrofitClient retrofitClient = RetrofitClient.getInstance();

        if (retrofitClient!=null){
            retrofitAPI = RetrofitClient.getRetrofitAPI();
            retrofitAPI.updatePoint(pointData).enqueue(new Callback<Long>() {
                @Override
                public void onResponse(Call<Long> call, Response<Long> response) {
                    Log.d("updatePoint: POST", "not success yet");
                    if (response.isSuccessful()){
                        Log.d("updatePoint: POST", "POST Success!");
                        Log.d("updatePoint: POST", ">>>response.body()="+response.body());

                        ((UserPoints)context.getApplicationContext()).updateUserPoint(point, insturction);
                        System.out.println("포인트가 업데이트 되었습니다. 현재 user_point: " + ((UserPoints)context.getApplicationContext()).getUserPoint());
                    }
                    else {
                        System.out.println("updatePoint: @@@@ response is not successful...");
                        System.out.println("updatePoint: @@@@ response code : " + response.code());  //500
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

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        
    }
}