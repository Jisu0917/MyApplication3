package com.example.myapplication2;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SubMenu;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import jxl.Cell;
import jxl.Sheet;
import jxl.Workbook;
import jxl.read.biff.BiffException;

public class LineChart extends AppCompatActivity {
    static String fileName = "";
    static Map dataMap = new HashMap();
    static boolean xDash = false, yDash = false, showValue = true;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(new DrawLineChart(this));
        Intent it = getIntent();
        fileName = it.getStringExtra("filename");
        setTitle(fileName.substring(0, fileName.length() - 4));  // .xls 자르기

        Excel();
    }

    @Override
    protected void onStop() {
        super.onStop();
        dataMap.clear();
        xDash = false;
        yDash = false;
        showValue = true;
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        SubMenu sMenu_dash = menu.addSubMenu("점선 보기");
        sMenu_dash.add(0, 1, 0, "세로 점선만 보기");
        sMenu_dash.add(0, 2, 0, "가로 점선만 보기");
        sMenu_dash.add(0, 3, 0, "가로, 세로 모두 보기");
        sMenu_dash.add(0, 4, 0, "점선 숨기기");
        SubMenu sMenu_value = menu.addSubMenu("수치 표시하기");
        sMenu_value.add(0, 5, 0, "모든 수치 표시하기");
        sMenu_value.add(0, 6, 0, "최댓값만 표시하기");
        return super.onCreateOptionsMenu(menu);
    }


    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case 1:
                xDash = true;
                yDash = false;
                return true;
            case 2:
                xDash = false;
                yDash = true;
                return true;
            case 3:
                xDash = true;
                yDash = true;
                return true;
            case 4:
                xDash = false;
                yDash = false;
                return true;
            case 5:
                showValue = true;
                return true;
            case 6:
                showValue = false;
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void Excel() {
        Workbook workbook = null;
        Sheet sheet = null;
        try {
            // 파일 불러오기
            InputStream inputStream = getBaseContext().getResources().getAssets().open(fileName);
            workbook = Workbook.getWorkbook(inputStream);
            sheet = workbook.getSheet(0);  // 시트 선택

            int rows = sheet.getRows();
            int cols = sheet.getColumns();

            ArrayList<String> category = new ArrayList<>();  // 서울, 인천, 수원, ...
            ArrayList<String> item = new ArrayList<>();  // 1월, 2월, 3월, ...
            ArrayList<ArrayList<Double>> valueSet = new ArrayList<>();  // 월별 강수량 리스트 모음

            for (int i = 0; i < cols; i++) {
                ArrayList<Double> value = new ArrayList<>();  // 각 월별 강수량
                for (int j = 0; j < rows; j++) {
                    Cell ce = sheet.getCell(i, j);
                    if (i == 0 && j == 0) dataMap.put("VALUname", ce.getContents());  // 강수량(mm)
                    else if (i == 0) {
                        item.add(ce.getContents());  // 1월, 2월, 3월, ...
                    }
                    else if (j == 0)
                    {
                        category.add(ce.getContents());  // 서울, 인천, 수원, ...
                    }
                    else {
                        value.add(Double.valueOf(ce.getContents()));  // 강수량 값
                    }
                }
                if (i > 0) valueSet.add(value);
            }
            dataMap.put("CATEGORY", category);
            dataMap.put("ITEM", item);
            dataMap.put("VALUEset", valueSet);

            System.out.println(dataMap.get("CATEGORY"));
            System.out.println("---구분선---");
            System.out.println(dataMap.get("ITEM"));
            System.out.println("---구분선---");
            System.out.println(dataMap.get("VALUEset"));

            // 버리는 코드..
            int MaxColumn = 2, RowStart = 0, RowEnd = sheet.getColumn(MaxColumn - 1).length -1, ColumnStart = 0, ColumnEnd = sheet.getRow(1).length - 1;
            for(int row = RowStart; row <= 1; row++) {
                String excelload = sheet.getCell(ColumnStart, row).getContents();
            }
        } catch (IOException e) {
            // TODO Auto-generated chat block
            e.printStackTrace();
        } catch (BiffException e) {
            // TODO Auto-generated chat block
            e.printStackTrace();
        } finally {
            workbook.close();
        }
    }

}
