package com.example.myapplication2;

import android.app.ProgressDialog;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Random;

public class ShowAnalysisActivity extends AppCompatActivity {
    String CAPTURE_PATH = "/sookpeech_capture";
    int practice_index = 0;
    String practice_title = "";

    LinearLayout linearLayout;
    ScrollView scrollView;

    DBHelper dbHelper;
    SQLiteDatabase db = null;
    Cursor cursor;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_showanalysis);

        final Intent it = getIntent();
        practice_index = it.getIntExtra("PRACTICE_INDEX", 0);

        dbHelper = new DBHelper(this, 4);
        db = dbHelper.getReadableDatabase();    // 읽기/쓰기 모드로 데이터베이스를 오픈
        cursor = db.rawQuery(" SELECT * FROM tableName ", null);
        startManagingCursor(cursor);    // 엑티비티의 생명주기와 커서의 생명주기를 같게 한다.
        cursor.moveToPosition(practice_index);
        practice_title = cursor.getString(1);

        linearLayout = (LinearLayout) findViewById(R.id.linearlayout_analysis);
        scrollView = (ScrollView) findViewById(R.id.scrollView);

    }

    public void download_img(View v) {
//        ProgressDialog dialog = new ProgressDialog(getActivity());
//        dialog.setMessage("Saving...");
//        dialog.show();

        Bitmap bitmap = getBitmapFromView(scrollView,scrollView.getChildAt(0).getHeight(),scrollView.getChildAt(0).getWidth());
        try {
            File defaultFile = new File(Environment.getExternalStorageDirectory().getAbsolutePath()+CAPTURE_PATH);
            if (!defaultFile.exists())
                defaultFile.mkdirs();

            String filename = "Sookpeech_"+ practice_title +".jpg";
            File file = new File(defaultFile,filename);
            if (file.exists()) {
                file.delete();
                file = new File(defaultFile,filename);
            }

            FileOutputStream output = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, output);
            output.flush();
            output.close();

//            dialog.dismiss();

            Toast.makeText(ShowAnalysisActivity.this, "이미지를 다운로드 했습니다.", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            e.printStackTrace();
//            dialog.dismiss();
            Toast.makeText(ShowAnalysisActivity.this, "이미지를 다운로드 하는 데 실패했습니다.", Toast.LENGTH_SHORT).show();
        }
    }

    //create bitmap from the view
    private Bitmap getBitmapFromView(View view,int height,int width) {
        Bitmap bitmap = Bitmap.createBitmap(width+190, height,Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        Drawable bgDrawable =view.getBackground();
        if (bgDrawable!=null)
            bgDrawable.draw(canvas);
        else
            canvas.drawColor(Color.WHITE);
        view.draw(canvas);
        return bitmap;
    }

    public void show_graph(View v) {
        Intent intent = new Intent(ShowAnalysisActivity.this, GraphActivity.class);
        startActivity(intent);
    }

}
