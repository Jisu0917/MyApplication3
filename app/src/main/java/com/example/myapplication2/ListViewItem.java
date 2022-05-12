package com.example.myapplication2;

import android.graphics.drawable.Drawable;

public class ListViewItem {
    private int bgColor;
    private Drawable box;
    private Drawable icon;
    private String text;

    public void setBgColor(int bgColor) {
        this.bgColor = bgColor;
    }

    public void setBox(Drawable box) {
        this.box = box;
    }
    public void setIcon(Drawable icon) {
        this.icon = icon;
    }
    public void setText(String text) {
        this.text = text;
    }

    public int getBgColor() {
        return this.bgColor;
    }

    public Drawable getBox() {
        return this.box;
    }
    public Drawable getIcon() {
        return this.icon;
    }

    public String getText() {
        return this.text;
    }
}
