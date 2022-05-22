package com.example.myapplication2;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.view.MotionEvent;
import android.view.View;

import java.util.ArrayList;
import java.util.Locale;
import java.util.Map;


public class DrawBarChart extends View {
    static Map map;
    static boolean dashY = false;
    static int n = 0;  // 카테고리 개수
    static float max = 0, min = 0;

    public DrawBarChart(Context context) { super(context); }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        this.invalidate();
        return true;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawColor(Color.WHITE);

        map = BarChart.dataMap;
        dashY = BarChart.yDash;
//        // 데이터 셋팅
//        if (map.size() == 0){
//            map = BarChartByTable.dataMap;
//            dashY = BarChartByTable.yDash;
//        }
        String cateName = (String) map.get("CATEname");
        String valueName = (String) map.get("VALUname");
        ArrayList<String> cate = (ArrayList<String>) map.get("CATEGORY");
        ArrayList<Double> origValue = (ArrayList<Double>) map.get("VALUE");
        n = cate.size();
        ArrayList<Float> value = new ArrayList<>();
        for (int i = 0; i < n; i++) {
            double d = origValue.get(i);
            float f = (float)d;
            value.add(f);
        }

        final int top = 300;
        final int bottom = getHeight() -400;
        final int margin = 140;
        final int barWidth = (getWidth() - 2*margin) / (2*n + 1);
        final int firstBarLeft = margin + 30*9/n;

        Paint barPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        barPaint.setStyle(Paint.Style.FILL);
        RectF rect1 = new RectF();

        // 최대값, 최소값 구하기
        max = value.get(0);
        min = value.get(0);
        for (int i=1; i < n; i++) {
            if (value.get(i) > max) max = value.get(i);
            if (value.get(i) < min) min = value.get(i);
        }

        Typeface typeface = Typeface.createFromAsset(getResources().getAssets(), "nanum_gothic.TTF");

        float txtSize1 = 0;  // 막대 위 value 값 텍스트 크기
        if (n > 5) { txtSize1 = 20f; }
        else { txtSize1 = 28f; }
        Paint txtPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        txtPaint.setTextSize(txtSize1);
        txtPaint.setColor(0xff7d7d7d);
        txtPaint.setTypeface(typeface);
        Paint maxTxtPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        maxTxtPaint.setTextSize(txtSize1);
        maxTxtPaint.setColor(0xfff55b5b);
        maxTxtPaint.setTypeface(typeface);

        float txtSize2 = 0;  // x축 아래 카테고리명 텍스트 크기
        double scale = 0;
        if (n > 5) { txtSize2 = 30f; scale = 0.0944; }
        else { txtSize2 = 40f; scale = 0.044; }

        Paint linePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        DashPathEffect dashPath = new DashPathEffect(new float[]{5,10}, 2);
        linePaint.setStyle( Paint.Style.STROKE );
        linePaint.setPathEffect(dashPath);
        linePaint.setStrokeWidth(3);
        float barLength = 0;
        if (cate.size() == value.size() && value.size() == n) {
            for (int i = 0; i < n; i++) {
                float v = value.get(i);
                // 막대 그리기
                barLength = (bottom - top) * v / max;  // 막대의 최대 길이 = (bottom - top) * 100%
                barPaint.setColor(selectColor(i, true));
                rect1.set(firstBarLeft + 2 * barWidth * i, bottom - barLength, firstBarLeft + 2 * barWidth * i + barWidth, bottom);
                canvas.drawRect(rect1, barPaint);

                // 점선 그리기 (optional)
                if (dashY) {
                    linePaint.setColor(selectColor(i, false));
                    canvas.drawLine(margin, bottom - barLength, firstBarLeft + 2 * barWidth * i, bottom - barLength, linePaint);  // 가로 점선 그리기 (optional)

                }

                // 막대 위에 value 값 써넣기
                String txt = "";
                if (v >= 10000) {
                    txt = "" + getNumShort(v) + "(만)";
                }
                else {
                    if (v == (int) v) txt = "" + (int) v;
                    else txt = "" + Float.parseFloat(String.format(Locale.US,"%.2f", (float)v));
                }
                if (v == max) { canvas.drawText(txt, firstBarLeft + 2 * barWidth * i + (float)(barWidth*0.5) - fTxtAdjust(v, txtSize1), bottom - barLength -20, maxTxtPaint); }
                else { canvas.drawText(txt, firstBarLeft + 2 * barWidth * i + (float)(barWidth*0.5) - fTxtAdjust(v, txtSize1), bottom - barLength -20, txtPaint); }
            }

            // x축, y축 그리기
            Paint linePaint2 = new Paint(Paint.ANTI_ALIAS_FLAG);
            linePaint2.setColor(0xff636363);
            canvas.drawLine(margin, bottom, getWidth() -margin, bottom, linePaint2);  // x축
            canvas.drawLine(margin, 100, margin, bottom, linePaint2);  // y축

            // y축 눈금값 그리기
            txtPaint.setTextSize(20f);
            // 0 그리기
            canvas.drawText("0", margin - 25, bottom + 25, txtPaint);
            int interval = 0;
            if (max < 10) {
                for (int i = 1; i <= (int)max +1; i++) {
                    if ((bottom - barLength) > 100)
                        canvas.drawText("" + i, margin - 30 - iTxtAdjust(i, 20f), bottom - barLength, txtPaint);
                }
            }
            else {
                if (max < 50) interval = 5;
                else if (max < 100) interval = 10;
                else if (max < 200) interval = 20;
                else if (max < 500) interval = 50;
                else if (max < 1000) interval = 100;
                else if (max < 2000) interval = 200;
                else if (max < 5000) interval = 500;
                else { interval = numDown(max, 2)/2; } // ex) 32017 -> interval: 5000

                for (int i = interval; i <= max + interval; i+=interval) {
                    barLength = (bottom - top) * i / max;
                    if (i < 1000 && (bottom - barLength) > 100) {
                        canvas.drawText("" + i, margin - 30 - iTxtAdjust(i, 20f), bottom - barLength + 20f / 4, txtPaint);  // y값 고정, + 20f/4 는 텍스트 위치 미세조정
                    } else if ((bottom - barLength) > 100) {
                            canvas.drawText("" + getNumShort(i), margin - 30 - fTxtAdjust(getNumShort(i), 20f), bottom - barLength + 20f/4, txtPaint);  // y값 고정, + 20f/4 는 텍스트 위치 미세조정
                            txtPaint.setTextSize(15f);
                            canvas.drawText("(만)", margin - 30 -sTxtAdjust("(만)", 15f), 20+ bottom - barLength + 20f/4, txtPaint);  // y값 고정, + 15f/4 는 텍스트 위치 미세조정
                            txtPaint.setTextSize(20f);
                    }
                }
            }

            // y축 카테고리 표시하기
            txtPaint.setTextSize(25f);
            canvas.translate(0, getHeight());
            canvas.rotate(270);
            canvas.drawText(valueName, getHeight() - (float) (top + (bottom - top)*0.5) - sTxtAdjust(valueName, 25f), 50, txtPaint);  // y축 카테고리
            canvas.rotate(90);
            canvas.translate(0, -getHeight());

            // 카테고리명 그리기
            canvas.translate(getWidth(), 0);
            canvas.rotate(90);
            txtPaint.setTextSize(txtSize2);
            for (int i = 0; i < n; i++) {
                canvas.drawText(cate.get(i), bottom + 17 , getWidth() - (firstBarLeft + 2 * barWidth * i + (float)(barWidth*0.5) - sTxtAdjust("가", txtSize2)), txtPaint);
            }
        }
        else canvas.drawText("데이터를 불러오는 데 문제가 발생했습니다.", 250, 500, txtPaint);
    }

    private int selectColor(int i, boolean opacity) {
        ArrayList<Integer> colors = new ArrayList<>();
        colors.add(0xffffb0b0);  // 0
        colors.add(0xfff1ffa8);  // 1
        colors.add(0xffcaf0fc);  // 2
        colors.add(0xffc7ffde);  // 3
        colors.add(0xffffe8fa);  // 4
        colors.add(0xffe7d4ff);  // 5
        colors.add(0xffbdfcf5);  // 6
        colors.add(0xfffeffb5);  // 7
        colors.add(0xffc2bfff);  // 8
        colors.add(0xffffe5b8);  // 9
        colors.add(0xffffd1dd);  // 10
        if (opacity) return colors.get(i % 11);  // 불투명 (opacity)
        else {
            return colors.get(i % 11) - 0x7f000000;  // 투명
        }
    }

    private float getNumShort(float num) {
        float numShort = Float.parseFloat(String.format(Locale.US,"%.2f", (float)(num/10000)));
        return numShort;
    }

    private int numDown(float num, int option) {
        int tmp0 = (int)(Math.log10(num));  // ex) 314 -> 2  ,  73402 -> 4
        int tmp1 = (int)(Math.pow(10, tmp0));  // ex) 314 -> 100  ,  73402 -> 10000
        int tmp2 = (int) (num/tmp1);  // ex) 314 -> 3  ,  73402 -> 7  // 맨 앞의 숫자
        switch (option) {
            case 1: return tmp0;
            case 2: return tmp1;
            case 3: return tmp2;
            default: return 0;
        }
        // int tmp3 = (int)(Math.pow(10, tmp0 -1));  // ex) 314 -> 10
    }

    // 텍스트 위치 조정
    private float fTxtAdjust(float f, float txtSize) {
        int len = (""+f).length();
        return (float) (len*0.21*txtSize);
    }
    private float iTxtAdjust(int i, float txtSize) {
        int len = (""+i).length();
        return (float) (len*0.25*txtSize);
    }
    private float sTxtAdjust(String s, float txtSize) {
        int len = s.length();
        return (float) (len*0.25*txtSize);
    }

}
