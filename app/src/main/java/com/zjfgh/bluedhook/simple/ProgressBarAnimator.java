package com.zjfgh.bluedhook.simple;

import android.animation.ValueAnimator;
import android.view.View;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;

import android.animation.ValueAnimator;
import android.view.View;
import android.widget.LinearLayout;

public class ProgressBarAnimator {

    /**
     * 平滑动画改变进度条
     * @param progressView 进度条 View
     * @param spaceView    空白 Space
     * @param targetProgress 目标进度 (0.0~1.0)
     * @param duration      动画时长（毫秒）
     */
    public static void animateProgress(
            final View progressView,
            final View spaceView,
            float targetProgress,
            long duration
    ) {
        // 获取当前的 LayoutParams
        final LinearLayout.LayoutParams progressParams =
                (LinearLayout.LayoutParams) progressView.getLayoutParams();
        final LinearLayout.LayoutParams spaceParams =
                (LinearLayout.LayoutParams) spaceView.getLayoutParams();

        // 当前进度值
        float currentProgress = progressParams.weight;

        // 使用 ValueAnimator 平滑过渡
        ValueAnimator animator = ValueAnimator.ofFloat(currentProgress, targetProgress);
        animator.setDuration(duration);

        animator.addUpdateListener(animation -> {
            float progress = (float) animation.getAnimatedValue();

            // 更新进度条 weight
            progressParams.weight = progress;
            progressView.setLayoutParams(progressParams);

            // 更新 Space weight = 1 - progress
            spaceParams.weight = 1f - progress;
            spaceView.setLayoutParams(spaceParams);
        });

        animator.start();
    }
}