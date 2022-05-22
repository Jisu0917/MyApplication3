package com.example.myapplication2;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.view.MotionEvent;
import android.view.View;

import java.util.ArrayList;
import java.util.Locale;
import java.util.Map;

public class DrawLineChart extends View {
    static Map map;
    static boolean dashX = false, dashY = false, valueShow = true;
    static int n = 0, m = 0;  // 카테고리 개수
    static float max = 0, min = 0;

    public DrawLineChart(Context context) { super(context); }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        this.invalidate();
        return true;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawColor(Color.WHITE);

        map = LineChart.dataMap;
        dashX = LineChart.xDash;
        dashY = LineChart.yDash;
        valueShow = LineChart.showValue;
//        // 데이터 셋팅
//        if (map.size() == 0) {
//            map = LineChartByTable.dataMap;
//            dashX = LineChartByTable.xDash;
//            dashY = LineChartByTable.yDash;
//            valueShow = LineChartByTable.showValue;
//        }
        String valueName = (String) map.get("VALUname");  // 강수량(mm)
        ArrayList<String> cate = (ArrayList<String>) map.get("CATEGORY");  // 서울, 인천, 수원, ...
        ArrayList<String> item = (ArrayList<String>) map.get("ITEM");  // 1월, 2월, 3월, ...
        ArrayList<ArrayList<Double>> origValueSet = (ArrayList<ArrayList<Double>>) map.get("VALUEset");  // 각 월별 강수량 리스트 모음
        n = cate.size();
        m = (origValueSet.get(0)).size();
        ArrayList<ArrayList<Float>> valueSet = new ArrayList<>();
        for (int i = 0; i < n; i++) {
            ArrayList<Float> value = new ArrayList<>();
            for (int j = 0; j < m; j++) {
                double d = (origValueSet.get(i)).get(j);
                float f = (float)d;
                value.add(f);
            }
            valueSet.add(value);
        }
        getMaxMin(valueSet);

        final int top = 150;
        final int bottom = getHeight() -550;
        final int margin = 120;
        final int dotDistance = (getWidth() - 2*margin) / m;  // 간격 개수 m - 1, 시작끝 좌우여백
        final int firstDotX = margin + dotDistance/2;

        Paint dotPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        Paint pathPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        pathPaint.setStyle(Paint.Style.STROKE);
        pathPaint.setStrokeWidth(5f);

        Typeface typeface = Typeface.createFromAsset(getResources().getAssets(), "nanum_gothic.TTF");

        float txtSize1 = 0;  // 꼭짓점 위 value 값 텍스트 크기
        if (n > 5) { txtSize1 = 18f; }
        else { txtSize1 = 20f; }
        Paint txtPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        txtPaint.setTypeface(typeface);
        txtPaint.setTextSize(txtSize1);
        Paint maxTxtPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        maxTxtPaint.setTypeface(typeface);
        maxTxtPaint.setTextSize(txtSize1 + 2f);
        maxTxtPaint.setColor(0xfff55b5b);

        float txtSize2 = 0;  // x축 아래 항목 이름 텍스트 크기
        double scale = 0;
        if (m > 5) { txtSize2 = 25f; scale = 0.0944; }
        else { txtSize2 = 30f; scale = 0.044; }

        Paint linePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        DashPathEffect dashPath = new DashPathEffect(new float[]{5,10}, 2);
        linePaint.setStyle( Paint.Style.STROKE );
        linePaint.setPathEffect(dashPath);
        linePaint.setStrokeWidth(3);
        ArrayList<Float> value;
        for (int i = 0; i < n; i++) {
            value = valueSet.get(i);
            dotPaint.setColor(selectColor(i, 'o'));
            linePaint.setColor(selectColor(i, 't'));
            Path p = new Path();
            p.moveTo(firstDotX, bottom - (bottom - top -20) * value.get(0)/max);
            for (int j = 0; j < m; j++) {
                // 꼭짓점 그리기
                canvas.drawCircle(firstDotX + dotDistance * j, bottom - (bottom - top -20) * value.get(j) / max, 5, dotPaint);

                // 점선 그리기 (optional)
                if (dashX) {
                    canvas.drawLine(firstDotX + dotDistance * j, bottom - (bottom - top -20) * value.get(j) / max, firstDotX + dotDistance * j, bottom, linePaint);  // 세로 점선 그리기 (optional)

                }
                if (dashY) {
                    canvas.drawLine(margin, bottom - (bottom - top -20) * value.get(j) / max, firstDotX + dotDistance * j, bottom - (bottom - top -20) * value.get(j) / max, linePaint);  // 가로 점선 그리기 (optional)

                }

                // "이전 꼭짓점"과 연결해주는 선 그리기
                if (j > 0) {
                    p.lineTo(firstDotX + dotDistance * (j-1), bottom - (bottom - top -20) * value.get(j-1) / max);
                }
            }
            p.lineTo(firstDotX + dotDistance * (m-1), bottom - (bottom - top -20) * value.get(m-1) / max);
            pathPaint.setColor(selectColor(i, 'o'));
            canvas.drawPath(p, pathPaint);
        }

        // 꼭짓점 위에 값 써넣기
        int txtDistanceY = 11;
        for (int i = 0; i < n; i++) {
            value = valueSet.get(i);
            txtPaint.setColor(selectColor(i, 'd'));
            for (int j = 0; j < m; j++) {
                float v = value.get(j);
                String txt = "";
                if (v == (int) v) txt = "" + (int) v;
                else txt = "" + v;

                if (value.get(j) == max) {
                    canvas.drawText(txt, firstDotX + dotDistance * j - fTxtAdjust((int)v, txtSize1), bottom - txtDistanceY - (bottom - top -20) * value.get(j) / max, maxTxtPaint);
                }
                else if (valueShow) { canvas.drawText(txt, firstDotX + dotDistance * j - fTxtAdjust(v, txtSize1), bottom - txtDistanceY - (bottom - top -20) * value.get(j) / max, txtPaint); }
            }
        }

        // x축, y축 그리기
        Paint linePaint2 = new Paint(Paint.ANTI_ALIAS_FLAG);
        linePaint2.setColor(0xff636363);
        canvas.drawLine(margin, bottom, getWidth() -margin, bottom, linePaint2);  // x축
        canvas.drawLine(margin, 80, margin, bottom, linePaint2);  // y축

        // y축 눈금값 그리기
        txtPaint.setColor(0xff7d7d7d);
        txtPaint.setTextSize(20f);
        // 0 그리기
        canvas.drawText("0", margin - 25, bottom + 25, txtPaint);
        int interval = 0;
        if (max < 10) {
            for (int i = 1; i <= (int)max +1; i++) {
                if ((bottom - (bottom - top - 20) * i / max) > 80)
                    canvas.drawText("" + i, margin - 30 - iTxtAdjust(i, 20f), bottom - (bottom - top -20) * i / max, txtPaint);
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
                if (i < 1000 && (bottom - (bottom - top - 20) * i / max) > 80)
                    canvas.drawText("" + i, margin - 30 - iTxtAdjust(i, 20f), (bottom - (bottom - top - 20) * i / max) + 20f / 4, txtPaint);  // y값 고정, + 20f/4 는 텍스트 위치 미세조정
                else if ((bottom - (bottom - top - 20) * i / max) > 80) {
                        canvas.drawText("" + getNumShort(i), margin - 30 - fTxtAdjust(getNumShort(i), 20f), (bottom - (bottom - top -20) * i / max) + 20f/4, txtPaint);  // y값 고정, + 20f/4 는 텍스트 위치 미세조정
                        txtPaint.setTextSize(15f);
                        canvas.drawText("(만)", margin - 30 - sTxtAdjust("(만)", 15f), 20 + (bottom - (bottom - top -20) * i / max) + 20f/4, txtPaint);  // y값 고정, + 15f/4 는 텍스트 위치 미세조정
                        txtPaint.setTextSize(20f);
                }
            }
        }

        // y축 이름 표시하기
        txtPaint.setTextSize(25f);
        canvas.translate(0, getHeight());
        canvas.rotate(270);
        canvas.drawText(valueName, getHeight() - (float) (top + (bottom - top)*0.5) - sTxtAdjust(valueName, 25f), 50, txtPaint);  // y축 이름
        canvas.rotate(90);
        canvas.translate(0, -getHeight());

        // 색상표시 상자 & 카테고리명 그리기
        txtPaint.setTextSize(25f);
        RectF rect = new RectF();
        float txt_y = 0, left = 0, boxMargin = getWidth()/9;
        Paint boxPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        for (int i = 0; i < n; i++) {
            if (i < 4) {
                txt_y = getHeight() - 330;
                left = boxMargin + 220 * i;
            } else if (i < 8) {
                txt_y = getHeight() - 210;
                left = boxMargin + 220 * (i - 4);
            } else {
                txt_y = getHeight() - 90;
                left = boxMargin + 220 * (i - 8);
            }
            rect.set(left, txt_y - 20, left + 40, txt_y + 20);
            boxPaint.setColor(selectColor(i, 'o'));
            canvas.drawRect(rect, boxPaint);
            if (cate.get(i).length() > 7) { canvas.drawText(cate.get(i).substring(0, 7) + "...", left + 55, txt_y + 12, txtPaint); }
            else { canvas.drawText(cate.get(i), left + 55, txt_y + 12, txtPaint); }
        }

        // 항목 이름 그리기
        canvas.translate(getWidth(), 0);
        canvas.rotate(90);
        txtPaint.setTextSize(txtSize2);
        for (int j = 0; j < m; j++) {
            if (item.get(j).length() > 7) { canvas.drawText(item.get(j).substring(0, 7) + "...", bottom + 17, firstDotX + dotDistance * (m - j) - (float)(dotDistance * scale * m), txtPaint); }
            else { canvas.drawText(item.get(j), bottom + 17, getWidth() - (firstDotX + dotDistance * j) + sTxtAdjustY(txtSize2), txtPaint); }
        }


    }

    private int selectColor(int i, char option) {
        ArrayList<Integer> colors = new ArrayList<>();
        colors.add(0xffc7ffde);  // 0
        colors.add(0xffffd1f5);  // 1
        colors.add(0xffddc2ff);  // 2
        colors.add(0xff8ff7ed);  // 3
        colors.add(0xffffb0b0);  // 4
        colors.add(0xffedff8c);  // 5
        colors.add(0xffcaf0fc);  // 6
        colors.add(0xffbcfa2a);  // 7
        colors.add(0xffbbb8fc);  // 8
        colors.add(0xffffcf8c);  // 9
        colors.add(0xffffc4d4);  // 10

        switch (option) {
            case 'o': return colors.get(i % 11);  // 불투명 (opacity)
            case 't': return colors.get(i % 11) - 0x9f000000;  // 투명 (transparent)
            case 'd': return colors.get(i % 11) - 0x202020;  // 진하게 (dark)
            default: return 0x00000000;
        }
    }

    private void getMaxMin(ArrayList<ArrayList<Float>> valueSet) {
        ArrayList<Float> value = valueSet.get(0);
        max = value.get(0);
        min = value.get(0);
        for (int i = 0; i < n; i++) {
            value = valueSet.get(i);
            for (int j = 0; j < m; j++) {
                if (value.get(j) > max) { max = value.get(j); }
                if (value.get(j) < min) { min = value.get(j); }
            }
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
    private float sTxtAdjustY(float txtSize) {
        return (float) (txtSize*0.32);
    }
}
