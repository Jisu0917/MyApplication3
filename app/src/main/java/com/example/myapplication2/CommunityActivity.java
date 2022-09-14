package com.example.myapplication2;

import static com.example.myapplication2.MainActivity.tabWidget;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;

import com.example.myapplication2.api.RetrofitAPI;
import com.example.myapplication2.api.RetrofitClient;
import com.example.myapplication2.api.dto.PostsData;
import com.example.myapplication2.api.dto.PracticesData;
import com.example.myapplication2.api.dto.UserInfoData;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CommunityActivity extends AppCompatActivity {
    final private String TAG = getClass().getSimpleName();

    Toolbar toolbar;
    ActionBar actionBar;

    static Long userId;
    static RetrofitAPI retrofitAPI;

    FloatingActionButton fab_register;

    ArrayList<PostsData> postsDataList;

    LinearLayout community_layout;
    TextView tv_loading;

    public static Context context;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_community);
        setTitle("게시판");
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        //툴바
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        actionBar = getSupportActionBar();
        actionBar.setDisplayShowCustomEnabled(true);
        actionBar.setDisplayShowTitleEnabled(false);//기본 제목을 없애줍니다.

        context = this;

        Intent it = getIntent();
        userId = it.getLongExtra("id", 0);

        tabWidget.setVisibility(View.VISIBLE);

        community_layout = (LinearLayout) findViewById(R.id.community_layout);
        tv_loading = (TextView) findViewById(R.id.tv_loading);

        getAllPost();  //게시물 리스트 불러오기

        fab_register = findViewById(R.id.fab_register);
        fab_register.show();
        fab_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(CommunityActivity.this, RegisterActivity.class);
                intent.putExtra("userid", userId);
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();

        getAllPost();  //게시물 리스트 불러오기
    }

    public void getAllPost(){
        System.out.println("CommunityActivity: getUserInfo");  //임시, 확인용
        userId = MainActivity.userId;
        RetrofitClient retrofitClient = RetrofitClient.getInstance();

        postsDataList = new ArrayList<>();

        if (retrofitClient!=null){
            retrofitAPI = RetrofitClient.getRetrofitAPI();
            retrofitAPI.getAllPosts().enqueue(new Callback<ArrayList<PostsData>>() {
                @Override
                public void onResponse(Call<ArrayList<PostsData>> call, Response<ArrayList<PostsData>> response) {
                    ArrayList<PostsData> tmpPostData = response.body();

                    if (tmpPostData!=null){
                        Log.d("CommunityActivity: GET_ALLPOSTS", "GET SUCCESS");
                        Log.d("CommunityActivity: GET_ALLPOSTS", ">>>response.body()=" + response.body());


                        // 중복 제거
                        for (int i=0; i<tmpPostData.size(); i++) {
                            boolean isDuplicated = false;
                            for (int j=0; j<postsDataList.size(); j++) {
                                if (tmpPostData.get(i).getId() == postsDataList.get(j).getId()) {
                                    isDuplicated = true;
                                    break;
                                }
                            }
                            if (!isDuplicated) {
                                postsDataList.add(tmpPostData.get(i));
                            }
                        }

                        setPostListView();
                    }
                }

                @Override
                public void onFailure(Call<ArrayList<PostsData>> call, Throwable throwable) {
                    Log.d("GET_ALLPOSTS", "GET FAILED");
                }
            });
        }
    }

    private void setPostListView() {
        System.out.println("CommunityActivity: setPostListView");  //임시, 확인용

        community_layout.removeAllViews();

        LayoutInflater layoutInflater = LayoutInflater.from(CommunityActivity.this);
        //LayoutInflater layoutInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);


        if (postsDataList != null) {
            System.out.println("postsDataList : " + postsDataList);  //임시, 확인용
            if (postsDataList.size() == 0) {  //연습목록이 비어있을 때
                tv_loading.setText("아직 등록된 게시물이 없습니다.");
            }
            else {  //검색된 게시글 목록이 존재할 때
                tv_loading.setVisibility(View.GONE);
                for (int i = 0; i< postsDataList.size(); i++) {
                    View customView = layoutInflater.inflate(R.layout.custom_post_info, null);
                    PostsData postsData = postsDataList.get(i);

                    Long id = postsData.getId();
                    String title = postsData.getTitle();
                    String content = postsData.getContent();
                    Long practice_id = postsData.getPracticeId();
                    Long user_id = postsData.getUserId();

                    ((LinearLayout)customView.findViewById(R.id.container)).setTag(id+"@:#"+title+"@:#"+content+"@:#"+practice_id+"@:#"+user_id);
                    ((TextView)customView.findViewById(R.id.tv_title)).setText(title);
                    ((TextView)customView.findViewById(R.id.tv_content)).setText(content);
                    ((TextView)customView.findViewById(R.id.tv_id)).setText("id: "+id.intValue());

                    registerForContextMenu((LinearLayout)customView.findViewById(R.id.container));  //register context menu

                    community_layout.addView(customView);
                }
            }
        } else {
            System.out.println("postDataList is null...");
        }
    }

    public void onClickPost(View view) {
        String tag = (String) view.getTag();
        String[] tag_split = tag.split("@:#");
        Long id = Long.valueOf(tag_split[0]);
        String title = tag_split[1];
        String content = tag_split[2];
        Long practice_id = Long.valueOf(tag_split[3]);
        Long user_id = Long.valueOf(tag_split[4]);

        Intent intent = new Intent(CommunityActivity.this, DetailActivity.class);
        intent.putExtra("title", title);
        intent.putExtra("content", content);
        intent.putExtra("postId", id);
        intent.putExtra("practiceId", practice_id);
        intent.putExtra("practice_user_id", user_id);

        intent.putExtra("userid", userId);
        startActivity(intent);
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
            getAllPost();  //새로고침

            System.out.println("게시물 리스트를 업데이트 합니다.");  //임시, 확인용

            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
