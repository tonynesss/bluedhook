package com.zjfgh.bluedhook.simple;

import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
public class Gradient {
    private float radius = 0f;          // 默认无圆角
    private String colorLeft = "#FF000000";  // 默认黑色（带透明度）
    private String colorRight = "#FFFFFFFF"; // 默认白色（带透明度）
    private GradientDrawable.Orientation orientation = GradientDrawable.Orientation.LEFT_RIGHT;

    public Gradient setRadius(float radius) {
        this.radius = radius;
        return this;
    }

    public Gradient setColorLeft(String colorLeft) {
        if (colorLeft != null) {
            this.colorLeft = colorLeft;
        }
        return this;
    }

    public Gradient setColorRight(String colorRight) {
        if (colorRight != null) {
            this.colorRight = colorRight;
        }
        return this;
    }

    public Gradient setOrientation(GradientDrawable.Orientation orientation) {
        if (orientation != null) {
            this.orientation = orientation;
        }
        return this;
    }

    public GradientDrawable build() {
        GradientDrawable gradientDrawable = new GradientDrawable();
        gradientDrawable.setCornerRadius(radius);
        gradientDrawable.setColors(new int[] {
                Color.parseColor(colorLeft),
                Color.parseColor(colorRight)
        });
        gradientDrawable.setOrientation(orientation);
        return gradientDrawable;
    }
}