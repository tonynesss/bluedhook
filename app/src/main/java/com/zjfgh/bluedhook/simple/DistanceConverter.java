package com.zjfgh.bluedhook.simple;

import android.annotation.SuppressLint;

public class DistanceConverter {

    // 公里转米
    public static double kmToMeters(double km) {
        return km * 1000;
    }

    // 米转公里
    public static double metersToKm(double meters) {
        return meters / 1000;
    }

    // 格式化距离显示
    @SuppressLint("DefaultLocale")
    public static String formatDistance(double km) {
        if (km < 1) {
            // 小于1公里显示为米
            return String.format("%dm", (int) Math.round(km * 1000));
        } else if (km < 10) {
            // 1-10公里保留2位小数
            return String.format("%.2fkm", km);
        } else {
            // 大于10公里保留1位小数
            return String.format("%.1fkm", km);
        }
    }

    // 处理浮点数精度问题
    public static double fixPrecision(double value) {
        return Math.round(value * 1000) / 1000.0;
    }
    /**
     * 根据公里数计算 AMap 的 zoomLevel（1-16）
     * @param distanceKm 输入公里数（需 > 0）
     * @return zoomLevel 范围 [1, 16]，数值越小地图越远
     */
    public static int calculateZoomLevelByDistance(double distanceKm) {
        // 边界条件处理
        if (distanceKm >= 2000) return 1;   // 2000km 及以上 -> 等级1
        if (distanceKm <= 0) return 20;  // 10m 及以下 -> 等级16

        // 对数公式计算（非线性映射）
        double zoomLevel = 1 + 15 - (Math.log(distanceKm) / Math.log(2));

        // 四舍五入并限制范围
        return (int) Math.min(20, Math.max(1, Math.round(zoomLevel)));
    }
}