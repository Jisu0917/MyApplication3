package com.example.myapplication2;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.media.ThumbnailUtils;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class ShowPracticeActivity extends AppCompatActivity {
    Intent it;
    int practice_index;

    TextView tv_practiceTitle;
    ImageView videoThumbnail;
    ImageButton btn_play;
    Button btn_showAnalysis;

    DBHelper dbHelper;
    SQLiteDatabase db = null;
    Cursor cursor;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_showpractice);

        tv_practiceTitle = (TextView) findViewById(R.id.tv_practice_title);
        videoThumbnail = (ImageView) findViewById(R.id.imageview_thumbnail);
        btn_play = (ImageButton) findViewById(R.id.btn_play);
        btn_showAnalysis = (Button) findViewById(R.id.btn_show_analysis);

        // Practice Title
        it = getIntent();
        practice_index = it.getIntExtra("ITEM_INDEX", 0);

        dbHelper = new DBHelper(this, 4);
        db = dbHelper.getReadableDatabase();    // 읽기 모드로 데이터베이스를 오픈

        cursor = db.rawQuery(" SELECT * FROM tableName ", null);
        startManagingCursor(cursor);    // 엑티비티의 생명주기와 커서의 생명주기를 같게 한다.
        cursor.moveToFirst();
        cursor.moveToPosition(practice_index);  // position에 해당하는 row로 가서
        String practiceTitle = cursor.getString(1);
        String practiceFilename = cursor.getString(10);

        tv_practiceTitle.setText(practiceTitle);


        // 비디오 썸네일 보이기
        try {
            // 썸네일 추출후 리사이즈해서 다시 비트맵 생성
            Bitmap bitmap = ThumbnailUtils.createVideoThumbnail(practiceFilename, MediaStore.Video.Thumbnails.MICRO_KIND);
            Bitmap thumbnail = ThumbnailUtils.extractThumbnail(bitmap, 300, 300);
            videoThumbnail.setImageBitmap(thumbnail);

        } catch (Exception e) {
            e.printStackTrace();
        }

        btn_play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Toast.makeText(ShowPracticeActivity.this, "play 버튼 클릭", Toast.LENGTH_SHORT).show(); //임시, 확인용
                Intent intent = new Intent(ShowPracticeActivity.this, PlayActivity.class);
                intent.putExtra("PRACTICE_INDEX", practice_index);
                startActivity(intent);
            }
        });

        btn_showAnalysis.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int finished = cursor.getInt(9);
                if (finished == 1) {
                    Intent intent = new Intent(ShowPracticeActivity.this, ShowAnalysisActivity.class);
                    intent.putExtra("PRACTICE_INDEX", practice_index);
                    startActivity(intent);
                } else {
                    Toast.makeText(ShowPracticeActivity.this, "분석이 완료되지 않았습니다.", Toast.LENGTH_SHORT).show();
                }
            }
        });


    }
}
