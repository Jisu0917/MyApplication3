package com.example.myapplication2;


import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class GraphActivity extends AppCompatActivity {

        static boolean useTable = false;
        final static String Sample01 = "전체 항목 총점.xls";
        final static String Sample02 = "전체 항목 총점.xls";
        final static String Sample03 = "전체 항목 구간별 점수.xls";
        final static String Sample04 = "산점도 그래프 예시.xls";

//        final static String pieBarSample01 = "이번 달 지출 내역.xls";
//        final static String pieBarSample02 = "2018년 2분기 전세계 스마트폰 판매량.xls";
//        final static String pieBarSample03 = "2017년 신간 발행 종수.xls";
//        final static String pieBarSample04 = "무안시 초등학교 신입생 수.xls";
//        final static String lineSample01 = "6년간 신간 발행 종수.xls";
//        final static String lineSample02 = "로드샵 빅3 영업실적.xls";
//        final static String lineSample03 = "팀별 판촉비 예실 분석.xls";
//        final static String lineSample04 = "2018 지역별 강수량.xls";
//        final static String scatterSample01 = "성별 나이 체중 조사.xls";
//        final static String scatterSample02 = "2018학년도 학생부교과전형 결과.xls";
//        final static String scatterSample03 = "칼립테루스 암수컷 길이와 무게.xls";
//        final static String scatterSample04 = "꽃받침 길이와 넓이.xls";

        @Override
        protected void onCreate(@Nullable Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_graph);
            setTitle("분석 결과 그래프");

            Button btn_sample1 = (Button) findViewById(R.id.btn_sample1);
            Button btn_sample2 = (Button) findViewById(R.id.btn_sample2);
            Button btn_sample3 = (Button) findViewById(R.id.btn_sample3);
            Button btn_sample4 = (Button) findViewById(R.id.btn_sample4);

            TextView txt_sample1 = (TextView)findViewById(R.id.txt_sample1);
            TextView txt_sample2 = (TextView)findViewById(R.id.txt_sample2);
            TextView txt_sample3 = (TextView)findViewById(R.id.txt_sample3);
            TextView txt_sample4 = (TextView)findViewById(R.id.txt_sample4);

            txt_sample1.setText(Sample01);  // 엑셀 파일명
            txt_sample2.setText(Sample02);
            txt_sample3.setText(Sample03);
            txt_sample4.setText(Sample04);

        }

        public void displaySample(View view) {
            int id = view.getId();
            Button button = (Button)findViewById(id);
            //LinearLayout layout = (LinearLayout)findViewById(id);
            String tag = (String)button.getTag();

            Intent it = getIntent();
//            String chartType = it.getStringExtra("chartType");
            String filename = "";
            Intent intent = null;

            if (tag.equals("01")) {
                filename = Sample01;
                intent = new Intent(GraphActivity.this, PieChart.class);
            }
            else if (tag.equals("02")) {
                filename = Sample02;
                intent = new Intent(GraphActivity.this, BarChart.class);
            }
            else if (tag.equals("03")) {
                filename = Sample03;
                intent = new Intent(GraphActivity.this, LineChart.class);
            }
            else if (tag.equals("04")) {
                filename = Sample04;
                intent = new Intent(GraphActivity.this, ScatterPlot.class);
            }

            intent.putExtra("filename", filename);
            startActivity(intent);
        }

//        @Override
//        public boolean onCreateOptionsMenu(Menu menu) {
//            MenuInflater inflater = getMenuInflater();
//            inflater.inflate(R.menu.menu_option, menu);
//
//            return true;
//        }
//
//        @Override
//        public boolean onPrepareOptionsMenu(Menu menu) {
//            return super.onPrepareOptionsMenu(menu);
//        }
//
//        @Override
//        public boolean onOptionsItemSelected(MenuItem item) {
//            if(item.getItemId() == R.id.menu1)
//            {
//                useTable = true;
//                Intent it = getIntent();
//                String chartType = it.getStringExtra("chartType");
//                Intent intent = null;
//                if (chartType.equals("PIE") || chartType.equals("BAR")) {
//                    intent = new Intent(SelectSample.this, TableForPieBar.class);
//                    intent.putExtra("chartType", chartType);
//                } else if (chartType.equals("LINE")) {
//                    intent = new Intent(SelectSample.this, TableForLine.class);
//                }
//                else if (chartType.equals("SCATTER")) {
//                    intent = new Intent(SelectSample.this, TableForScatter.class);
//                }
//
//                startActivity(intent);
//            }
//            return super.onOptionsItemSelected(item);
//        }

        public void showExcelFile(View view) {
//            int id = view.getId();
//            TextView textView = (TextView)findViewById(id);
//            String tag = (String) textView.getTag();
//            String file = (String) textView.getText();
//            int excelId = 0;
//            if (file.equals(pieBarSample01)) {
//                excelId = R.drawable.piebar_sample_img01;
//            } else if (file.equals(pieBarSample02)) {
//                excelId = R.drawable.piebar_sample_img02;
//            } else if (file.equals(pieBarSample03)) {
//                excelId = R.drawable.piebar_sample_img03;
//            } else if (file.equals(pieBarSample04)) {
//                excelId = R.drawable.piebar_sample_img04;
//            } else if (file.equals(lineSample01)) {
//                excelId = R.drawable.line_sample_img01;
//            } else if (file.equals(lineSample02)) {
//                excelId = R.drawable.line_sample_img02;
//            } else if (file.equals(lineSample03)) {
//                excelId = R.drawable.line_sample_img03;
//            } else if (file.equals(lineSample04)) {
//                excelId = R.drawable.line_sample_img04;
//            } else if (file.equals(scatterSample01)) {
//                excelId = R.drawable.scatter_sample_img01;
//            } else if (file.equals(scatterSample02)) {
//                excelId = R.drawable.scatter_sample_img02;
//            } else if (file.equals(scatterSample03)) {
//                excelId = R.drawable.scatter_sample_img03;
//            } else if (file.equals(scatterSample04)) {
//                excelId = R.drawable.scatter_sample_img04;
//            }
//
//            View dialogView = (View) View.inflate(
//                    this, R.layout.dialog, null);
//            AlertDialog.Builder dig = new AlertDialog.Builder(this);
//            ImageView ivPoster = (ImageView) dialogView.findViewById(R.id.imageViewForPoster);
//            ivPoster.setImageResource(excelId);
//            dig.setTitle(file);
//            dig.setIcon(R.drawable.table_icon);
//            dig.setView(dialogView);
//            dig.setNegativeButton("닫기", null);
//            dig.show();
        }
    }
