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

public class ViewPracticeActivity extends AppCompatActivity {
    Intent it;
    Long practice_id;
    String practice_title, practice_state;

    TextView tv_practiceTitle;
    ImageView videoThumbnail;
    ImageButton btn_play;
    Button btn_showAnalysis;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_viewpractice);

        tv_practiceTitle = (TextView) findViewById(R.id.tv_practice_title);
        videoThumbnail = (ImageView) findViewById(R.id.imageview_thumbnail);
        btn_play = (ImageButton) findViewById(R.id.btn_play);
        btn_showAnalysis = (Button) findViewById(R.id.btn_show_analysis);

        // Practice Title
        it = getIntent();
        practice_id = it.getLongExtra("practice_id", 0);
        practice_title = it.getStringExtra("practice_title");
        practice_state = it.getStringExtra("practice_state");

        tv_practiceTitle.setText(practice_title);


//        // 비디오 썸네일 보이기
//        try {
//            // 썸네일 추출후 리사이즈해서 다시 비트맵 생성
//            Bitmap bitmap = ThumbnailUtils.createVideoThumbnail(practiceFilename, MediaStore.Video.Thumbnails.MICRO_KIND);
//            Bitmap thumbnail = ThumbnailUtils.extractThumbnail(bitmap, 300, 300);
//            videoThumbnail.setImageBitmap(thumbnail);
//
//        } catch (Exception e) {
//            e.printStackTrace();
//        }

        btn_play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Toast.makeText(ShowPracticeActivity.this, "play 버튼 클릭", Toast.LENGTH_SHORT).show(); //임시, 확인용
                Intent intent = new Intent(ViewPracticeActivity.this, PlayPracticeActivity.class);
                intent.putExtra("practice_id", practice_id);
                startActivity(intent);
            }
        });

        btn_showAnalysis.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (practice_state.equals("COMPLETE")) {
                    Intent intent = new Intent(ViewPracticeActivity.this, ViewAnalysisActivity.class);
                    intent.putExtra("practice_id", practice_id);
                    intent.putExtra("practice_title", practice_title);
                    startActivity(intent);
                } else {
                    Toast.makeText(ViewPracticeActivity.this, "분석이 완료되지 않았습니다.", Toast.LENGTH_SHORT).show();
                }
            }
        });


    }
}
