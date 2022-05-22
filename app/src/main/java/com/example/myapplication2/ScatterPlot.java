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
import java.util.HashSet;
import java.util.Map;
import java.util.TreeSet;

import jxl.Cell;
import jxl.Sheet;
import jxl.Workbook;
import jxl.read.biff.BiffException;

public class ScatterPlot extends AppCompatActivity {
    static String fileName = "";
    static Map dataMap = new HashMap();
    static boolean xDash = false, yDash = false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(new DrawScatterPlot(this));
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
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        SubMenu sMenu_dash = menu.addSubMenu("점선 보기");
        sMenu_dash.add(0, 1, 0, "세로 점선만 보기");
        sMenu_dash.add(0, 2, 0, "가로 점선만 보기");
        sMenu_dash.add(0, 3, 0, "가로, 세로 모두 보기");
        sMenu_dash.add(0, 4, 0, "점선 숨기기");
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem group) {
        switch (group.getItemId()) {
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
        }
        return super.onOptionsItemSelected(group);
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

            dataMap.put("CATEX", (sheet.getCell(1, 0)).getContents());  // x축 카테고리 (나이)
            dataMap.put("CATEY", (sheet.getCell(2, 0)).getContents());  // y축 카테고리 (체중)

            Map groupMap = new HashMap();
            ArrayList<String> group0 = new ArrayList<>();  // 여성, 남성, ...
            ArrayList<ArrayList<Float>> valueSets = new ArrayList<>();  // 성별 체중 리스트 모음

            for (int j = 1; j < rows; j++) {
                Cell cell = sheet.getCell(0, j);
                group0.add(cell.getContents());
            }
            // 그룹 중복 제거
            TreeSet<String> group1 = new TreeSet<String>(group0);
            ArrayList<String> group = new ArrayList<String>(group1);

            for (int i = 0; i < group.size(); i++) {
                groupMap.put(i, group.get(i));  // Key: 0, 1, 2, ...  Value: 여성, 남성, ...
            }

            for (int j = 1; j < rows; j++) {
                Cell g = sheet.getCell(0, j);
                int groupIndex = 0;
                for (int i = 0; i < groupMap.size(); i++) {
                    if (g.getContents() == groupMap.get(i)) {
                        groupIndex = i;
                    }
                }
                float valueX = Float.parseFloat(sheet.getCell(1, j).getContents());
                float valueY = Float.parseFloat(sheet.getCell(2, j).getContents());
                ArrayList<Float> valueSet = new ArrayList<>();
                valueSet.add((float) groupIndex);
                valueSet.add(valueX);
                valueSet.add(valueY);
                valueSets.add(valueSet);
            }

            dataMap.put("GROUP", groupMap);
            dataMap.put("VALUEsets", valueSets);

            System.out.println("x: "+dataMap.get("CATEX")+", y: "+dataMap.get("CATEY"));
            System.out.println("---구분선---");
            System.out.println(dataMap.get("GROUP"));
            System.out.println("---구분선---");
            System.out.println(dataMap.get("VALUEsets"));

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
