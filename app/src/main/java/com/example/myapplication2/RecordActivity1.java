package com.example.myapplication2;

import static com.example.myapplication2.MainActivity.updatePoint;

import android.Manifest;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Point;
import android.hardware.Camera;
import android.media.CamcorderProfile;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Display;
import android.view.SurfaceHolder;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.Date;

import static com.example.myapplication2.MainActivity.EXTERNAL_STORAGE_PATH;

import com.example.myapplication2.api.RetrofitAPI;
import com.example.myapplication2.api.RetrofitClient;
import com.example.myapplication2.api.RetrofitClient2;
import com.example.myapplication2.api.RetrofitClient3;
import com.example.myapplication2.api.dto.PracticesData;
import com.google.android.gms.common.api.internal.StatusCallback;
import com.google.gson.JsonObject;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RecordActivity1 extends AppCompatActivity {
    final int NO_INPUT = Integer.MAX_VALUE;
    public static final String TAG = "RecordActivity1";
    public static String RECORDED_DIR = "myRec";
    static String filename = "";

    ImageView recStartBtn, recStopBtn, analysisBtn;

    File f;

    MediaRecorder recorder;

    private static CameraPreview surfaceView;
    private SurfaceHolder holder;
    private static Camera mCamera;
    private int RESULT_PERMISSIONS = 100;
    public static RecordActivity1 getInstance;

    LinearLayout guide;

    public static Context context;

    Intent it;
//    Long practice_id;
    String title, scope, sort, gender;
    int sensitivity;


    static Long userId = MainActivity.userId;

    static RetrofitAPI retrofitAPI;

    static String presignedUrl = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        it = getIntent();
        title = it.getStringExtra("title");
        scope = it.getStringExtra("scope");
        sort = it.getStringExtra("sort");
        gender = it.getStringExtra("gender");
        sensitivity = it.getIntExtra("sensitivity", 0);


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


        guide = (LinearLayout) findViewById(R.id.guide);
        guide.setVisibility(View.VISIBLE);

        recStartBtn = (ImageView) findViewById(R.id.recStartBtn);
        recStopBtn = (ImageView) findViewById(R.id.recStopBtn);
//        analysisBtn = (ImageView) findViewById(R.id.imageview_analysis_start);
//        analysisBtn.setVisibility(View.GONE);

        f = new File(EXTERNAL_STORAGE_PATH + "/" + RECORDED_DIR);
        System.out.println(f);

        recStartBtn.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.S)
            @Override
            public void onClick(View v) {

                startRecording();
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

                recStopBtn.setVisibility(View.INVISIBLE);
                recStartBtn.setVisibility(View.INVISIBLE);

                //다이얼로그 띄우기 - 재촬영 or 분석하기
                AlertDialog.Builder builder = new AlertDialog.Builder(RecordActivity1.this);
                builder.setTitle("이 영상을 분석할까요?");
                builder.setMessage("재촬영을 원하시면 재촬영 버튼을 누르세요.");
                builder.setPositiveButton("확인",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                // 내장메모리에 영상 저장
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

                                // 서버에 영상 업로드 + 분석 시작
                                postNewPractice();

                                finish();
                            }
                        });
                builder.setNegativeButton("재촬영",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                guide.setVisibility(View.VISIBLE);
                                recStartBtn.setVisibility(View.VISIBLE);
                                recStopBtn.setVisibility(View.INVISIBLE);
                            }
                        });
                builder.show();

//                analysisBtn.setVisibility(View.VISIBLE);
//                recStopBtn.setVisibility(View.INVISIBLE);
//                recStartBtn.setVisibility(View.VISIBLE);

            }
        });

//        analysisBtn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                // 서버에 영상 업로드 + 분석 시작
//                postNewPractice();
//
//                finish();
//            }
//        });
    }

    @Override
    protected void onResume() {
        super.onResume();

        guide.setVisibility(View.VISIBLE);
    }

    private void startRecording() {
        guide.setVisibility(View.GONE);

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

    public static Camera getCamera(){
        return mCamera;
    }
    private void setInit(){
        getInstance = this;

        // 카메라 객체를 R.layout.activity_main의 레이아웃에 선언한 SurfaceView에서 먼저 정의해야 함으로 setContentView 보다 먼저 정의한다.
        mCamera = Camera.open(findFrontSideCamera());

        setContentView(R.layout.activity_record);

        // 가이드 이미지 크기 조정
        /// 화면 가로 길이 구하기
        Display display = getWindowManager().getDefaultDisplay();  // in Activity
        /* getActivity().getWindowManager().getDefaultDisplay() */ // in Fragment
        Point size = new Point();
        display.getRealSize(size); // or getSize(size)
        int width = size.x;
        int height = size.y;
        /// 이미지뷰 width, height 조절
        ImageView iv_guideline = (ImageView) findViewById(R.id.imageview_guideline);
        iv_guideline.requestLayout();
        iv_guideline.getLayoutParams().height = (int) (width * 0.5);  // 비율 : 화면 가로 너비의 50%
        iv_guideline.getLayoutParams().width = (int) (width * 0.5);  // 비율 : 화면 가로 너비의 50%

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

                ActivityCompat.requestPermissions(RecordActivity1.this,
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


    private void postNewPractice() {
        System.out.println("서버에 업로드 시작");

        PracticesData practice = new PracticesData();
        practice.setUserId(userId);
        practice.setTitle(title);
        practice.setScope(scope);
        practice.setSort(sort);
        practice.setGender(gender);
        practice.setMoveSensitivity(sensitivity);
        practice.setEyesSensitivity(sensitivity);

        System.out.println("practice.getUserId(): " + practice.getUserId());  //임시, 확인용
        System.out.println("practice.getTitle(): " + practice.getTitle());  //임시, 확인용
        System.out.println("practice.getScope(): " + practice.getScope());  //임시, 확인용
        System.out.println("practice.getSort(): " + practice.getSort());  //임시, 확인용
        System.out.println("practice.getGender(): " + practice.getGender());  //임시, 확인용
        System.out.println("practice.getMoveSensitivity(): " + practice.getMoveSensitivity());  //임시, 확인용
        System.out.println("practice.getEyesSensitivity(): " + practice.getEyesSensitivity());  //임시, 확인용


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

                        practice.setId(practice_id);

//                        // 내장 DB에 practice_id 업데이트
//                        db.execSQL("UPDATE practiceTable SET practice_id = " + practice_id.intValue() + " WHERE time = '" + recordTime + "'");

                        getPresignedURL(practice);  // 영상 업로드 할 url 받아오기
                    }
                    else {
                        System.out.println("@@@@ response is not successful...");
                        System.out.println("@@@@ response code : " + response.code());  //400
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

    private void getPresignedURL(PracticesData practice) {

        System.out.println("getPresignedURL 시작");

        RetrofitClient2 retrofitClient = RetrofitClient2.getInstance();

        System.out.println("userId: " + userId + ", practice.getId(): " + practice.getId());  //임시, 확인용

        Integer[] ids = {Math.toIntExact(userId), Math.toIntExact(practice.getId())};

        if (retrofitClient!=null){
            retrofitAPI = RetrofitClient2.getRetrofitAPI();
            retrofitAPI.getPresignedURL(ids, userId, practice.getId()).enqueue(new Callback<JsonObject>() {
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
                            uploadVideoOkhttp(practice);
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

    private void uploadVideoOkhttp(PracticesData practice) throws MalformedURLException {
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

        System.out.println("presignedUrl : " + presignedUrl);  // 임시, 확인용

        String url_arr[] = presignedUrl.split(":");
        url_arr[1] = url_arr[1].replace("\"", "");

        Request request = new Request.Builder()
                .url("https:" + url_arr[1])
                .method("PUT", body)
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

                askAnalysis(practice);  // 분석 요청하기
            }
            else {
                System.out.println("@@@@ response is not successful...");
                System.out.println("@@@@ response code : " + response.code());  //404 403
                System.out.println("@@@@ response : " + response);
            }
        }).start();


    }

    private void askAnalysis(PracticesData practice) {
        System.out.println("분석 요청 시작");

        Handler handler = new Handler(Looper.getMainLooper());
        handler.postDelayed(new Runnable() {
            @Override
            public void run()
            {
                Toast.makeText(getApplicationContext(), practice.getTitle() + "을(를) 분석 중입니다.", Toast.LENGTH_SHORT).show();
            }
        }, 0);

        // 임시, 확인용
        System.out.println("userId : " + userId);
        System.out.println("practice_id : " + practice.getId());
        System.out.println("gender : " + practice.getGender());
        System.out.println("practice.getMoveSensitivity() : " + practice.getMoveSensitivity());
        System.out.println("practice.getEyesSensitivity() : " + practice.getEyesSensitivity());

        String gen;
        if (practice.getGender().equals("WOMEN"))
            gen = "W";
        else
            gen = "M";

        RetrofitClient3 retrofitClient = RetrofitClient3.getInstance();

        if (retrofitClient!=null) {
            retrofitAPI = RetrofitClient3.getRetrofitAPI();
            retrofitAPI.askAnalysis(userId.intValue(), practice.getId().intValue(), gen, practice.getMoveSensitivity(), practice.getEyesSensitivity()).enqueue(new Callback<StatusCallback>() {
                @Override
                public void onResponse(Call<StatusCallback> call, Response<StatusCallback> response) {
                    Log.d("GET", "not success yet");
                    if (response.isSuccessful()) {
                        Log.d("GET", "GET Success!");
                        Log.d("GET", ">>>response.body()=" + response.body());

                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run()
                            {
                                Toast.makeText(getApplicationContext(), practice.getTitle() + "의 분석이 완료되었습니다.", Toast.LENGTH_SHORT).show();
                            }
                        }, 0);

                        // 분석(서버에 영상 업로드) 완료 후 내장메모리에서 영상 삭제하기
                        File file = new File(filename);
                        file.delete();

                        // 포인트 -10 차감
                        updatePoint(10, "minus", RecordActivity1.this);

                        ((HomeActivity1)HomeActivity1.context).getUserInfo();
                        System.out.println("HomeActivity1의 리스트 업데이트!");  //임시, 확인용

                    } else {
                        System.out.println("@@@@ response is not successful...");
                        System.out.println("@@@@ response code : " + response.code());  //500

                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run()
                            {
                                Toast.makeText(getApplicationContext(), practice.getTitle() + "의 분석에 실패하였습니다.", Toast.LENGTH_SHORT).show();
                            }
                        }, 0);
                    }
                }

                @Override
                public void onFailure(Call<StatusCallback> call, Throwable t) {
                    Log.d("GET", "GET Failed");
                    Log.d("GET", t.getMessage());

                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run()
                        {
                            Toast.makeText(getApplicationContext(), practice.getTitle() + "의 분석에 실패하였습니다.", Toast.LENGTH_SHORT).show();
                            Toast.makeText(getApplicationContext(), t.getMessage(), Toast.LENGTH_LONG).show();

                        }
                    }, 0);
                }
            });
        }
    }  // end of askAnalysis
}