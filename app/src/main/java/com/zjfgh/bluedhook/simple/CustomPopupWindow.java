package com.zjfgh.bluedhook.simple;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowInsetsController;
import android.widget.PopupWindow;

public class CustomPopupWindow {
    private PopupWindow popupWindow;
    private final Activity activity;
    private final int popupColor;
    private final View contentView;
    private int originalStatusBarColor;
    private int originalNavigationBarColor;
    private int originalSystemUiVisibility;
    private boolean originalLightStatusBars;
    private boolean originalLightNavBars;
    private boolean hasSavedOriginalState = false;
    private OnDismissListener onDismissListener;

    public CustomPopupWindow(Activity activity, View contentView, int popupColor) {
        this.activity = activity;
        this.contentView = contentView;
        this.popupColor = popupColor;
        initPopupWindow();
    }

    private void initPopupWindow() {
        popupWindow = new PopupWindow(
                contentView,
                android.view.ViewGroup.LayoutParams.MATCH_PARENT,
                android.view.ViewGroup.LayoutParams.WRAP_CONTENT,
                true
        );
        popupWindow.setOnDismissListener(() -> {
            if (onDismissListener != null){
                onDismissListener.onDismiss();
            }
            restoreOriginalSystemBarState();
        });
    }

    public void showAtCenter() {
        showAtLocation(Gravity.CENTER, 0, 0);
    }

    public void showAtLocation(int gravity, int x, int y) {
        if (!hasSavedOriginalState) {
            saveOriginalSystemBarState();
            hasSavedOriginalState = true;
        }

        updateSystemBarsForPopup();
        popupWindow.showAtLocation(contentView, gravity, x, y);
    }

    public void dismiss() {
        popupWindow.dismiss();
    }
    public boolean isShowing() {
        return popupWindow.isShowing();
    }
    public void setBackgroundDrawable(Drawable background){
        popupWindow.setBackgroundDrawable(background);
    }
    private void saveOriginalSystemBarState() {
        Window window = activity.getWindow();
        originalStatusBarColor = window.getStatusBarColor();
        originalNavigationBarColor = window.getNavigationBarColor();

        originalSystemUiVisibility = window.getDecorView().getSystemUiVisibility();
        originalLightStatusBars = (originalSystemUiVisibility & View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR) != 0;
        originalLightNavBars = (originalSystemUiVisibility & View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR) != 0;
    }

    private void restoreOriginalSystemBarState() {
        Window window = activity.getWindow();

        // 恢复颜色
        window.setStatusBarColor(originalStatusBarColor);
        window.setNavigationBarColor(originalNavigationBarColor);

        // 恢复系统UI标志
        int newVisibility = originalSystemUiVisibility;

        // 清除可能影响状态栏的标志位
        newVisibility &= ~View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR;
        newVisibility &= ~View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR;

        // 重新设置原始标志位
        if (originalLightStatusBars) {
            newVisibility |= View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR;
        }
        if (originalLightNavBars) {
            newVisibility |= View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR;
        }

        window.getDecorView().setSystemUiVisibility(newVisibility);

        // 对于API 30+，使用WindowInsetsController
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.R) {
            WindowInsetsController insetsController = window.getInsetsController();
            if (insetsController != null) {
                insetsController.setSystemBarsAppearance(
                        originalLightStatusBars ? WindowInsetsController.APPEARANCE_LIGHT_STATUS_BARS : 0,
                        WindowInsetsController.APPEARANCE_LIGHT_STATUS_BARS
                );
                insetsController.setSystemBarsAppearance(
                        originalLightNavBars ? WindowInsetsController.APPEARANCE_LIGHT_NAVIGATION_BARS : 0,
                        WindowInsetsController.APPEARANCE_LIGHT_NAVIGATION_BARS
                );
            }
        }
    }

    private void updateSystemBarsForPopup() {
        Window window = activity.getWindow();

        // 设置新颜色
        window.setStatusBarColor(popupColor);
        window.setNavigationBarColor(popupColor);

        // 根据颜色亮度调整文字颜色
        boolean isLightColor = isColorLight(popupColor);

        int newVisibility = window.getDecorView().getSystemUiVisibility();

        // 清除现有标志
        newVisibility &= ~View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR;
        newVisibility &= ~View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR;

        // 设置新标志
        if (isLightColor) {
            newVisibility |= View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR;
            newVisibility |= View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR;
        }

        window.getDecorView().setSystemUiVisibility(newVisibility);

        // 对于API 30+
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.R) {
            WindowInsetsController insetsController = window.getInsetsController();
            if (insetsController != null) {
                insetsController.setSystemBarsAppearance(
                        isLightColor ? WindowInsetsController.APPEARANCE_LIGHT_STATUS_BARS : 0,
                        WindowInsetsController.APPEARANCE_LIGHT_STATUS_BARS
                );
                insetsController.setSystemBarsAppearance(
                        isLightColor ? WindowInsetsController.APPEARANCE_LIGHT_NAVIGATION_BARS : 0,
                        WindowInsetsController.APPEARANCE_LIGHT_NAVIGATION_BARS
                );
            }
        }
    }

    /**
     * 判断颜色是否为浅色
     */
    private boolean isColorLight(int color) {
        double darkness = 1 - (0.299 * Color.red(color) + 0.587 * Color.green(color) + 0.114 * Color.blue(color)) / 255;
        return darkness < 0.5;
    }

    // 设置PopupWindow的其他属性
    public void setOutsideTouchable(boolean touchable) {
        popupWindow.setOutsideTouchable(touchable);
    }

    public void setFocusable(boolean focusable) {
        popupWindow.setFocusable(focusable);
    }

    public void setAnimationStyle(int animationStyle) {
        popupWindow.setAnimationStyle(animationStyle);
    }
    public void setOnDismissListener(OnDismissListener onDismissListener){
        this.onDismissListener = onDismissListener;
    }
    public interface OnDismissListener{
        void onDismiss();
    }
}
