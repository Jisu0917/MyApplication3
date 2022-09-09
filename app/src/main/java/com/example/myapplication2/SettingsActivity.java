package com.example.myapplication2;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

//import static com.example.myapplication2.MainActivity.tabWidget;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;


public class SettingsActivity extends AppCompatActivity {
    TextView point_text;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        String name = getIntent().getStringExtra("name");
        String email = getIntent().getStringExtra("email");
        String picture = getIntent().getStringExtra("picture");
        //String point = getIntent().getStringExtra("point");

        TextView name_text = (TextView) findViewById(R.id.user_name);
        TextView email_text = (TextView) findViewById(R.id.user_email);
        point_text = (TextView) findViewById(R.id.user_point);
        ImageView picture_image = (ImageView) findViewById(R.id.user_picture);

        if (name!=null) name_text.setText("name: "+name);
        if (email!=null) email_text.setText("email: "+email);
        point_text.setText("point: " + ((UserPoint)getApplication()).getUserPoint());
        if (picture!=null) Glide.with(this).load(picture).into(picture_image);
    }

    @Override
    protected void onResume() {
        super.onResume();

        point_text.setText("point: " + ((UserPoint)getApplication()).getUserPoint());
    }
}
