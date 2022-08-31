package com.example.myapplication2;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.myapplication2.api.RetrofitAPI;
import com.example.myapplication2.api.RetrofitClient;
import com.example.myapplication2.api.dto.AnalysisData;
import com.example.myapplication2.api.dto.PracticesData;
import com.example.myapplication2.api.objects.UserIdObject;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EditPracticeActivity extends AppCompatActivity {
    Intent it;
    Cursor cursor;
    int index;

    EditText editText_content;

    ImageView imageView_star;
    int isStarFilled = 0;
    ListView listView;
    Button btn_cancel, btn_save;

    DBHelper dbHelper;
    SQLiteDatabase db = null;

    public static Context context;

    static String personName;
    static String idToken;
    static Long userId = MainActivity.userId;

    static RetrofitAPI retrofitAPI;
    static UserIdObject userIdObject;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editpractice);

        context = getApplicationContext();

        editText_content = (EditText)findViewById(R.id.editText_practice_title);
        imageView_star = (ImageView)findViewById(R.id.imageVie_star);
        listView = findViewById(R.id.listview1);
        btn_cancel = findViewById(R.id.btn_cancel);
        btn_save = findViewById(R.id.btn_save);

        dbHelper = new DBHelper(this, 4);
        db = dbHelper.getWritableDatabase();    // 읽기/쓰기 모드로 데이터베이스를 오픈

        it = getIntent();
        index = it.getIntExtra("INDEX", 0);
        cursor = db.rawQuery(" SELECT * FROM tableName ", null);
        startManagingCursor(cursor);    // 엑티비티의 생명주기와 커서의 생명주기를 같게 한다.
        cursor.moveToPosition(index);  // position에 해당하는 row로 가서
        editText_content.setText(cursor.getString(1));


        if (cursor.getInt(8) == 1) {
            isStarFilled = 1;
            imageView_star.setImageResource(R.drawable.ic_star_filled);
        } else {
            isStarFilled = 0;
            imageView_star.setImageResource(R.drawable.ic_star_empty);
        }


        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        btn_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String content = editText_content.getText().toString();
                if (!content.equals("")) {
                    db.execSQL("UPDATE tableName SET content = '" + content + "' WHERE mid = " + (index + 1));
                    db.execSQL("UPDATE tableName SET starfill = " + isStarFilled + " WHERE mid = " + (index + 1));

                    updatePractice(index);

                    finish();
                } else {
                    Toast.makeText(getApplicationContext(), "내용을 입력하지 않았습니다.", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    // xml에서 호출
    public void setStarFilled(View view) {
        if (isStarFilled == 0) {
            imageView_star.setImageResource(R.drawable.ic_star_filled);
            isStarFilled = 1;
        } else if (isStarFilled == 1) {
            imageView_star.setImageResource(R.drawable.ic_star_empty);
            isStarFilled = 0;
        }
    }

    private void updatePractice(int dbPosition) {
        System.out.println("서버에 연습 업데이트 시작");

        /* RecordActivity - postNewPractice 복붙 */
        dbHelper = new DBHelper(context, 4);
        db = dbHelper.getWritableDatabase();    // 읽기/쓰기 모드로 데이터베이스를 오픈


        Cursor cursor = db.rawQuery(" SELECT * FROM tableName ", null);
        startManagingCursor(cursor);    // 엑티비티의 생명주기와 커서의 생명주기를 같게 한다.
        cursor.moveToPosition(dbPosition);
        //cursor.moveToLast();

        Long practice_id = Long.valueOf(cursor.getInt(11));

        String title = cursor.getString(1);  // 1 : content

        String audioPath = cursor.getString(10);  // 10 : filename(url)

        String scope = "";

        int starfill = cursor.getInt(8);  // 8 : starfill. 0 = PUBLIC, 1 = PRIVATE
        if (starfill == 0) {
            scope = "PUBLIC";
        } else {
            scope = "PRIVATE";
        }

        AnalysisData analysisData = new AnalysisData();
        int finished = cursor.getInt(9); // 9 : finished. 0 (unfinished), 1 (finished)
        if (finished == 0) {
            analysisData.setState("INCOMPLETE");
        } else {
            analysisData.setState("COMPLETE");
        }

        String sort = "ONLINE";  // 임시, 확인용 - 온라인, 오프라인 설정하는 거 addpracticeactivity에 추가해야함
        String gender = "WOMEN";    // 임시, 확인용 - gender 설정??? 추가해야함

        PracticesData practice = new PracticesData();
        practice.setId(practice_id);
        practice.setUserId(userId);
        practice.setTitle(title);
        practice.setScope(scope);
        practice.setSort(sort);
        practice.setGender(gender);
        practice.setMoveSensitivity(3);  // 임시, 확인용
        practice.setEyesSensitivity(3);  // 임시, 확인용


        printPractice(practice);  // 임시, 확인용


        RetrofitClient retrofitClient = RetrofitClient.getInstance();

        if (retrofitClient!=null){
            retrofitAPI = RetrofitClient.getRetrofitAPI();

            retrofitAPI.updatePractice(practice_id, practice).enqueue(new Callback<Long>() {
                @Override
                public void onResponse(Call<Long> call, Response<Long> response) {
                    Log.d("POST", "not success yet");
                    if (response.isSuccessful()){
                        Log.d("POST", "POST Success!");
                        Log.d("POST", ">>>response.body()="+response.body());
                    }
                    else {
                        System.out.println("@@@@ response is not successful...");
                        System.out.println("@@@@ response code : " + response.code());
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

    // 확인용
    private void printPractice(PracticesData practicesData) {
        System.out.println("id: " + practicesData.getId());
        System.out.println("title: " + practicesData.getTitle());
        System.out.println("user_id: " + practicesData.getUserId());
        System.out.println("scope: " + practicesData.getScope());
        System.out.println("sort: " + practicesData.getSort());
    }
}
