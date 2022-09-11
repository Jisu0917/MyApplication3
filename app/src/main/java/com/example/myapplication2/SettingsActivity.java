package com.example.myapplication2;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

//import static com.example.myapplication2.MainActivity.tabWidget;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.myapplication2.api.dto.UserInfoData;

import java.util.ArrayList;


public class SettingsActivity extends AppCompatActivity {
    ImageView iv_profile;
    TextView tv_name, tv_email, tv_id, tv_point, tv_num_practice, tv_num_post, tv_num_feedback, tv_num_friend;
    ArrayList<String> history;
    LinearLayout history_layout;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        setTitle("내 회원 정보");
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        Intent it = getIntent();

        Long id = it.getLongExtra("id", 0L);
        String name = it.getStringExtra("name");
        String email = it.getStringExtra("email");
        String picture = it.getStringExtra("picture");
        //String point = it.getStringExtra("point");
        int num_of_practices = it.getIntExtra("num_of_practices", 0);
        int num_of_posts = it.getIntExtra("num_of_posts", 0);
        int num_of_friends = it.getIntExtra("num_of_friends", 0);
        int num_of_feedbacks = it.getIntExtra("num_of_feedbacks", 0);

        iv_profile = (ImageView) findViewById(R.id.iv_profile);
        tv_name = (TextView) findViewById(R.id.tv_name);
        tv_email = (TextView) findViewById(R.id.tv_email);
        tv_id = (TextView) findViewById(R.id.tv_id);
        tv_point = (TextView) findViewById(R.id.tv_point);
        tv_num_practice = (TextView) findViewById(R.id.tv_num_practice);
        tv_num_post = (TextView) findViewById(R.id.tv_num_post);
        tv_num_feedback = (TextView) findViewById(R.id.tv_num_feedback);
        tv_num_friend = (TextView) findViewById(R.id.tv_num_friend);


        if (picture!=null) Glide.with(this).load(picture).into(iv_profile);
        if (name!=null) tv_name.setText(name);
        if (email!=null) tv_email.setText(email);
        tv_id.setText(""+id);
        tv_point.setText("" + ((UserPoint)getApplication()).getUserPoint());
        tv_num_practice.setText(num_of_practices+"개");
        tv_num_post.setText(num_of_posts+"개");
        tv_num_feedback.setText(num_of_feedbacks+"개");
        tv_num_friend.setText(num_of_friends+"명");
        
        history_layout = (LinearLayout) findViewById(R.id.history_layout);

        setHistoryView();
    }

    @Override
    protected void onResume() {
        super.onResume();

        tv_point.setText("" + ((UserPoint)getApplication()).getUserPoint());

        setHistoryView();
    }

    private void setHistoryView() {
        history = ((UserPoint)getApplication()).getHistory();

        history_layout.removeAllViews();

        LayoutInflater layoutInflater = LayoutInflater.from(SettingsActivity.this);
        //LayoutInflater layoutInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);


        if (history != null) {
            if (history.size() == 0) {  //히스토리 기록이 없을 때
                View customView = layoutInflater.inflate(R.layout.custom_textview, null);

                ((TextView)customView.findViewById(R.id.custom_textView)).setText("포인트 히스토리가 없습니다.");
                history_layout.addView(customView);
            }

            else {  //히스토리 기록이 있을 때
                for (int i=history.size()-1; i>=0; i--) {
                    View customView = layoutInflater.inflate(R.layout.custom_history, null);
                    String s = history.get(i);
                    String s_split[] = s.split("#");
                    String time = s_split[0];
                    String update = s_split[1];
                    String mypoint = s_split[2];

                    ((TextView)customView.findViewById(R.id.tv_update_point)).setText(update);
                    ((TextView)customView.findViewById(R.id.tv_date_time)).setText(time);
                    ((TextView)customView.findViewById(R.id.tv_mypoint)).setText(mypoint);

                    history_layout.addView(customView);
                }
            }

        } else {
            System.out.println("history is null...");
        }
    }
}
