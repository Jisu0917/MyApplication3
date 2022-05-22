package com.example.myapplication2;

import android.Manifest;
import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.drawable.GradientDrawable;
import android.hardware.Camera;
import android.media.CamcorderProfile;
import android.media.MediaRecorder;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.io.File;
import java.util.Date;

import static com.example.myapplication2.MainActivity.EXTERNAL_STORAGE_PATH;

public class RecordActivity extends AppCompatActivity {
    public static final String TAG = "RecordActivity";
    public static String RECORDED_DIR = "myRec";
    static String filename = "";

    File f;

    MediaRecorder recorder;

    private static CameraPreview surfaceView;
    private SurfaceHolder holder;
    private static Camera mCamera;
    private int RESULT_PERMISSIONS = 100;
    public static RecordActivity getInstance;

    RelativeLayout guide;
    RelativeLayout relativeLayout_btns;

    ImageView recentRecImg;

    public static Context context;

    Intent it;
    Integer practice_index = 0;

    DBHelper dbHelper;
    SQLiteDatabase db = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        it = getIntent();
        practice_index = it.getIntExtra("PRACTICE_INDEX", 0);

        dbHelper = new DBHelper(this, 4);
        db = dbHelper.getWritableDatabase();    // 읽기/쓰기 모드로 데이터베이스를 오픈


        // 카메라 프리뷰를  전체화면으로 보여주기 위해 셋팅한다.
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        context = getApplicationContext();

        // 안드로이드 6.0 이상 버전에서는 CAMERA 권한 허가를 요청한다.
        requestPermissionCamera();

        File directory = new File(Environment.getExternalStorageDirectory() + File.separator+RECORDED_DIR);
        if (!directory.exists()) { // 원하는 경로에 폴더가 있는지 확인
            directory.mkdirs();
            Log.d(TAG, "Directory Created");
        }


        guide = (RelativeLayout) findViewById(R.id.guide);
        guide.setVisibility(View.VISIBLE);

        final ImageView recStartBtn = (ImageView) findViewById(R.id.recStartBtn);
        final ImageView recStopBtn = (ImageView) findViewById(R.id.recStopBtn);
        final ImageView recFileListBtn = (ImageView) findViewById(R.id.recFileList);
        final ImageView analysisBtn = (ImageView) findViewById(R.id.imageview_analysis_start);
        analysisBtn.setVisibility(View.GONE);

        recentRecImg = (ImageView) findViewById(R.id.recentRecFile);
        GradientDrawable drawable = (GradientDrawable) this.getDrawable(R.drawable.background_rounding);
        recentRecImg.setBackground(drawable);
        recentRecImg.setClipToOutline(true);

        f = new File(EXTERNAL_STORAGE_PATH + "/" + RECORDED_DIR);
        setThumbnail();

        recStartBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                guide.setVisibility(View.GONE);
                analysisBtn.setVisibility(View.GONE);

                try {
                    if (recorder == null) {
                        recorder = new MediaRecorder();
                    }

                    mCamera.unlock();
                    recorder.setCamera(mCamera);
                    recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
                    recorder.setVideoSource(MediaRecorder.VideoSource.CAMERA);
                    //recorder.setProfile(CamcorderProfile.get(CamcorderProfile.QUALITY_HIGH));
                    recorder.setProfile(CamcorderProfile.get(Camera.CameraInfo.CAMERA_FACING_FRONT, CamcorderProfile.QUALITY_HIGH));


                    recorder.setMaxDuration(1800000);  // 1800sec = 30min
                    recorder.setMaxFileSize(500000000);  // 500Mb
                    recorder.setPreviewDisplay(surfaceView.getHolder().getSurface());
                    recorder.setOrientationHint(90);



//                    recorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
//                    recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);


                    filename = createFilename();
                    Log.d(TAG, "current filename : " + filename);
                    recorder.setOutputFile(filename);
                    recorder.setPreviewDisplay(holder.getSurface());
                    recorder.prepare();
                    recorder.start();
                    recStartBtn.setVisibility(View.INVISIBLE);
                    recStopBtn.setVisibility(View.VISIBLE);
                } catch (Exception ex) {
                    Log.e(TAG, "Exception : ", ex);
                    recorder.release();
                    recorder = null;
                }
            }
        });

        recStopBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (recorder == null) return;
                try {
                    recorder.stop();
                    recorder.reset();
                    recorder.release();
                } catch (Exception e) {
                    Log.w(TAG, "recStopBtn", e);
                } finally {
                    recorder = null;
                }

                analysisBtn.setVisibility(View.VISIBLE);
                recStopBtn.setVisibility(View.INVISIBLE);
                recStartBtn.setVisibility(View.VISIBLE);

                Toast.makeText(RecordActivity.this, "재촬영을 하려면 촬영 버튼을 다시 누르세요.", Toast.LENGTH_SHORT).show();

                /*
                 * 촬영 버튼을 다시 만드는 게 아니라 업로드 버튼, 재촬영 버튼이 나오게끔 하기
                 * dialog로 띄우거나...
                 * */

                ContentValues values = new ContentValues(10);

                values.put(MediaStore.MediaColumns.TITLE, "RecordedVideo");
                values.put(MediaStore.Audio.Media.ALBUM, "Video Album");
                values.put(MediaStore.Audio.Media.ARTIST, "Mike");
                values.put(MediaStore.MediaColumns.DISPLAY_NAME, "RecordedVideo");
                values.put(MediaStore.MediaColumns.DATE_ADDED, System.currentTimeMillis()/1000);
                values.put(MediaStore.MediaColumns.MIME_TYPE, "video/mp4");
                values.put(MediaStore.Audio.Media.DATA, filename);

                Uri videoUri = getContentResolver().insert(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, values);
                if (videoUri == null) {
                    Log.d("SampleVideoRecorder", "Video insert failed.");
                    return;
                }
                sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, videoUri));

                setThumbnail();


                // DB에 파일명 저장
                db.execSQL("UPDATE tableName SET filename = '" + filename + "' WHERE mid = " + practice_index);
            }
        });

        recFileListBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        analysisBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
                Toast.makeText(RecordActivity.this, "분석을 시작합니다. 이 작업은 시간이 걸립니다.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();

        guide.setVisibility(View.VISIBLE);
    }

    public static Camera getCamera(){
        return mCamera;
    }
    private void setInit(){
        getInstance = this;

        // 카메라 객체를 R.layout.activity_main의 레이아웃에 선언한 SurfaceView에서 먼저 정의해야 함으로 setContentView 보다 먼저 정의한다.
        mCamera = Camera.open(findFrontSideCamera());

        setContentView(R.layout.activity_record);

        // SurfaceView를 상속받은 레이아웃을 정의한다.
        surfaceView = (CameraPreview) findViewById(R.id.preview);


        // SurfaceView 정의 - holder와 Callback을 정의한다.
        holder = surfaceView.getHolder();
        holder.addCallback(surfaceView);
        holder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);

    }

    public boolean requestPermissionCamera(){
        int sdkVersion = Build.VERSION.SDK_INT;
        if(sdkVersion >= Build.VERSION_CODES.M) {

            if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {

                ActivityCompat.requestPermissions(RecordActivity.this,
                        new String[]{Manifest.permission.CAMERA},
                        RESULT_PERMISSIONS);

            }else {
                setInit();
            }
        }else{  // version 6 이하일때
            setInit();
            return true;
        }

        return true;
    }


    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (RESULT_PERMISSIONS == requestCode) {

            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // 권한 허가시
                setInit();
            } else {
                // 권한 거부시
            }
            return;
        }

    }

    private String createFilename() {
        String newFilename = "";
        if (EXTERNAL_STORAGE_PATH == null || EXTERNAL_STORAGE_PATH.equals("")) {
            newFilename = RECORDED_DIR + "/" + getNowTime() + ".mp4";
        } else {
            newFilename = EXTERNAL_STORAGE_PATH + "/" + RECORDED_DIR + "/" + getNowTime() + ".mp4";
        }

        return newFilename;
    }

    private String getNowTime() {
        String nowTime = "";
        Date date = new Date();
        nowTime += (date.getYear() + 1900) + "" + (date.getMonth() + 1) + "" + date.getDate() + "_" + date.getHours() + "" + date.getMinutes() + "" + date.getSeconds();

        return nowTime;
    }

    private void setThumbnail() {
        String recentFilePath = getRecentFilePath();
        if (!recentFilePath.equals("NONE")) {
            try {
                // 썸네일 추출후 리사이즈해서 다시 비트맵 생성
                Bitmap bitmap = ThumbnailUtils.createVideoThumbnail(recentFilePath, MediaStore.Video.Thumbnails.MICRO_KIND);
                Bitmap thumbnail = ThumbnailUtils.extractThumbnail(bitmap, 300, 300);
                recentRecImg.setImageBitmap(thumbnail);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private String getRecentFilePath() {
        File[] files = f.listFiles();
        if (files.length == 0) {
            return "NONE";
        } else {
            String path = files[0].getPath();
            Date lastModDate = new Date(files[0].lastModified());
            for (File file : files) {
                Date date = new Date(file.lastModified());
                if (date.compareTo(lastModDate) > 0) {
                    lastModDate = date;
                    path = file.getPath();
                }
            }
            return path;
        }
    }

    public void setCameraDisplayOrientation(Activity activity,
                                            int cameraId, Camera camera) {
        Camera.CameraInfo info =
                new Camera.CameraInfo();

        Camera.getCameraInfo(cameraId, info);

        int rotation = activity.getWindowManager().getDefaultDisplay().getRotation();
        int degrees = 0;

        switch (rotation) {
            case Surface.ROTATION_0: degrees = 0; break;
            case Surface.ROTATION_90: degrees = 90; break;
            case Surface.ROTATION_180: degrees = 180; break;
            case Surface.ROTATION_270: degrees = 270; break;
        }

        int result;
        if (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
            result = (info.orientation + degrees) % 360;
            result = (360 - result) % 360;  // compensate the mirror
        } else {  // back-facing
            result = (info.orientation - degrees + 360) % 360;
        }

        this.getResources().getConfiguration().orientation = result;
        camera.setDisplayOrientation(result);
    }

    private int findFrontSideCamera() {
        int cameraId = -1;
        int numberOfCameras = Camera.getNumberOfCameras();
        for (int i = 0; i < numberOfCameras; i++) {
            Camera.CameraInfo cmInfo = new Camera.CameraInfo();
            Camera.getCameraInfo(i, cmInfo);
            if (cmInfo.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
                cameraId = i;
                break;
            }
        }
        return cameraId;
    }

}