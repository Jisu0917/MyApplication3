package com.example.myapplication2;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.TabActivity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Environment;
import android.widget.TabHost;
import android.widget.TabWidget;
import android.widget.Toast;

public class MainActivity extends TabActivity {
    static String EXTERNAL_STORAGE_PATH = "";

    public static Context context_main;
    public static TabHost myTabHost = null;
    TabHost.TabSpec spec;

    static TabWidget tabWidget;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        context_main = this;

        // For Record Activity
        String state = Environment.getExternalStorageState();
        if (!state.equals(Environment.MEDIA_MOUNTED)) {
            Toast.makeText(getApplicationContext(), "외장 메모리가 마운트 되지 않았습니다.", Toast.LENGTH_LONG).show();
        } else {
            EXTERNAL_STORAGE_PATH = Environment.getExternalStorageDirectory().getAbsolutePath();
        }

        checkDangerousPermissions();


        tabWidget = (TabWidget)findViewById(android.R.id.tabs);

        myTabHost = getTabHost();

//        Intent intent1 = new Intent(MainActivity.this, RecordActivity.class);
        Intent intent2 = new Intent(MainActivity.this, CommunityActivity.class);
        Intent intent3 = new Intent(MainActivity.this, HomeActivity.class);
        Intent intent4 = new Intent(MainActivity.this, FriendActivity.class);
        Intent intent5 = new Intent(MainActivity.this, SettingsActivity.class);

        //Tab 추가
//        spec = myTabHost.newTabSpec("Record").setIndicator("RECORD").setContent(intent1);
//        myTabHost.addTab(spec);

        spec = myTabHost.newTabSpec("Community").setIndicator("COMMUNITY").setContent(intent2);
        myTabHost.addTab(spec);

        spec = myTabHost.newTabSpec("Home").setIndicator("HOME").setContent(intent3);
        myTabHost.addTab(spec);

        spec = myTabHost.newTabSpec("Friend").setIndicator("FRIEND").setContent(intent4);
        myTabHost.addTab(spec);

        spec = myTabHost.newTabSpec("Settings").setIndicator("SETTINGS").setContent(intent5);
        myTabHost.addTab(spec);

        myTabHost.setCurrentTab(1);  //메인 Tab 지정


    }  // end of onCreate

//    //-- 권한 요청 --//
//    private void checkDangerousPermissions() {
//        String[] permissions = { Manifest.permission.READ_EXTERNAL_STORAGE,
//                Manifest.permission.WRITE_EXTERNAL_STORAGE,
//                Manifest.permission.CAMERA,
//                Manifest.permission.RECORD_AUDIO };
//
//        for (int i = 0; i < permissions.length; i++) {
//            int permissionCheck = ContextCompat.checkSelfPermission(this, permissions[i]);
//            if (permissionCheck != PackageManager.PERMISSION_GRANTED) {  // 권한이 허용되지 않은 경우
//                // 권한을 재요청 하는 경우 ('다시 묻지 않기' 체크박스가 자동 추가됨)
//                if (ActivityCompat.shouldShowRequestPermissionRationale(this, permissions[i])) {
//                    ActivityCompat.requestPermissions(this, permissions, 1);
//                }
//                // 권한을 처음 요청하는 경우
//                else {
//                    ActivityCompat.requestPermissions(this, permissions, 1);
//                }
//            } else {
//                break;
//            }
//        }
//    }
//
//    // 사용자의 권한 승인/거절에 대한 대응 (요청이 거절되면 grantResults는 null값)
//    @Override
//    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
//        if (requestCode == 1) {
//            for (int i = 0; i < permissions.length; i++) {
//                if (grantResults[i] == PackageManager.PERMISSION_GRANTED) {
//                    Toast.makeText(this, permissions[i] + " 권한이 승인됨.", Toast.LENGTH_LONG).show();
//                } else {
//                    Toast.makeText(this, "권한 요청을 승인해야 사용 가능합니다.", Toast.LENGTH_LONG).show();
//                }
//                return;
//            }
//        }
//    }

    private void checkDangerousPermissions() {
        String[] permissions = {
                Manifest.permission.RECORD_AUDIO,
                Manifest.permission.CAMERA,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.INTERNET,
                Manifest.permission.VIBRATE
        };

        int permissionCheck = PackageManager.PERMISSION_GRANTED;
        for (int i = 0; i < permissions.length; i++) {
            permissionCheck = ContextCompat.checkSelfPermission(this, permissions[i]);
            if (permissionCheck == PackageManager.PERMISSION_DENIED) {
                break;
            }
        }

        if (permissionCheck == PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(this, "권한 있음", Toast.LENGTH_SHORT).show();
        } else {
            ActivityCompat.requestPermissions(this, permissions, 1);
        }
    }

}