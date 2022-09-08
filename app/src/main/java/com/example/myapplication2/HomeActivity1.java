package com.example.myapplication2;

import static com.example.myapplication2.MainActivity.tabWidget;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.example.myapplication2.api.RetrofitAPI;
import com.example.myapplication2.api.RetrofitClient;
import com.example.myapplication2.api.dto.PracticesData;
import com.example.myapplication2.api.dto.UserInfoData;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeActivity1 extends AppCompatActivity {
    static Long userId = MainActivity.userId;
    static RetrofitAPI retrofitAPI;

    FloatingActionButton fab_del, fab_add;

    PracticesData[] practicesDataList;
    ArrayList<PracticesData> practicesList;

    LinearLayout home_layout;
    TextView tv_loading;

    DBHelper dbHelper;
    SQLiteDatabase db = null;
    Cursor cursor;

    Long selectedPracticeId;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home1);

        System.out.println("HomeActivity1: onCreate");  //임시, 확인용

        tabWidget.setVisibility(View.VISIBLE);

        home_layout = (LinearLayout) findViewById(R.id.home_layout);
        tv_loading = (TextView) findViewById(R.id.tv_loading);

        getUserInfo();

        dbHelper = new DBHelper(this, 4);
        db = dbHelper.getWritableDatabase();    // 읽기/쓰기 모드로 데이터베이스를 오픈

        fab_add = findViewById(R.id.fab_add);
        fab_add.show();
        fab_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HomeActivity1.this, AddPracticeActivity1.class);
                intent.putExtra("Home", "ADD");
                startActivity(intent);
            }
        });

        fab_del = findViewById(R.id.fab_del);
        fab_del.hide();
        fab_del.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(HomeActivity1.this, "fab_del click", Toast.LENGTH_SHORT).show();  //임시, 확인용
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();

        getUserInfo();
    }

    private void getUserInfo(){
        System.out.println("HomeActivity1: getUserInfo");  //임시, 확인용
        userId = MainActivity.userId;
        RetrofitClient retrofitClient = RetrofitClient.getInstance();

//        practicesDataList = null;
        practicesList = new ArrayList<>();

        if (retrofitClient!=null){
            retrofitAPI = RetrofitClient.getRetrofitAPI();
            System.out.println("userId: " + userId);  //임시, 확인용
            retrofitAPI.getUserInfo(userId).enqueue(new Callback<UserInfoData>() {
                @Override
                public void onResponse(Call<UserInfoData> call, Response<UserInfoData> response) {
                    UserInfoData userInfoData = response.body();
                    if (userInfoData!=null){
                        Log.d("HomeActivity1: GET_USERINFO", "GET SUCCESS");
                        Log.d("HomeActivity1: GET_USERINFO", ">>>response.body()=" + response.body());

//                        practicesDataList = userInfoData.getPractices();
                        PracticesData[] tmpData = userInfoData.getPractices();
                        // 중복 제거
                        for (int i=0; i<tmpData.length; i++) {
                            System.out.println("temData["+i+"].getId() : " + tmpData[i].getId());  //임시, 확인용
                            
                            boolean isDuplicated = false;
                            for (int j=0; j<practicesList.size(); j++) {
                                if (tmpData[i].getId() == practicesList.get(j).getId()) {
                                    isDuplicated = true;
                                    break;
                                }
                            }
                            if (!isDuplicated) {
                                practicesList.add(tmpData[i]);
                            }
                        }

//                        Collections.addAll(practicesList, practicesDataList);

                        setPracticeListView();
                    }
                }

                @Override
                public void onFailure(Call<UserInfoData> call, Throwable t) {
                    Log.d("HomeActivity1: GET_USERINFO", "GET FAILED");
                }
            });
        }
    }

    private void setPracticeListView() {
        System.out.println("HomeActivity1: setPracticeListView");  //임시, 확인용

        home_layout.removeAllViews();

        LayoutInflater layoutInflater = LayoutInflater.from(HomeActivity1.this);
        //LayoutInflater layoutInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);


        if (practicesList != null) {
            System.out.println("practicesList : " + practicesList);  //임시, 확인용
            if (practicesList.size() == 0) {  //연습목록이 비어있을 때
                tv_loading.setText("+ 버튼을 눌러 연습을 추가하세요.");
            }
            else {  //검색된 연습 목록이 존재할 때
                tv_loading.setVisibility(View.GONE);
                for (int i = 0; i< practicesList.size(); i++) {
                    View customView = layoutInflater.inflate(R.layout.custom_practice_info, null);
                    PracticesData practicesData = practicesList.get(i);

                    Long id = practicesData.getId();
                    String title = practicesData.getTitle();
                    String sort = practicesData.getSort();
                    String scope = practicesData.getScope();
                    String state = practicesData.getAnalysis().getState();  //COMPLETE, INCOMPLETE
                    Drawable drawable;
                    if (state.equals("COMPLETE"))
                        drawable = ContextCompat.getDrawable(this, R.drawable.ic_100percent);
                    else drawable = ContextCompat.getDrawable(this, R.drawable.ic_loading);

                    ((LinearLayout)customView.findViewById(R.id.container)).setTag(id+":"+title+":"+state);
                    ((TextView)customView.findViewById(R.id.tv_title)).setText(title);
                    ((TextView)customView.findViewById(R.id.tv_id)).setText("id: "+id.intValue());
                    ((TextView)customView.findViewById(R.id.tv_sort)).setText(sort);
                    ((TextView)customView.findViewById(R.id.tv_scope)).setText(scope);
                    ((ImageView)customView.findViewById(R.id.iv_finished)).setImageDrawable(drawable);

                    registerForContextMenu((LinearLayout)customView.findViewById(R.id.container));  //register context menu

                    home_layout.addView(customView);
                }
            }
        } else {
            System.out.println("practicesList is null...");
        }
    }

    public void onClickPractice(View view) {
        String tag = (String) view.getTag();
        String[] tag_split = tag.split(":");
        Long id = Long.valueOf(tag_split[0]);
        String title = tag_split[1];
        String state = tag_split[2];

        Toast.makeText(this, "id: " + id + ", title: " + title, Toast.LENGTH_SHORT).show();  //임시, 확인용

        Intent intent = new Intent(HomeActivity1.this, ViewPracticeActivity.class);
        intent.putExtra("practice_id", id);
        intent.putExtra("practice_title", title);
        intent.putExtra("practice_state", state);
        startActivity(intent);
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, final View v, ContextMenu.ContextMenuInfo menuInfo) {
        getMenuInflater().inflate(R.menu.context_menu, menu);

        String tag = (String) v.getTag();
        String[] tag_split = tag.split(":");
        selectedPracticeId = Long.valueOf(tag_split[0]);

        super.onCreateContextMenu(menu, v, menuInfo);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        //AdapterContextMenuInfo
        //AdapterView가 onCreateContextMenu할때의 추가적인 menu 정보를 관리하는 클래스

        Long practice_id = selectedPracticeId;

        switch( item.getItemId() ){
            case R.id.menu_edit:
                Intent intent = new Intent(HomeActivity1.this, EditPracticeActivity1.class);
                intent.putExtra("practice_id", practice_id);
                startActivity(intent);
                break;

            case R.id.menu_delete:

                // 내부저장소에서 동영상 파일 삭제
                cursor = db.rawQuery(" SELECT * FROM practiceTable WHERE practice_id = " + practice_id, null);
                startManagingCursor(cursor);    // 엑티비티의 생명주기와 커서의 생명주기를 같게 한다.
                cursor.moveToFirst();

                File file = new File(cursor.getString(1));  //file_url
                file.delete();
                
                // 내장DB에서 해당 줄 삭제
                String sql = " DELETE FROM practiceTable WHERE practice_id = " + practice_id;
                db.execSQL(sql);

                // 서버DB에서 해당 practice 정보 삭제
                deletePractice(practice_id);

                getUserInfo();  //새로고침
                break;
        }
        return true;
    };

    private void deletePractice(Long practice_id) {
        System.out.println("서버에서 선택한 연습 삭제 시작");

        RetrofitClient retrofitClient = RetrofitClient.getInstance();

        if (retrofitClient != null) {
            retrofitAPI = RetrofitClient.getRetrofitAPI();
            retrofitAPI.deletePractice(practice_id).enqueue(new Callback<Long>() {
                @Override
                public void onResponse(Call<Long> call, Response<Long> response) {
                    Log.d("DELETE", "not success yet");
                    if (response.isSuccessful()) {
                        Log.d("DELETE", "DELETE Success!");
                        Log.d("DELETE", ">>>response.body()=" + response.body());
                    } else {
                        System.out.println("@@@@ response is not successful...");
                        System.out.println("@@@@ response code : " + response.code());
                    }
                }

                @Override
                public void onFailure(Call<Long> call, Throwable t) {
                    Log.d("DELETE", "POST Failed");
                    Log.d("DELETE", t.getMessage());
                }
            });
        }

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

            System.out.println("리스트를 업데이트 합니다.");  //임시, 확인용

            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
