package com.example.myapplication2;

import static com.example.myapplication2.MainActivity.idToken;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class CommunityActivity extends AppCompatActivity {
    String userid = "";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        userid = idToken;

        Intent intent = new Intent(CommunityActivity.this, ListActivity.class);
        intent.putExtra("userid", userid);
        startActivity(intent);
    }
}
