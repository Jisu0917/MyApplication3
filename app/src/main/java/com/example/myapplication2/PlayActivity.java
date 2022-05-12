package com.example.myapplication2;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.media.AudioManager;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import static com.example.myapplication2.RecordActivity1.RECORDED_DIR;

public class PlayActivity extends AppCompatActivity implements MediaController.MediaPlayerControl {
    public static final String TAG = "PlayActivity";
    private static String fileUrl = "";
    int practice_index = 0;

    MediaMetadataRetriever retriever;
    MediaPlayer player;
    SurfaceHolder holder;
    MediaController mediaController;
    Handler handler = new Handler();

    SurfaceView surfaceView;
    ImageView lockIcon;
    ImageView unlockIcon;
    //ImageView captureIcon;
    ImageView volIcon;
    ImageView lightIcon;
    TextView volText;
    TextView lightText;

    // 밝기 조절
    private WindowManager.LayoutParams params;
    private float brightness; // 밝기값은 float

    // 볼륨 조절
    AudioManager audioManager;
    int volume;  // 기존 볼륨
    int maxVol;  // 최대 볼륨

    static boolean screenLock = false;

    static char ctrl = 'x';
    static int light = 13;
    static int vol = 0;
    static float pX0 = 0;
    static float pX1 = 0;
    static float pX = 0;
    static float pY0 = 0;
    static int gap = 0;
    static int delay = 0;


    DBHelper dbHelper;
    SQLiteDatabase db = null;
    Cursor cursor;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);  // 상단바 없애기
        setContentView(R.layout.activity_play);

        final Intent it = getIntent();
        practice_index = it.getIntExtra("PRACTICE_INDEX", 0);

        dbHelper = new DBHelper(this, 4);
        db = dbHelper.getReadableDatabase();    // 읽기/쓰기 모드로 데이터베이스를 오픈
        cursor = db.rawQuery(" SELECT * FROM tableName ", null);
        startManagingCursor(cursor);    // 엑티비티의 생명주기와 커서의 생명주기를 같게 한다.
        cursor.moveToPosition(practice_index);
        fileUrl = cursor.getString(10);


        // 화면 정보 불러오기
        params = getWindow().getAttributes();
        light = 13;

        // 볼륨
        audioManager = (AudioManager)getSystemService(Context.AUDIO_SERVICE);

        surfaceView = (SurfaceView)findViewById(R.id.videoPlayLayout);
        volIcon = (ImageView)findViewById(R.id.volIcon);
        volText = (TextView)findViewById(R.id.volText);
        lightIcon = (ImageView)findViewById(R.id.lightIcon);
        lightText = (TextView)findViewById(R.id.lightText);
        volIcon.setVisibility(View.GONE);
        volText.setVisibility(View.GONE);
        lightIcon.setVisibility(View.GONE);
        lightText.setVisibility(View.GONE);
        lockIcon = (ImageView)findViewById(R.id.lockIcon);
        unlockIcon = (ImageView)findViewById(R.id.unlockIcon);
        //captureIcon = (ImageView) findViewById(R.id.captureIcon);
        lockIcon.setVisibility(View.GONE);
        unlockIcon.setVisibility(View.GONE);
        //captureIcon.setVisibility(View.GONE);
        screenLock = false;

        if (isMyRecFile()) {
            this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);  // 화면 세로 고정
        } else {
            // 화면 가로/세로 고정
            retriever = new MediaMetadataRetriever();
            retriever.setDataSource(fileUrl);

            int width = Integer.valueOf(retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_WIDTH));
            int height = Integer.valueOf(retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_HEIGHT));
            retriever.release();
            if (width > height) {
                this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);  // 화면 가로 고정
            }
            else {
                this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);  // 화면 세로 고정
            }
        }

        createHolder();


        unlockIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                screenLock = true;
                lockIcon.setVisibility(View.VISIBLE);
                unlockIcon.setVisibility(View.GONE);
                hideAll();
            }
        });

        lockIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                screenLock = false;
                lockIcon.setVisibility(View.GONE);
            }
        });

        surfaceView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (!screenLock) {
                    int w = v.getWidth();
                    float x = event.getX();
                    float y = event.getY();

                    if (event.getAction() == MotionEvent.ACTION_DOWN) {
                        if (unlockIcon.getVisibility() == View.VISIBLE) {
                            hideAll();
                        } else {
                            showAll();
                        }
                        /*
                        if (mediaController.isShowing()) {
                            hideAll();
                        } else {
                            showAll();
                        }*/

                        pX0 = x;
                        pY0 = y;
                        lightIcon.setVisibility(View.GONE);
                        lightText.setVisibility(View.GONE);
                        volIcon.setVisibility(View.GONE);
                        volText.setVisibility(View.GONE);
                    }  // end of DOWN

                    else if (event.getAction() == MotionEvent.ACTION_MOVE) {
                        delay += 1;
                        if (delay > 3) {
                            hideAll();
                            if (pX0 < w*0.5 && pX1 < w*0.5 && x < w*0.5) {
                                // 밝기 조절
                                ctrl = 'l';
                                int gap1 = (int) (pY0 - y);
                                int light1 = light + gap1/100;
                                if (light1 > 15) {
                                    light1 = 15;
                                } else if (light1 < 0) {
                                    light1 = 0;
                                }
                                if (delay > 5) {
                                    lightText.setText("" + light1);
                                    lightText.setVisibility(View.VISIBLE);
                                    lightIcon.setVisibility(View.VISIBLE);
                                    volText.setVisibility(View.GONE);
                                    volIcon.setVisibility(View.GONE);
                                }
                            }
                            else if (pX0 > w*0.5 && pX1 > w*0.5 && x > w*0.5) {
                                // 볼륨 조절
                                ctrl = 'v';
                                int gap1 = (int) (pY0 - y);
                                int vol1 = vol + gap1/100;
                                if (vol1 > 15) {
                                    vol1 = 15;
                                } else if (vol1 < 0) {
                                    vol1 = 0;
                                }
                                if (delay > 5) {
                                    volText.setText("" + vol1);
                                    volText.setVisibility(View.VISIBLE);
                                    volIcon.setVisibility(View.VISIBLE);
                                    lightText.setVisibility(View.GONE);
                                    lightIcon.setVisibility(View.GONE);
                                }
                            }
                        }
                        pX1 = x;
                    } // end of MOVE

                    else if (event.getAction() == MotionEvent.ACTION_UP) {
                        delay = 0;
                        lightIcon.setVisibility(View.GONE);
                        lightText.setVisibility(View.GONE);
                        volIcon.setVisibility(View.GONE);
                        volText.setVisibility(View.GONE);
                        if (x < w*0.5 && ctrl == 'l') {
                            // 밝기 조절
                            gap = (int) (pY0 - y);
                            light += gap/100;
                            if (light >= 15) {
                                light = 15;
                                Toast.makeText(PlayActivity.this, "밝기 최대", Toast.LENGTH_SHORT).show();
                            } else if (light <= 0) {
                                light = 0;
                                Toast.makeText(PlayActivity.this, "밝기 최소", Toast.LENGTH_SHORT).show();
                            }
                            params.screenBrightness = 1f * light/15;
                            getWindow().setAttributes(params);
                        }
                        else if (x > w*0.5 && ctrl == 'v') {
                            // 볼륨 조절
                            gap = (int) (pY0 - y);
                            vol += gap/100;
                            if (vol >= 15) {
                                vol = 15;
                                Toast.makeText(PlayActivity.this, "볼륨 최대", Toast.LENGTH_SHORT).show();
                            } else if (vol <= 0) {
                                vol = 0;
                                Toast.makeText(PlayActivity.this, "음소거", Toast.LENGTH_SHORT).show();
                            }
                            audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, (int) (maxVol * vol/15), 0);
                        }
                        ctrl = 'x';
                        pX1 = (float) (w*0.5);
                        pX = 0;
                    } // end of UP
                }
                return true;
            }
        });
    } // end of onCreate()

    private boolean isMyRecFile() {
        return fileUrl.contains("/" + RECORDED_DIR + "/");
    }

    public static Bitmap RotateBitmap(Bitmap source, float angle)
    {
        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        return Bitmap.createBitmap(source, 0, 0, source.getWidth(), source.getHeight(), matrix, true);
    }

    private void hideAll() {
        if (mediaController.isShowing()) {
            mediaController.hide();
        }
//        if (captureIcon.getVisibility() == View.VISIBLE) {
//            captureIcon.setVisibility(View.GONE);
//        }
        if (unlockIcon.getVisibility() == View.VISIBLE) {
            unlockIcon.setVisibility(View.GONE);
        }
    }

    private void showAll() {
        if (!mediaController.isShowing()) {
            mediaController.show(0);
        }
//        if (captureIcon.getVisibility() != View.VISIBLE) {
//            captureIcon.setVisibility(View.VISIBLE);
//        }
        if (unlockIcon.getVisibility() != View.VISIBLE) {
            unlockIcon.setVisibility(View.VISIBLE);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();

        screenLock = false;
        lockIcon.setVisibility(View.GONE);

        // 기존 밝기 저장
        brightness = params.screenBrightness;
        // 기본 밝기로 설정
        params.screenBrightness = 1f * light/15;
        getWindow().setAttributes(params);

        // 기존 볼륨 저장
        volume = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
        // 최대 볼륨
        maxVol = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        vol = 15 *volume/maxVol;

        createHolder();
    }

    @Override
    protected void onResume() {
        super.onResume();

        screenLock = false;
        lockIcon.setVisibility(View.GONE);

        // 기존 밝기 저장
        brightness = params.screenBrightness;
        // 기본 밝기로 설정
        params.screenBrightness = 1f * light/15;
        getWindow().setAttributes(params);

        // 기존 볼륨 저장
        volume = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
        // 최대 볼륨
        maxVol = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        vol = 15 *volume/maxVol;

        createHolder();
    }

    @Override
    protected void onPause() {
        if (player != null) {
            player.release();
            player = null;
        }
        if (mediaController != null) {
            mediaController.hide();
            mediaController = null;
        }

        // 기존 밝기로 변경
        params.screenBrightness = brightness;
        getWindow().setAttributes(params);

        // 기존 볼륨으로 변경
        audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, (int) volume, AudioManager.FLAG_PLAY_SOUND);

        overridePendingTransition(R.anim.fadein, R.anim.fadeout);
        super.onPause();
    }

    public void createHolder() {
        holder = surfaceView.getHolder();
        holder.setSizeFromLayout();
        holder.addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(SurfaceHolder holder) {
                playVideo();
                player.setDisplay(holder);
            }

            @Override
            public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

            }

            @Override
            public void surfaceDestroyed(SurfaceHolder holder) {

            }
        });
    }

    private void playVideo() {
        onPause();

        if (player == null) {
            player =  new MediaPlayer();
        }

        try {
            player.setDataSource(fileUrl);
            player.setDisplay(holder);
            mediaController = new MediaController(PlayActivity.this);
            player.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mp) {
                    Log.d(TAG, "onPrepared called");
                    player.start();
                    mediaController.setMediaPlayer(PlayActivity.this);
                    mediaController.setAnchorView(findViewById(R.id.videoPlayLayout));
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            mediaController.setEnabled(true);
                            mediaController.show(0);
                        }
                    });
                }
            });
            player.prepare();
            player.setScreenOnWhilePlaying(true);

            int videoWidth = player.getVideoWidth();
            int videoHeight = player.getVideoHeight();
            int screenWidth = getWindowManager().getDefaultDisplay().getWidth();
            //int screenHeight = getWindowManager().getDefaultDisplay().getHeight();
            int screenHeight = getResources().getDisplayMetrics().heightPixels;
            float scaledHeight = ((float) videoHeight / (float) videoWidth) * (float) screenWidth;
            float scaledWidth = ((float) videoWidth / (float) videoHeight) * (float) screenHeight;

            if (this.getRequestedOrientation() == ActivityInfo.SCREEN_ORIENTATION_PORTRAIT) {
                android.view.ViewGroup.LayoutParams lp = surfaceView.getLayoutParams();
                if (scaledHeight > screenHeight) {
                    lp.height = screenHeight;
                    lp.width = (int) scaledWidth;
                } else {
                    lp.width = screenWidth;
                    lp.height = (int) scaledHeight;
                }
                surfaceView.setLayoutParams(lp);
            } else if (this.getRequestedOrientation() == ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE) {
                android.view.ViewGroup.LayoutParams lp = surfaceView.getLayoutParams();
                if (scaledWidth > screenWidth) {
                    lp.width = screenWidth;
                    lp.height = (int) scaledHeight;
                } else {
                    lp.height = screenHeight;
                    lp.width = (int) scaledWidth;
                }
                surfaceView.setLayoutParams(lp);
            }

            player.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {

                }
            });

        } catch (Exception e) {
            Log.e(TAG, "Video play failed.", e);
        }
    }

    @Override
    public void start() {
        hideAll();
        player.start();
    }

    @Override
    public void pause() {
        player.pause();
    }

    @Override
    public int getDuration() {
        try {
            return player.getDuration();
        } catch (Exception e) {
            return 0;
        }
    }

    @Override
    public int getCurrentPosition() {
        try {
            return player.getCurrentPosition();
        } catch (Exception e) {
            return 0;
        }
    }

    @Override
    public void seekTo(int pos) {
        try {
            player.seekTo(pos);
        } catch (Exception e) {
            return;
        }

    }

    @Override
    public boolean isPlaying() {
        try {
            return player.isPlaying();
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public int getBufferPercentage() {
        return 0;
    }

    @Override
    public boolean canPause() {
        return true;
    }

    @Override
    public boolean canSeekBackward() {
        return true;
    }

    @Override
    public boolean canSeekForward() {
        return true;
    }

    @Override
    public int getAudioSessionId() {
        return 0;
    }

}
