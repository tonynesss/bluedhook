package com.zjfgh.bluedhook.simple;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.res.XModuleResources;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.animation.LinearInterpolator;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.Objects;

public class TechDialog extends Dialog {

    private String title = "系统提示：";
    private String message = "";
    private String confirmText = "确认";
    private String cancelText = "取消";
    private String editContent = "";
    private String editHint = "";
    private OnConfirmListener onConfirmListener;
    private OnCancelListener onCancelListener;
    private final Context context;
    private final XModuleResources modRes;
    private boolean isHint;
    private Button btnConfirm;
    private Button btnCancel;

    public interface OnConfirmListener {
        void onConfirm(TechDialog techDialog, String editSrt);
    }

    public interface OnCancelListener {
        void onCancel();
    }

    public TechDialog(Context context, XModuleResources modRes) {
        super(context);
        this.context = context;
        this.modRes = modRes;
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(modRes.getLayout(R.layout.tech_dialog), null, false);
        setContentView(view);

        // 保持原有的背景设置方式
        Objects.requireNonNull(getWindow()).setBackgroundDrawableResource(android.R.color.transparent);
        view.setBackground(modRes.getDrawable(R.drawable.tech_dialog_bg, null));

        TextView tvTitle = findViewById(R.id.tvTitle);
        TextView tvMessage = findViewById(R.id.tvMessage);
        EditText etEditContent = findViewById(R.id.etEditContent);
        btnConfirm = findViewById(R.id.btnConfirm);
        btnCancel = findViewById(R.id.btnCancel);

        // 保持原有的角标背景设置方式
        View cornerTL = findViewById(R.id.cornerTL);
        cornerTL.setBackground(modRes.getDrawable(R.drawable.tech_corner_tl, null));
        View cornerTR = findViewById(R.id.cornerTR);
        cornerTR.setBackground(modRes.getDrawable(R.drawable.tech_corner_tr, null));
        View cornerBL = findViewById(R.id.cornerBL);
        cornerBL.setBackground(modRes.getDrawable(R.drawable.tech_corner_bl, null));
        View cornerBR = findViewById(R.id.cornerBR);
        cornerBR.setBackground(modRes.getDrawable(R.drawable.tech_corner_br, null));

        tvTitle.setText(title);
        if (!isHint){
            etEditContent.setVisibility(View.VISIBLE);
            etEditContent.setText(editContent);
            etEditContent.setHint(editHint);
        }else {
            etEditContent.setVisibility(View.GONE);
        }
        if (!message.isEmpty()){
            tvMessage.setVisibility(View.VISIBLE);
            tvMessage.setText(message);
        }else {
            tvMessage.setVisibility(View.GONE);
        }

        // 保持原有的按钮背景设置方式
        btnConfirm.setText(confirmText);
        btnConfirm.setBackground(modRes.getDrawable(R.drawable.tech_btn_bg, null));
        btnCancel.setText(cancelText);
        btnCancel.setBackground(modRes.getDrawable(R.drawable.tech_btn_bg, null));

        // 扫描线动画 - 保持原有背景设置方式
        final View scanLine = findViewById(R.id.scanLine);
        scanLine.setBackground(modRes.getDrawable(R.drawable.scan_line_gradient, null));
        ObjectAnimator scanAnimator = ObjectAnimator.ofFloat(
                scanLine,
                "translationY",
                -20f,
                scanLine.getHeight() + 20f
        );
        scanAnimator.setDuration(1500);
        scanAnimator.setRepeatCount(ValueAnimator.INFINITE);
        scanAnimator.setInterpolator(new LinearInterpolator());
        scanAnimator.start();

        // 脉冲点动画 - 保持原有背景设置方式
        final ImageView pulseDot = findViewById(R.id.pulseDot);
        pulseDot.setBackground(modRes.getDrawable(R.drawable.pulse_dot, null));
        ObjectAnimator pulseAnimator = ObjectAnimator.ofArgb(
                pulseDot,
                "colorFilter",
                Color.argb(255, 0, 255, 252),
                Color.argb(0, 0, 255, 252)
        );
        pulseAnimator.setDuration(1000);
        pulseAnimator.setRepeatCount(ValueAnimator.INFINITE);
        pulseAnimator.setRepeatMode(ValueAnimator.REVERSE);
        pulseAnimator.start();

        btnConfirm.setOnClickListener(v -> {
            if (onConfirmListener != null) {
                onConfirmListener.onConfirm(this, String.valueOf(etEditContent.getText()));
            }
        });

        btnCancel.setOnClickListener(v -> {
            dismiss();
            if (onCancelListener != null) {
                onCancelListener.onCancel();
            }
        });
    }

    public Button getBtnConfirm() {
        return btnConfirm;
    }
    public Button getBtnCancel(){
        return btnCancel;
    }

    // Builder类保持不变
    public static class Builder {
        private final TechDialog dialog;

        public Builder(Context context, XModuleResources modRes) {
            this.dialog = new TechDialog(context, modRes);
        }

        public Builder setTitle(String title) {
            dialog.title = title;
            return this;
        }

        public Builder isHintEdit(boolean isHint) {
            dialog.isHint = isHint;
            return this;
        }
        public Builder setEditContent(String content) {
            dialog.editContent = content;
            return this;
        }
        public Builder setEditHint(String hint) {
            dialog.editHint = hint;
            return this;
        }
        public Builder setMessage(String message) {
            dialog.message = message;
            return this;
        }
        public Builder setConfirmText(String text) {
            dialog.confirmText = text;
            return this;
        }

        public Builder setCancelText(String text) {
            dialog.cancelText = text;
            return this;
        }

        public Builder setOnConfirmListener(OnConfirmListener listener) {
            dialog.onConfirmListener = listener;
            return this;
        }

        public Builder setOnCancelListener(OnCancelListener listener) {
            dialog.onCancelListener = listener;
            return this;
        }

        public TechDialog create() {
            return dialog;
        }

        public void show() {
            TechDialog dialog = create();
            dialog.show();
        }
    }
}