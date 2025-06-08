package com.zjfgh.bluedhook.simple;

import android.app.AlertDialog;
import android.content.Context;

public class DeleteConfirmationDialog {
    public interface DeleteConfirmationListener {
        void onConfirmDelete();
        void onCancel();
    }

    public static void show(Context context, String userName, DeleteConfirmationListener listener) {
        new AlertDialog.Builder(context)
                .setTitle("删除用户")
                .setMessage("确定要删除用户 " + userName + " 吗？")
                .setPositiveButton("删除", (dialog, which) -> {
                    if (listener != null) {
                            listener.onConfirmDelete();
                    }
                })
                .setNegativeButton("取消", (dialog, which) -> {
                    if (listener != null) {
                        listener.onCancel();
                    }
                })
                .create()
                .show();
    }
}