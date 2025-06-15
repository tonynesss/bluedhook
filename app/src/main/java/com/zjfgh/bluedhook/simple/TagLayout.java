package com.zjfgh.bluedhook.simple;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

public class TagLayout extends LinearLayout {

    public TagLayout(Context context) {
        super(context);
        init();
    }

    public TagLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public TagLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        setOrientation(HORIZONTAL);
        setLayoutParams(new LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT));
    }

    private float firstMarginStartSize = 5;
    private float marginStartSize = 5;

    /**
     * 设置左外边距大小，请在添加文本标签前设置，否则无效。
     *
     * @param size 外边距大小
     */
    public void setMarginStart(float size) {
        this.marginStartSize = size;
    }

    /**
     * 设置首个标签左外边距大小。
     *
     * @param size 外边距大小
     */
    public void setFirstMarginStartSize(float size) {
        this.firstMarginStartSize = size;
        if (getChildCount() != 0) {
            TextView textView = (TextView) getChildAt(0);
            LayoutParams params = new LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT);
            params.setMarginStart(dpToPx(firstMarginStartSize));
            textView.setLayoutParams(params);
        }
    }

    /**
     * 添加一个新的TextView标签
     *
     * @param text               标签文本
     * @param textSize           文本大小
     * @param backgroundDrawable 背景Drawable
     */
    public TextView addTextView(String text, float textSize, Drawable backgroundDrawable) {
        TextView textView = new TextView(getContext());
        LayoutParams params = new LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        if (getChildCount() == 0) {
            params.setMarginStart(dpToPx(firstMarginStartSize));
        } else {
            params.setMarginStart(dpToPx(marginStartSize));
        }
        textView.setLayoutParams(params);
        textView.setText(text);
        textView.setTextColor(0xFFFFFFFF);
        textView.setBackground(backgroundDrawable);
        textView.setPadding(dpToPx(4), dpToPx(2), dpToPx(4), dpToPx(2));
        textView.setTextSize(textSize);
        addView(textView);
        return textView;
    }

    private int dpToPx(float dp) {
        float density = getResources().getDisplayMetrics().density;
        return Math.round(dp * density);
    }
}