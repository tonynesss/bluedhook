package com.zjfgh.bluedhook.simple;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.util.HashMap;
import java.util.Map;

public class ResourceManager {
    // 静态映射资源名称到资源 ID
    private static final Map<String, Integer> drawableMap = new HashMap<>();

    static {
        drawableMap.put("anchor_monitor_icon", R.drawable.anchor_monitor_icon);
    }

    public static Bitmap getResDrawableBitmap(String resName) {
        Integer resId = drawableMap.get(resName);
        if (resId != null) {
            return BitmapFactory.decodeResource(AppContainer.getInstance().getModuleRes(), resId);
        }
        return null; // 如果资源名称无效，返回 null
    }
}
