package com.example.myapplication2;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.view.MotionEvent;
import android.view.View;

import java.util.ArrayList;
import java.util.Locale;
import java.util.Map;


public class DrawPieChart extends View {
    static Map map;
    static boolean scaledPie = false;
    static int n = 0;
    static float max = 0;

    public DrawPieChart(Context context) { super(context); }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        this.invalidate();
        return true;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawColor(Color.WHITE);

        map = PieChart.dataMap;
        scaledPie = PieChart.scaled;
//        // 데이터 셋팅
//        if (map.size() == 0) {
//            map = PieChartByTable.dataMap;
//            scaledPie = PieChartByTable.scaled;
//        }
        ArrayList<String> cate = (ArrayList<String>) map.get("CATEGORY");
        ArrayList<Double> origValue = (ArrayList<Double>) map.get("VALUE");
        n = cate.size();
        double sum = 0;
        for (int i = 0; i < n; i++) {
            assert origValue != null;
            sum += origValue.get(i);
        }
        ArrayList<Float> value = new ArrayList<>();
        for (int i = 0; i < n; i++) {
            double d = (origValue.get(i) / sum) *100;  // 백분율 환산
            float f = (float)d;
            value.add(f);
        }

        // 최대값 구하기
        max = value.get(0);
        for (int i=1; i < n; i++) {
            if (value.get(i) > max) max = value.get(i);
        }

        float per = 0;
        float flag = 0;

        Paint piePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        Paint outLinePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        outLinePaint.setStyle(Paint.Style.STROKE);
        RectF rect1 = new RectF();
        RectF rect01 = new RectF();
        final double radius_d = getHeight()/4.4;
        final float radius = (float)radius_d;  // 반지름 (float)
        final float circleTop = (float) (90 * getHeight()/getWidth());
        rect1.set(getWidth()/2 -radius, circleTop, getWidth()/2 +radius, circleTop +2*radius);  // 정사각형
        final float centerX = getWidth()/2;  // 원의 중심 x좌표
        final float centerY = circleTop + radius;  // 원의 중심 y좌표

        Paint boxPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        RectF rect2 = new RectF();

        Typeface typeface = Typeface.createFromAsset(getResources().getAssets(), "nanum_gothic.TTF");

        Paint txtPaint1 = new Paint(Paint.ANTI_ALIAS_FLAG);
        txtPaint1.setTextSize(25f);
        txtPaint1.setColor(0xff7d7d7d);
        txtPaint1.setTypeface(typeface);

        Paint txtPaint2 = new Paint(Paint.ANTI_ALIAS_FLAG);
        txtPaint2.setTextSize(22f);
        txtPaint2.setTypeface(typeface);

        // 전체 세로 길이에서 상하여백(대략 circleTop*3/2), 원(radius*2), 카테고리 영역((n/3) +1)*(20 + 120))을 제외한 나머지를 원과 카테고리 영역 사이 간격으로 사용
        double circleBoxSpace = getHeight() - (circleTop*3/2 + radius*2 + ((n/3) +1)*(20 + 120));
        double boxTop = circleTop + radius*2 + circleBoxSpace;  // 카테고리 영역의 시작 위치(첫 번째 boxY값)
        float boxY = 0, left = 0, margin = getWidth()/9 -30;
        if (cate.size() == value.size() && value.size() == n) {
            for(int i = 0; i < n; i++) {
                // 파이 그리기
                per = value.get(i);
                piePaint.setColor(selectColor(i, false));
                float start = getStart(flag);
                float angle = getAngle(per);
                if (scaledPie) {
                    double d = Math.sqrt(per * max);
                    float rad = radius * (float)d/max;
                    rect01.set(getWidth()/2 -rad, circleTop + radius - rad, getWidth()/2 +rad, circleTop + radius + rad);
                    canvas.drawArc(rect01, start, angle, true, piePaint);
                    outLinePaint.setColor(selectColor(i, false));
                    canvas.drawArc(rect1, start, angle, true, outLinePaint);
                }
                else {
                    canvas.drawArc(rect1, start, angle, true, piePaint);
                }

                flag += per;

                // 색상표시 상자 & 카테고리명, 원본데이터값 (퍼센트값%) 그리기
                if (i < 3) { boxY = (float) boxTop; left = margin + 310*i;}
                else if (i < 6) { boxY = (float) boxTop + 120; left = margin + 310*(i - 3);}
                else if (i < 9) { boxY = (float) boxTop + 240; left = margin + 310*(i - 6);}
                else { boxY = (float) boxTop + 360; left = margin + 310*(i - 9);}
                rect2.set(left, boxY -20, left +40, boxY +20);
                boxPaint.setColor(selectColor(i, false));
                canvas.drawRect(rect2, boxPaint);
                if ((cate.get(i)).length() > 10) {canvas.drawText((cate.get(i)).substring(0, 10) + "...", left + 55, boxY +8, txtPaint1);}
                else {canvas.drawText(cate.get(i), left + 55, boxY +8, txtPaint1);}
                txtPaint2.setColor(selectColor(i, true));
                double v = origValue.get(i);
                String txt = "";
                if (v >= 10000) {
                    txt = "" + Float.parseFloat(String.format(Locale.US,"%.2f", (float)(v/10000))) + "(만)";
                }
                else {
                    if (v == (int) v) txt = "" + (int) v;
                    else txt = "" + Float.parseFloat(String.format(Locale.US,"%.2f", (float)v));
                }
                canvas.drawText(txt + "  (" + value.get(i) + "%)", left + 55, boxY +47, txtPaint2);
            }

            // 파이 안에 백분율값, 카테고리명 써넣기
            flag = 0;
            for (int i = 0; i < n; i++) {
                per = value.get(i);
                float start = getStart(flag);
                float angle = getAngle(per);
                flag += per;
                double theta = (start + angle * 0.5) * Math.PI/180;
                double x = Math.cos(theta) * 0.7 * radius; // * scale;
                double y = Math.sin(theta) * 0.7 * radius;
                double Px = centerX + x;
                double Py = centerY + y;
                txtPaint2.setColor(selectColor(i, true));
                txtPaint2.setTextSize(27f);
                int len = 0;
                if ((per + "").length() > 4) {
                    len = ((per +"").substring(0, 4) + "%").length();
                    canvas.drawText((per +"").substring(0, 4) + "%", (float)Px - len*5, (float)Py +15, txtPaint2);
                }
                else {
                    len = (per + "%").length();
                    canvas.drawText(per +"%", (float)Px - len*5, (float)Py +15, txtPaint2);
                }
                txtPaint2.setTextSize(25f);
                canvas.drawText(cate.get(i), (float)Px - len*6, (float)Py -15, txtPaint2);
            }
        }
        else canvas.drawText("데이터를 불러오는 데 문제가 발생했습니다.", 250, 500, txtPaint1);

    }

    private int selectColor(int i, boolean dark) {
        ArrayList<Integer> colors = new ArrayList<>();
        colors.add(0xffffb0b0);  // 0
        colors.add(0xffedff8c);  // 1
        colors.add(0xffcaf0fc);  // 2
        colors.add(0xffc7ffde);  // 3
        colors.add(0xffffd1f5);  // 4
        colors.add(0xffddc2ff);  // 5
        colors.add(0xff8ff7ed);  // 6
        colors.add(0xffbcfa2a);  // 7
        colors.add(0xffbbb8fc);  // 8
        colors.add(0xffffcf8c);  // 9
        colors.add(0xffffc4d4);  // 10

        if (dark) return colors.get(i % 11) - 0x404040;
        else return colors.get(i % 11);
    }

    private float getAngle(float per) {
        return 360 * (per/100);
    }

    private float getStart(float flag) {
       return 360 * (flag/100);
    }

}
