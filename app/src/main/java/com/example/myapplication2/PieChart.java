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
import java.util.List;
import java.util.Map;

import jxl.Cell;
import jxl.Sheet;
import jxl.Workbook;
import jxl.read.biff.BiffException;

public class PieChart extends AppCompatActivity {
    static String fileName = "";
    static Map dataMap = new HashMap();
    static boolean scaled = false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(new DrawPieChart(this));
        Intent it = getIntent();
        fileName = it.getStringExtra("filename");
        setTitle(fileName.substring(0, fileName.length() - 4));  // .xls 자르기

        Excel();
    }

    @Override
    protected void onStop() {
        super.onStop();
        dataMap.clear();
        scaled = false;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        SubMenu sMenu_dash = menu.addSubMenu("파이 스타일");
        sMenu_dash.add(0, 1, 0, "비율에 맞게 채우기");
        sMenu_dash.add(0, 2, 0, "꽉 차게 보기");
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
                scaled = true;
                return true;
            case 2:
                scaled = false;
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

            // 임시, 확인용
            System.out.println("######## workbook ########");
            System.out.println(workbook);
            System.out.println("######## sheet ########");
            System.out.println(sheet);

            int rows = sheet.getRows();
            int cols = sheet.getColumns();

            // 임시, 확인용
            System.out.println("######## rows ########");
            System.out.println(rows);
            System.out.println("######## cols ########");
            System.out.println(cols);

            ArrayList<String> category = new ArrayList<>();
            ArrayList<Double> value = new ArrayList<>();

            for (int i = 0; i < cols; i++) {  // 열 (세로)
                // j = 0에 해당하는 카테고리명은 원그래프에서는 사용하지 않을 것임.
                for (int j = 1; j < rows; j++) {  // 행 (가로)
                    // 위에서 읽은 Sheet 안에 각각의 셀 객체를 생성
                    // i와 j의 값이 바뀌면 가로세로 값이 바뀌어 저장됨.
                    Cell ce = sheet.getCell(i, j);

                    // 임시, 확인용
                    System.out.println("######## ce.getContents() ########");
                    System.out.println(ce.getContents());

                    if (i == 0) category.add(ce.getContents());
                    else if (i == 1) value.add(Double.valueOf(ce.getContents()));
                }
                dataMap.put("CATEGORY", category);
                dataMap.put("VALUE", value);
            }

            System.out.println(dataMap.get("CATEGORY"));
            System.out.println("---구분선---");
            System.out.println(dataMap.get("VALUE"));

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
