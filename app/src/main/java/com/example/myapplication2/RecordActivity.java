package com.example.myapplication2;

import android.Manifest;
import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
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
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Date;

import static com.example.myapplication2.MainActivity.EXTERNAL_STORAGE_PATH;

import com.example.myapplication2.api.RetrofitAPI;
import com.example.myapplication2.api.RetrofitClient;
import com.example.myapplication2.api.RetrofitClient2;
import com.example.myapplication2.api.RetrofitClient3;
import com.example.myapplication2.api.dto.AnalysisData;
import com.example.myapplication2.api.dto.PracticesData;
import com.example.myapplication2.api.objects.UserIdObject;
import com.google.android.gms.common.api.internal.StatusCallback;
import com.google.gson.JsonObject;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

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


    static String personName;
    static String idToken;
    static Long userId = MainActivity.userId;

    static RetrofitAPI retrofitAPI;
    static UserIdObject userIdObject;

    static String presignedUrl = "";

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

        if (Build.VERSION.SDK_INT >= 30){
            System.out.println("android version >= 30");

            File destination = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MOVIES);
            if (!destination.exists()) { // 원하는 경로에 폴더가 있는지 확인
                destination.mkdirs();
                Log.d(TAG, "destination Created");
            }

            File directory = new File(destination + File.separator+RECORDED_DIR);
            if (!directory.exists()) { // 원하는 경로에 폴더가 있는지 확인
                directory.mkdirs();
                Log.d(TAG, "Directory Created");
            }
        } else{
            File directory = new File(Environment.getExternalStorageDirectory() + File.separator+RECORDED_DIR);
            if (!directory.exists()) { // 원하는 경로에 폴더가 있는지 확인
                directory.mkdirs();
                Log.d(TAG, "Directory Created");
            }
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
        System.out.println(f);
        //setThumbnail();

        recStartBtn.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.S)
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


                    if (Build.VERSION.SDK_INT >= 30){
                        System.out.println("android version >= 30");

                        filename = createFilename30();
                        Log.d(TAG, "current filename (30) : " + filename);
                        recorder.setOutputFile(filename);

                        //recorder.setOutputFile(destination + "/myRecordingFile01.mp4");
                    } else {
                        filename = createFilename();
                        Log.d(TAG, "current filename : " + filename);
                        recorder.setOutputFile(filename);
                    }

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

                //setThumbnail();


                // DB에 파일명 저장
                db.execSQL("UPDATE tableName SET filename = '" + filename + "' WHERE mid = " + practice_index);
                System.out.println("DB에 파일명 저장 완료");

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
                // 서버에 업로드
                postNewPractice(Long.valueOf(practice_index));

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

    private String createFilename30() {
        String newFilename = "";
        File destination = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MOVIES);

        newFilename = destination.getAbsolutePath() + "/" + RECORDED_DIR + "/" + getNowTime() + ".mp4";

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


    private void postNewPractice(Long practice_index) {
        System.out.println("서버에 업로드 시작");

        dbHelper = new DBHelper(context, 4);
        db = dbHelper.getWritableDatabase();    // 읽기/쓰기 모드로 데이터베이스를 오픈

        Cursor cursor = db.rawQuery(" SELECT * FROM tableName ", null);
        startManagingCursor(cursor);    // 엑티비티의 생명주기와 커서의 생명주기를 같게 한다.

        printDBPracticeId();  // 임시, 확인용

        //cursor.move(practice_index.intValue());
        cursor.moveToLast();

        //int mid = cursor.getInt(0);  // 내장 db mid
        int mid = getLastMid() + 1;
        
        System.out.println("#####mid: "+mid);  // 임시, 확인용

        String title = cursor.getString(1);  // 1 : content

        String audioPath = cursor.getString(10);  // 10 : filename(url)

        //this.scope = cursor.getInt(8);  // 8 : starfill. 0 = PUBLIC, 1 = PRIVATE

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
        practice.setUserId(userId);
        practice.setTitle(title);
        practice.setScope(scope);
        practice.setSort(sort);
        practice.setGender(gender);
        practice.setMoveSensitivity(10);  // 임시, 확인용
        practice.setEyesSensitivity(10);  // 임시, 확인용

//        // 임시, 확인용
//        PracticesData practice = new PracticesData();
//        practice.setUserId(3L);
//        practice.setTitle("title000");
//        practice.setScope("PRIVATE");
//        practice.setSort("ONLINE");
//        practice.setGender("MEN");
//        practice.setMoveSensitivity(2);  // 임시, 확인용
//        practice.setEyesSensitivity(2);  // 임시, 확인용


        RetrofitClient retrofitClient = RetrofitClient.getInstance();

        if (retrofitClient!=null){
            retrofitAPI = RetrofitClient.getRetrofitAPI();
            retrofitAPI.postNewPractice(practice).enqueue(new Callback<Long>() {
                @Override
                public void onResponse(Call<Long> call, Response<Long> response) {
                    Log.d("POST", "not success yet");
                    if (response.isSuccessful()){
                        Log.d("POST", "POST Success!");
                        Log.d("POST", ">>>response.body()="+response.body());

                        Long practice_id = response.body();

                        updatePracticeIdOnDB(mid, practice_id);  // 내장db에 practice_id 저장하기

                        getPresignedURL(practice_id, practice, mid);  // 영상 업로드 할 url 받아오기
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

    private void getPresignedURL(Long practice_id, PracticesData practice, int mid) {

        System.out.println("getPresignedURL 시작");

        RetrofitClient2 retrofitClient = RetrofitClient2.getInstance();

        Integer[] ids = {Math.toIntExact(userId), practice_index};

        if (retrofitClient!=null){
            retrofitAPI = RetrofitClient2.getRetrofitAPI();
            retrofitAPI.getPresignedURL(ids, userId, practice_id).enqueue(new Callback<JsonObject>() {
                @Override
                public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                    Log.d("POST", "not success yet");
                    if (response.isSuccessful()){
                        Log.d("POST", "POST Success!");
                        Log.d("POST", ">>>url="+response.body());

                        JsonObject resbody = response.body();

                        presignedUrl = String.valueOf(resbody.get("uploadURL"));

                        System.out.println("presignedUrl = " + presignedUrl);

                        try {
                            uploadVideoOkhttp(practice_id, practice, mid);
                        } catch (MalformedURLException e) {
                            e.printStackTrace();
                        }
                    }
                    else {
                        System.out.println("@@@@ response is not successful...");
                        System.out.println("@@@@ response code : " + response.code());
                    }
                }

                @Override
                public void onFailure(Call<JsonObject> call, Throwable t) {
                    Log.d("POST", "POST Failed");
                    Log.d("POST", t.getMessage());
                }
            });
        }
    }

    private void uploadVideoOkhttp(Long practice_id, PracticesData practice, int mid) throws MalformedURLException {
        System.out.println("uploadVideoOkhttp 시작");

        // 임시, 확인용
        String testFilename = "";
        if (EXTERNAL_STORAGE_PATH == null || EXTERNAL_STORAGE_PATH.equals("")) {
            testFilename = RECORDED_DIR + "/analysis_test_video.mp4";
        } else {
            testFilename = EXTERNAL_STORAGE_PATH + "/" + RECORDED_DIR + "/analysis_test_video.mp4";
        }

        OkHttpClient client = new OkHttpClient().newBuilder()
                .build();
        MediaType mediaType = MediaType.parse("video/mp4");
//        RequestBody body = RequestBody.create(mediaType, new File(filename));
        RequestBody body = RequestBody.create(mediaType, new File(testFilename));

//        RequestBody body = new MultipartBody.Builder().setType(MultipartBody.FORM)
//                .addFormDataPart("video",filename,
//                        RequestBody.create(MediaType.parse("application/octet-stream"),
//                                new File(filename)))
//                .build();

        System.out.println("presignedUrl : " + presignedUrl);  // 임시, 확인용

        String url_arr[] = presignedUrl.split(":");
        url_arr[1] = url_arr[1].replace("\"", "");

        Request request = new Request.Builder()
                //.url("https://sookpeech-wavfile.s3.ap-northeast-2.amazonaws.com/video_undefined_undefined.mp4?Content-Type=video%2Fmp4&X-Amz-Algorithm=AWS4-HMAC-SHA256&X-Amz-Credential=ASIAQ7FPEYTJT23I2HET%2F20220831%2Fap-northeast-2%2Fs3%2Faws4_request&X-Amz-Date=20220831T060603Z&X-Amz-Expires=900&X-Amz-Security-Token=IQoJb3JpZ2luX2VjEM7%2F%2F%2F%2F%2F%2F%2F%2F%2F%2FwEaDmFwLW5vcnRoZWFzdC0yIkYwRAIgM25swCMz1n9Mqqcjt5Yq0J5wTNtLGfZY7ajPE3BQGjoCIHOY%2Fg8nnHi6UnXlca54ouHjkuTmyj95m%2F0x7o7ErxLaKvcCCFcQARoMMDY2OTM5MzA3MjE5IgxTmIDDB%2B4uzqlzCHUq1AJJcN%2FOJT25oR7DI%2BvhcSjUgvdu4ndkcm3Ze8cgXheJCgIbpMyRsZhOjAtiFfLE0%2BRTWfRQBWCHTTVCNQqAeMRoNhoQs6R5vJbu5zo%2F%2FjB7cVAZY%2FP9OOKOuAVtKLyn8Wp0m5XNXx4OzrzA%2BwgiK%2FDNEvKmgVwZoqxa%2Fuj%2Fim16JT4UdiEMNhMd3RX8n%2BLSr0%2BGk7goD8pmRChhy4ygRI6EZB2GUbUDPdFVwHEMfvo88PhWt8JKhScosBDyqktsiPN1BPYf1rM781xwN3qgeRyq9wuRBrdpSS7KStITsSZFOl0ETMe8sgPZXvPazhC9REHXzw1h7Yk7%2BDjv6NUAka7EFKJ2Z4fhpBGFuAf%2Ba0WUXbEBuVTrOgaGgwGAO%2B2f9SbmROjjYYb1TmdPyXDP14m1ThOFhXTgRRLt0oOY435YUYJ3hG6nCYdt%2Fe86xweXZ9c%2BIkzmMMn0u5gGOp8B70jC8Yv2d7u8Anc%2BpvdGpHOSu%2BeI3vd2V%2B7PZ0RxqkN8cM7OeCRMH1xup8oYBvQ70cnjKkyhMMhrTlpyXRIBRtcUECxuUaNj%2B5n57il%2FLSjwiXikrwSjgrLa9Nk79GyoQumMU75%2By%2FD7UHYItHiWr%2BxfIZb9VlLcaLdlUIdC8y7xGnCUvDdNchBqNqvrE68nMUS%2BrRbQ83su8bF8iKF%2F&X-Amz-Signature=b4e765a42f0c063afea1d9777eaa5b8220b706337f86af1c1bdab063116a5579&X-Amz-SignedHeaders=host%3Bx-amz-acl&x-amz-acl=public-read")
                //.url(new URL(presignedUrl))
                //.url(presignedUrl)
                .url("https:" + url_arr[1])
                .method("PUT", body)
//                .addHeader("x-amz-meta-userid", "12")
//                .addHeader("x-amz-meta-practiceid", "13")
                .addHeader("Content-Type", "video/mp4")
                .build();

        new Thread(() -> {
            okhttp3.Response response = null;
            try {
                response = client.newCall(request).execute();
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (response.isSuccessful()){
                Log.d("PUT", ">>>response.body()="+response.body());

                askAnalysis(practice_id, practice, mid);  // 분석 요청하기
            }
            else {
                System.out.println("@@@@ response is not successful...");
                System.out.println("@@@@ response code : " + response.code());  //404 403
                System.out.println("@@@@ response : " + response);
            }
        }).start();


    }

    private void uploadVideo() {
        System.out.println("uploadVideo 시작");

        //Uri videoUri = Uri.fromFile(new File(filename));
        File file = new File(filename);

        System.out.println("file:" + file);  // 임시, 확인용

        RetrofitClient2 retrofitClient = RetrofitClient2.getInstance();

        if (retrofitClient!=null){
            retrofitAPI = RetrofitClient2.getRetrofitAPI();
            retrofitAPI.uploadVideo(presignedUrl, file).enqueue(new Callback<StatusCallback>() {
                @Override
                public void onResponse(Call<StatusCallback> call, Response<StatusCallback> response) {
                    Log.d("PUT", "not success yet");
                    if (response.isSuccessful()){
                        Log.d("PUT", "POST Success!");
                        Log.d("PUT", ">>>response.body()="+response.body());
                    }
                    else {
                        System.out.println("@@@@ response is not successful...");
                        System.out.println("@@@@ response code : " + response.code());  //404
                    }
                }

                @Override
                public void onFailure(Call<StatusCallback> call, Throwable t) {
                    Log.d("PUT", "PUT Failed");
                    Log.d("PUT", t.getMessage());
                }
            });
        }
    }

    private void updatePracticeIdOnDB(int mid, Long practice_id) {

        dbHelper = new DBHelper(this, 4);
        db = dbHelper.getWritableDatabase();    // 읽기/쓰기 모드로 데이터베이스를 오픈

        db.execSQL("UPDATE tableName SET practice_id = '" + practice_id + "' WHERE mid = " + mid);
        
        System.out.println("practice_id: "+practice_id.intValue() + "mid: "+mid);  // 임시, 확인용

        printDBPracticeId();
    }

    public void printDBPracticeId() {
        dbHelper = new DBHelper(context, 4);
        db = dbHelper.getWritableDatabase();    // 읽기/쓰기 모드로 데이터베이스를 오픈

        Cursor cursor = db.rawQuery(" SELECT * FROM tableName ", null);
        startManagingCursor(cursor);    // 엑티비티의 생명주기와 커서의 생명주기를 같게 한다.

        String tableString = "";

        if (cursor.moveToFirst() ){
            do {
                tableString += "mid: " + cursor.getInt(0) + ", practice_id: " + cursor.getInt(11) + ", finished: " + cursor.getInt(9);
                tableString += "\n";

            } while (cursor.moveToNext());
        }
        System.out.println("@@@ print table :\n" + tableString);
    }

    public int getLastMid() {
        dbHelper = new DBHelper(context, 4);
        db = dbHelper.getWritableDatabase();    // 읽기/쓰기 모드로 데이터베이스를 오픈

        Cursor cursor = db.rawQuery(" SELECT * FROM tableName ", null);
        startManagingCursor(cursor);    // 엑티비티의 생명주기와 커서의 생명주기를 같게 한다.

        int last_mid = -1;

        if (cursor.moveToFirst() ){
            do {
                if(cursor.getInt(0) != 0) {
                    last_mid = cursor.getInt(0);
                } else {
                    return last_mid;
                }
            } while (cursor.moveToNext());
        }
        return last_mid;
    }

    private void askAnalysis(Long practice_id, PracticesData practice, int mid) {
        System.out.println("분석 요청 시작");

        // Gender - W, M 으로 변경
        String gender = "X";
        if (practice.getGender().equals("WOMEN")) {
            gender = "W";
        } else if (practice.getGender().equals("MEN")) {
            gender = "M";
        }


        // 임시, 확인용
        System.out.println("userId : " + userId);
        System.out.println("practice_id : " + practice_id);
        System.out.println("gender : " + gender);
        System.out.println("practice.getMoveSensitivity() : " + practice.getMoveSensitivity());
        System.out.println("practice.getEyesSensitivity() : " + practice.getEyesSensitivity());


        RetrofitClient3 retrofitClient = RetrofitClient3.getInstance();

        if (retrofitClient!=null) {
            retrofitAPI = RetrofitClient3.getRetrofitAPI();
            retrofitAPI.askAnalysis(userId.intValue(), practice_id.intValue(), gender, practice.getMoveSensitivity(), practice.getEyesSensitivity()).enqueue(new Callback<StatusCallback>() {
                @Override
                public void onResponse(Call<StatusCallback> call, Response<StatusCallback> response) {
                    Log.d("GET", "not success yet");
                    if (response.isSuccessful()) {
                        Log.d("GET", "GET Success!");
                        Log.d("GET", ">>>response.body()=" + response.body());

                        // 내장 DB에 finished = 1 로 바꾸기
                        updateFinishedOnDB(mid);
                    } else {
                        System.out.println("@@@@ response is not successful...");
                        System.out.println("@@@@ response code : " + response.code());  //500
                    }
                }

                @Override
                public void onFailure(Call<StatusCallback> call, Throwable t) {
                    Log.d("GET", "GET Failed");
                    Log.d("GET", t.getMessage());
                }
            });
        }
    }

    private void updateFinishedOnDB(int mid) {
        dbHelper = new DBHelper(this, 4);
        db = dbHelper.getWritableDatabase();    // 읽기/쓰기 모드로 데이터베이스를 오픈

        db.execSQL("UPDATE tableName SET finished = 1 WHERE mid = " + mid);

        printDBPracticeId();
    }
}