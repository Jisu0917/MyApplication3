package com.example.myapplication2;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.example.myapplication2.api.RetrofitAPI;
import com.example.myapplication2.api.RetrofitClient;
import com.example.myapplication2.api.dto.PostsData;
import com.example.myapplication2.api.dto.PracticesData;
import com.example.myapplication2.api.objects.UserIdObject;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/*
* 게시물 리스트 자동 새로고침 기능 구현해야 함
* */

public class ListActivity extends AppCompatActivity {

    // 로그에 사용할 TAG 변수
    final private String TAG = getClass().getSimpleName();

    // 사용할 컴포넌트 선언
    ListView listView;
    Button reg_button;
    FloatingActionButton fab_register;

    String userid = "";

    ArrayList<PostsData> postsDataList = new ArrayList<>();

    // 리스트뷰에 사용할 제목 배열
    ArrayList<String> titleList = new ArrayList<>();
    ArrayList<String> contentList = new ArrayList<>();
    ArrayList<Long> postIdList = new ArrayList<>();
    ArrayList<Long> practiceIdList = new ArrayList<>();

    // 클릭했을 때 어떤 게시물을 클릭했는지 게시물 번호를 담기 위한 배열  -- 변수 seq -> practice_id로 변경
//    ArrayList<String> seqList = new ArrayList<>();


    static String personName;
    static String idToken;

    static RetrofitAPI retrofitAPI;
    static UserIdObject userIdObject;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);

// CommunityActivity 에서 넘긴 userid 값 받기
//        userid = getIntent().getStringExtra("userid");
        userid = idToken;

// 컴포넌트 초기화
        listView = findViewById(R.id.listView);

// listView 를 클릭했을 때 이벤트 추가
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

// 어떤 값을 선택했는지 토스트를 뿌려줌
                Toast.makeText(ListActivity.this, adapterView.getItemAtPosition(i)+ " 클릭", Toast.LENGTH_SHORT).show();

// 게시물의 번호와 userid를 가지고 DetailActivity 로 이동
                // 게시물 제목과 내용, practice id를 가지고 DetailActivity로 이동
                Intent intent = new Intent(ListActivity.this, DetailActivity.class);
//                intent.putExtra("board_seq", seqList.get(i));
//                intent.putExtra("userid", userid);

                intent.putExtra("title", titleList.get(i));
                intent.putExtra("content", contentList.get(i));
                intent.putExtra("postId", postIdList.get(i));
                intent.putExtra("practiceId", practiceIdList.get(i));

                startActivity(intent);

            }
        });

        fab_register = findViewById(R.id.fab_register);
        fab_register.show();
        fab_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // userid 를 가지고 RegisterActivity 로 이동
                Intent intent = new Intent(ListActivity.this, RegisterActivity.class);
                intent.putExtra("userid", userid);
                startActivity(intent);
            }
        });

//// 버튼 컴포넌트 초기화
//        reg_button = findViewById(R.id.reg_button);
//
//// 버튼 이벤트 추가
//        reg_button.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//
//// userid 를 가지고 RegisterActivity 로 이동
//                Intent intent = new Intent(ListActivity.this, RegisterActivity.class);
//                intent.putExtra("userid", userid);
//                startActivity(intent);
//            }
//        });


        GetBoard getBoard = new GetBoard();
        getBoard.execute();
    }


    // onResume() 은 해당 액티비티가 화면에 나타날 때 호출됨
    @Override
    protected void onResume() {
        super.onResume();

        // 임시, 확인용
        System.out.println("onResume() 실행");

// 해당 액티비티가 활성화 될 때, 게시물 리스트를 불러오는 함수를 호출
        GetBoard getBoard = new GetBoard();
        getBoard.execute();
    }

//    @Override
//    protected void onRestart() {
//        super.onRestart();
//
//        GetBoard getBoard = new GetBoard();
//        getBoard.execute();
//    }



    // 게시물 리스트를 읽어오는 함수
    class GetBoard extends AsyncTask<String, Void, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            Log.d(TAG, "onPreExecute");
        }


        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            Log.d(TAG, "onPostExecute, " + result);

// 배열들 초기화
            titleList.clear();
            contentList.clear();
            postIdList.clear();
            practiceIdList.clear();
//            seqList.clear();

            if (postsDataList != null) {
                // PostsData 형태로 넘어온 데이터를 title 리스트와 practice_id 리스트로 변환
                for (int i=0; i<postsDataList.size(); i++) {
                    PostsData post = postsDataList.get(i);

                    String title = post.getTitle();
                    String content = post.getContent();
                    Long postId = post.getId();
//                    PracticesData practicesData = post.getPractices();
//                    Long practiceId = practicesData.getId();
                    Long practiceId = post.getPracticeId();

                    titleList.add(title);
                    contentList.add(content);
                    postIdList.add(postId);
                    practiceIdList.add(practiceId);
                }
            } else {
                System.out.println("postsDataList is null...");
            }
//
//// 결과물이 JSONArray 형태로 넘어오기 때문에 파싱
//                JSONArray jsonArray = new JSONArray(result);
//
//                for(int i=0;i<jsonArray.length();i++){
//                    JSONObject jsonObject = jsonArray.getJSONObject(i);
//
//                    String title = jsonObject.optString("title");
//                    //String seq = jsonObject.optString("seq");
//                    String seq = jsonObject.optString("practice_id");
//
//// title, seq 값을 변수로 받아서 배열에 추가
//                    titleList.add(title);
////                    seqList.add(seq);
//
////                    // 임시, 확인용
////                    String tmp_title1 = "게시글 제목1";
////                    String tmp_title2 = "게시글 제목2";
////                    titleList.add(tmp_title1);
////                    titleList.add(tmp_title2);
//
//                }

// ListView 에서 사용할 arrayAdapter를 생성하고, ListView 와 연결
            ArrayAdapter arrayAdapter = new ArrayAdapter<String>(ListActivity.this, android.R.layout.simple_list_item_1, titleList);
            listView.setAdapter(arrayAdapter);

// arrayAdapter의 데이터가 변경되었을때 새로고침
            arrayAdapter.notifyDataSetChanged();


        }


        @Override
        protected String doInBackground(String... params) {

            /////
            getAllPosts();
            /////


//
// String userid = params[0];
// String passwd = params[1];

            //String server_url = "http://15.164.252.136/load_board.php";


            //URL url;
            String response = "";
//            try {
//                url = new URL(server_url);
//
//                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
//                conn.setReadTimeout(15000);
//                conn.setConnectTimeout(15000);
//                conn.setRequestMethod("POST");
//                conn.setDoInput(true);
//                conn.setDoOutput(true);
//                Uri.Builder builder = new Uri.Builder()
//                        .appendQueryParameter("userid", "");
//// .appendQueryParameter("passwd", passwd);
//                String query = builder.build().getEncodedQuery();
//
//                OutputStream os = conn.getOutputStream();
//                BufferedWriter writer = new BufferedWriter(
//                        new OutputStreamWriter(os, "UTF-8"));
//                writer.write(query);
//                writer.flush();
//                writer.close();
//                os.close();
//
//                conn.connect();
//                int responseCode=conn.getResponseCode();
//
//                if (responseCode == HttpsURLConnection.HTTP_OK) {
//                    String line;
//                    BufferedReader br=new BufferedReader(new InputStreamReader(conn.getInputStream()));
//                    while ((line=br.readLine()) != null) {
//                        response+=line;
//                    }
//                }
//                else {
//                    response="";
//
//                }
//            } catch (Exception e) {
//                e.printStackTrace();
//            }

            // 임시, 확인용
//            response = "{\"id\":id1,\n\"password\":pw1}";
//            response = "[{\"id\":\"id1\",\"password\":\"pw1\"}]";
//            response = "[{\"title\":\"title001\",\"seq\":\"1\"},{\"title\":\"title002\",\"seq\":\"2\"}]";
//            response = "[{\"title\":\"title001\",\"practice_id\":\"1\"},{\"title\":\"title002\",\"practice_id\":\"2\"}]";

            return response;
        }
    }

    private void getAllPosts() {

        System.out.println("서버에서 전체 게시물 가져오기 시작");

        RetrofitClient retrofitClient = RetrofitClient.getInstance();

        if (retrofitClient!=null){
            retrofitAPI = RetrofitClient.getRetrofitAPI();
            retrofitAPI.getAllPosts().enqueue(new Callback<ArrayList<PostsData>>() {
                @Override
                public void onResponse(Call<ArrayList<PostsData>> call, Response<ArrayList<PostsData>> response) {
                    if (response.isSuccessful()){
                        postsDataList = response.body();
                        if (postsDataList !=null){
                            Log.d("GET_ALLPOSTS", "GET SUCCESS");
                            Log.d("GET_ALLPOSTS", postsDataList.toString());
                        } else {
                            System.out.println("GET_ALLPOSTS : postsDataList is null...");
                        }
                    }
                    else {
                        System.out.println("@@@@ response is not successful...");
                        System.out.println("@@@@ response code : " + response.code());
                    }
                }

                @Override
                public void onFailure(Call<ArrayList<PostsData>> call, Throwable throwable) {
                    Log.d("GET_ALLPOSTS", "GET FAILED");
                }
            });
        }
    }
}