package com.example.myapplication2;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.RadialGradient;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.Typeface;
import android.view.MotionEvent;
import android.view.View;

import java.util.ArrayList;
import java.util.Locale;
import java.util.Map;

import static com.example.myapplication2.ScatterPlot.dataMap;
import static com.example.myapplication2.ScatterPlot.xDash;
import static com.example.myapplication2.ScatterPlot.yDash;

public class DrawScatterPlot extends View {
    static Map map;
    static boolean dashX = false;
    static boolean dashY = false;
    static float xMax = 0, xMin = 0, yMax = 0, yMin = 0;

    public DrawScatterPlot(Context context) {super(context);}

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        this.invalidate();
        return true;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawColor(Color.WHITE);

        map = ScatterPlot.dataMap;
        dashX = ScatterPlot.xDash;
        dashY = ScatterPlot.yDash;
//        // 데이터 셋팅
//        if (map.size() == 0){
//            map = ScatterPlotByTable.dataMap;
//            dashX = ScatterPlotByTable.xDash;
//            dashY = ScatterPlotByTable.yDash;
//        }
        Map groupMap = (Map) map.get("GROUP");
        ArrayList<ArrayList<Float>> valueSets = (ArrayList<ArrayList<Float>>) map.get("VALUEsets");
        final int n = valueSets.size();  // 총 value 세트 개수 = 점의 개수
        getMaxMin(valueSets);


        // 지울 것
        //String[] groups = getGroups();  // 그룹 개수 = groups.length
        //ArrayList<Float[]> dataSets = getDataSets();  // getDataSets(?)[1]은 xValue, [2]는 yValue
        //getMaxMinValue(dataSets);

        final int top = 170;
        final int bottom = getHeight() -570;
        final int margin = 120;
        final int xBottom = getWidth() - margin;

        Typeface typeface = Typeface.createFromAsset(getResources().getAssets(), "nanum_gothic.TTF");
        Paint txtPaint = new Paint(Paint.ANTI_ALIAS_FLAG);  // x, y 각각 최댓값 value 표시, 눈금값 표시
        txtPaint.setTypeface(typeface);

        // 점선 그리기 (optional)
        Paint linePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        DashPathEffect dashPath = new DashPathEffect(new float[]{5,10}, 2);
        linePaint.setStyle( Paint.Style.STROKE );
        linePaint.setPathEffect(dashPath);
        linePaint.setStrokeWidth(3);


        // x축, y축 그리기
        Paint linePaint2 = new Paint(Paint.ANTI_ALIAS_FLAG);
        linePaint2.setColor(0xff636363);
        canvas.drawLine(margin, bottom, xBottom, bottom, linePaint2);  // x축
        canvas.drawLine(margin, 50, margin, bottom, linePaint2);  // y축

        linePaint2.setColor(0xf0e3e3e3);
        // 눈금값 그리기
        txtPaint.setColor(0xff7d7d7d);
        txtPaint.setTextSize(20f);
        // 0 그리기
        canvas.drawText("0", margin - 25, bottom + 25, txtPaint);
        // x축 눈금값 그리기
        int interval = 0;
        if (xMax < 10) {
            for (int i = 1; i <= (int)xMax +1; i++) {
                if ((margin + (xBottom - margin - 70) * i / xMax) < xBottom) {
                    canvas.drawText("" + i, margin + (xBottom - margin -70) * i / xMax, bottom + 40, txtPaint);
                    canvas.drawLine(margin + (xBottom - margin -70) * i / xMax, 50, margin + (xBottom - margin -70) * i / xMax, bottom, linePaint2);
                }
            }
        }
        else {
            if (xMax < 50) interval = 5;
            else if (xMax < 100) interval = 10;
            else if (xMax < 200) interval = 20;
            else if (xMax < 500) interval = 50;
            else if (xMax < 1000) interval = 100;
            else if (xMax < 2000) interval = 200;
            else if (xMax < 5000) interval = 500;
            else { interval = numDown(xMax, 2)/2; }  // ex) 32017 -> interval: 5000

            for (int i = interval; i <= xMax + interval; i+=interval) {
                if (i < 1000 && (margin + (xBottom - margin - 70) * i / xMax) < xBottom) {
                    canvas.drawText("" + i, (margin + (xBottom - margin - 70) * i / xMax) - iTxtAdjust(i, 20f), bottom + 40, txtPaint);  // x값 고정
                    canvas.drawLine((margin + (xBottom - margin - 70) * i / xMax), 50, (margin + (xBottom - margin - 70) * i / xMax), bottom, linePaint2);
                }
                else if ((margin + (xBottom - margin - 70) * i / xMax) < xBottom) {  // x축 바깥으로 넘어가는 눈금값은 그리지 않는다.
                        canvas.drawText("" + getNumShort(i), (margin + (xBottom - margin - 70) * i / xMax) - fTxtAdjust(getNumShort(i), 20f), bottom + 40, txtPaint);  // x값 고정
                        txtPaint.setTextSize(15f);
                        canvas.drawText("(만)", margin + (xBottom - margin - 70) * i / xMax -sTxtAdjust("(만)", 15f), bottom + 60, txtPaint);
                        txtPaint.setTextSize(20f);
                        canvas.drawLine((margin + (xBottom - margin - 70) * i / xMax), 50, (margin + (xBottom - margin - 70) * i / xMax), bottom, linePaint2);
                }
            }
        }
        // y축 눈금값 그리기
        if (yMax < 10) {
            for (int i = 1; i <= (int)yMax +1; i++) {
                if ((bottom - (bottom - top - 70) * i / yMax) > 50) {
                    canvas.drawText("" + i, margin - 30 - iTxtAdjust(i, 20f), bottom - (bottom - top -70) * i / yMax, txtPaint);
                    canvas.drawLine(margin, bottom - (bottom - top -70) * i / yMax, xBottom, bottom - (bottom - top -70) * i / yMax, linePaint2);
                }
            }
        }
        else {
            if (yMax < 50) interval = 5;
            else if (yMax < 100) interval = 10;
            else if (yMax < 200) interval = 20;
            else if (yMax < 500) interval = 50;
            else if (yMax < 1000) interval = 100;
            else if (yMax < 2000) interval = 200;
            else if (yMax < 5000) interval = 500;
            else { interval = numDown(yMax, 2)/2; } // ex) 32017 -> interval: 5000

            for (int i = interval; i <= yMax + interval; i+=interval) {
                if (i < 1000 && (bottom - (bottom - top - 70) * i / yMax) > 50) {
                    canvas.drawText("" + i, margin - 30 - iTxtAdjust(i, 20f), (bottom - (bottom - top - 70) * i / yMax) + 20f / 4, txtPaint);  // y값 고정, + 20f/4 는 텍스트 위치 미세조정
                    canvas.drawLine(margin, bottom - (bottom - top - 70) * i / yMax, xBottom, (bottom - (bottom - top - 70) * i / yMax), linePaint2);
                }
                else if ((bottom - (bottom - top - 70) * i / yMax) > 50 ) {  // y축 바깥으로 넘어가는 눈금값은 그리지 않는다.
                        canvas.drawText("" + getNumShort(i), margin - 30 - fTxtAdjust(getNumShort(i), 20f), (bottom - (bottom - top - 70) * i / yMax) + 20f/4, txtPaint);  // y값 고정, + 20f/4 는 텍스트 위치 미세조정
                        txtPaint.setTextSize(15f);
                        canvas.drawText("(만)", margin - 30 -sTxtAdjust("(만)", 15f), 30+ (bottom - (bottom - top - 70) * i / yMax) + 15f/4, txtPaint);  // y값 고정, + 15f/4 는 텍스트 위치 미세조정
                        txtPaint.setTextSize(20f);
                        canvas.drawLine(margin, bottom - (bottom - top - 70) * i / yMax, xBottom, bottom - (bottom - top - 70) * i / yMax, linePaint2);
                }
            }
        }

        // 점 그리기
        Paint dotPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        txtPaint.setTextSize(19f);
        txtPaint.setColor(0xfff55b5b);
        boolean xMaxFlag = true;  // x 최댓값 표시했으면 false
        boolean yMaxFlag = true;  // y 최댓값 표시했으면 false
        for (int i = 0; i < n; i++) {  // 모든 데이터에 대해 수행
            float groupIndex = (valueSets.get(i)).get(0);
            float valueX = (valueSets.get(i)).get(1);
            float valueY = (valueSets.get(i)).get(2);
            float pointX = margin + (xBottom - margin -70) * valueX / xMax;
            float pointY = bottom - (bottom - top - 70) * valueY / yMax;
            RadialGradient radGrad = new RadialGradient(pointX, pointY, 15, selectColor((int)groupIndex, 'o'), selectColor((int)groupIndex, 't'), Shader.TileMode.MIRROR);
            dotPaint.setShader(radGrad);
            canvas.drawCircle(pointX, pointY, 15, dotPaint);

            linePaint.setColor(selectColor((int) groupIndex, 't'));
            if (dashX) {
                canvas.drawLine(pointX, pointY, pointX, bottom, linePaint);  // X값 점선 그리기 (optional)
            }
            if (dashY) {
                canvas.drawLine(margin, pointY, pointX, pointY, linePaint);  // Y값 점선 그리기 (optional)
            }

            // x, y 각각 최댓값 표시하기
            if (xMaxFlag && valueX == xMax) {
                canvas.drawText("" + xMax, (float) (pointX - fTxtAdjust(xMax, 19f)), bottom -10, txtPaint);
                xMaxFlag = false;
            }
            if (yMaxFlag && valueY == yMax) {
                canvas.translate(getWidth(), 0);
                canvas.rotate(90);
                canvas.drawText("" + yMax, pointY - fTxtAdjust(yMax, 19f), getWidth() - (margin +10), txtPaint);
                canvas.rotate(270);
                canvas.translate(-getWidth(), 0);
                yMaxFlag = false;
            }
        }

        // x, y축 카테고리 표시하기
        txtPaint.setColor(0xff7d7d7d);
        txtPaint.setTextSize(25f);
        canvas.drawText((String) map.get("CATEX"), (float) (margin + (xBottom - margin)*0.5) - sTxtAdjust((String) map.get("CATEX"), 25f), bottom + 110, txtPaint);  // x축 카테고리

        canvas.translate(0, getHeight());
        canvas.rotate(270);
        canvas.drawText((String) map.get("CATEY"), getHeight() - (float) (top + (bottom - top)*0.5) - sTxtAdjust((String) map.get("CATEY"), 25f), 50, txtPaint);  // y축 카테고리
        canvas.rotate(90);
        canvas.translate(0, -getHeight());

        // 색상표시 상자 & 카테고리명 그리기
        txtPaint.setTextSize(26f);
        RectF rect = new RectF();
        float txt_y = 0, left = 0, boxMargin = getWidth()/9;
        Paint boxPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        for (int i = 0; i < groupMap.size(); i++) {
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
            String group = (String) groupMap.get(i);
            if (group.length() > 7) { canvas.drawText(group.substring(0, 7) + "...", left + 55, txt_y + 12, txtPaint); }
            else { canvas.drawText(group, left + 55, txt_y + 12, txtPaint); }
        }

    }

    private void getMaxMin(ArrayList<ArrayList<Float>> valueSets) {
        int m = valueSets.size();
        xMax = valueSets.get(0).get(1);
        xMin = valueSets.get(0).get(1);
        yMax = valueSets.get(0).get(2);
        yMin = valueSets.get(0).get(2);

        for (int i = 1; i < m; i++) {
            if (valueSets.get(i).get(1) > xMax) { xMax = valueSets.get(i).get(1); }
            if (valueSets.get(i).get(1) < xMin) { xMin = valueSets.get(i).get(1); }
            if (valueSets.get(i).get(2) > yMax) { yMax = valueSets.get(i).get(2); }
            if (valueSets.get(i).get(1) < yMin) { yMin = valueSets.get(i).get(2); }
        }
    }

    private int selectColor(int i, char option) {
        ArrayList<Integer> colors = new ArrayList<>();
        colors.add(0xffcaf0fc);  // 0
        colors.add(0xffc7ffde);  // 1
        colors.add(0xffffd1f5);  // 2
        colors.add(0xffffb0b0);  // 3
        colors.add(0xffe8ff6b);  // 4
        colors.add(0xffddc2ff);  // 5
        colors.add(0xff8ff7ed);  // 6
        colors.add(0xffbcfa2a);  // 7
        colors.add(0xffbbb8fc);  // 8
        colors.add(0xffffcf8c);  // 9
        colors.add(0xffffc4d4);  // 10
        switch (option) {
            case 'o': return colors.get(i % 11);  // 불투명 (opacity)
            case 't': return colors.get(i % 11) - 0x7f000000;  // 투명 (transparent)
            case 'd': return colors.get(i % 11) - 0x202020;  // 진하게 (dark)
            default: return 0x00000000;
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
