package com.zjfgh.bluedhook.simple;

public class CoordinateConverter {

    private static final double PI = 3.1415926535897932384626;
    private static final double X_PI = 3.14159265358979324 * 3000.0 / 180.0;
    private static final double A = 6378245.0;
    private static final double EE = 0.00669342162296594323;

    /**
     * WGS84转GCJ02(火星坐标系)
     *
     * @param lat WGS84纬度
     * @param lon WGS84经度
     * @return GCJ02坐标数组，[纬度, 经度]
     */
    public static double[] wgs84ToGcj02(double lat, double lon) {
        if (outOfChina(lat, lon)) {
            return new double[]{lat, lon};
        }
        double dLat = transformLat(lon - 105.0, lat - 35.0);
        double dLon = transformLon(lon - 105.0, lat - 35.0);
        double radLat = lat / 180.0 * PI;
        double magic = Math.sin(radLat);
        magic = 1 - EE * magic * magic;
        double sqrtMagic = Math.sqrt(magic);
        dLat = (dLat * 180.0) / ((A * (1 - EE)) / (magic * sqrtMagic) * PI);
        dLon = (dLon * 180.0) / (A / sqrtMagic * Math.cos(radLat) * PI);
        double mgLat = lat + dLat;
        double mgLon = lon + dLon;
        return new double[]{mgLat, mgLon};
    }

    /**
     * GCJ02转WGS84
     *
     * @param lat GCJ02纬度
     * @param lon GCJ02经度
     * @return WGS84坐标数组，[纬度, 经度]
     */
    public static double[] gcj02ToWgs84(double lat, double lon) {
        double[] gps = transform(lat, lon);
        double lontitude = lon * 2 - gps[1];
        double latitude = lat * 2 - gps[0];
        return new double[]{latitude, lontitude};
    }

    /**
     * GCJ02转BD09
     *
     * @param lat GCJ02纬度
     * @param lon GCJ02经度
     * @return BD09坐标数组，[纬度, 经度]
     */
    public static double[] gcj02ToBd09(double lat, double lon) {
        double z = Math.sqrt(lon * lon + lat * lat) + 0.00002 * Math.sin(lat * X_PI);
        double theta = Math.atan2(lat, lon) + 0.000003 * Math.cos(lon * X_PI);
        double bdLon = z * Math.cos(theta) + 0.0065;
        double bdLat = z * Math.sin(theta) + 0.006;
        return new double[]{bdLat, bdLon};
    }

    /**
     * BD09转GCJ02
     *
     * @param lat BD09纬度
     * @param lon BD09经度
     * @return GCJ02坐标数组，[纬度, 经度]
     */
    public static double[] bd09ToGcj02(double lat, double lon) {
        double x = lon - 0.0065;
        double y = lat - 0.006;
        double z = Math.sqrt(x * x + y * y) - 0.00002 * Math.sin(y * X_PI);
        double theta = Math.atan2(y, x) - 0.000003 * Math.cos(x * X_PI);
        double ggLon = z * Math.cos(theta);
        double ggLat = z * Math.sin(theta);
        return new double[]{ggLat, ggLon};
    }

    /**
     * WGS84转BD09
     *
     * @param lat WGS84纬度
     * @param lon WGS84经度
     * @return BD09坐标数组，[纬度, 经度]
     */
    public static double[] wgs84ToBd09(double lat, double lon) {
        double[] gcj02 = wgs84ToGcj02(lat, lon);
        return gcj02ToBd09(gcj02[0], gcj02[1]);
    }

    /**
     * BD09转WGS84
     *
     * @param lat BD09纬度
     * @param lon BD09经度
     * @return WGS84坐标数组，[纬度, 经度]
     */
    public static double[] bd09ToWgs84(double lat, double lon) {
        double[] gcj02 = bd09ToGcj02(lat, lon);
        return gcj02ToWgs84(gcj02[0], gcj02[1]);
    }

    private static boolean outOfChina(double lat, double lon) {
        if (lon < 72.004 || lon > 137.8347) {
            return true;
        }
        return lat < 0.8293 || lat > 55.8271;
    }

    private static double transformLat(double x, double y) {
        double ret = -100.0 + 2.0 * x + 3.0 * y + 0.2 * y * y + 0.1 * x * y + 0.2 * Math.sqrt(Math.abs(x));
        ret += (20.0 * Math.sin(6.0 * x * PI) + 20.0 * Math.sin(2.0 * x * PI)) * 2.0 / 3.0;
        ret += (20.0 * Math.sin(y * PI) + 40.0 * Math.sin(y / 3.0 * PI)) * 2.0 / 3.0;
        ret += (160.0 * Math.sin(y / 12.0 * PI) + 320 * Math.sin(y * PI / 30.0)) * 2.0 / 3.0;
        return ret;
    }

    private static double transformLon(double x, double y) {
        double ret = 300.0 + x + 2.0 * y + 0.1 * x * x + 0.1 * x * y + 0.1 * Math.sqrt(Math.abs(x));
        ret += (20.0 * Math.sin(6.0 * x * PI) + 20.0 * Math.sin(2.0 * x * PI)) * 2.0 / 3.0;
        ret += (20.0 * Math.sin(x * PI) + 40.0 * Math.sin(x / 3.0 * PI)) * 2.0 / 3.0;
        ret += (150.0 * Math.sin(x / 12.0 * PI) + 300.0 * Math.sin(x / 30.0 * PI)) * 2.0 / 3.0;
        return ret;
    }

    private static double[] transform(double lat, double lon) {
        if (outOfChina(lat, lon)) {
            return new double[]{lat, lon};
        }
        double dLat = transformLat(lon - 105.0, lat - 35.0);
        double dLon = transformLon(lon - 105.0, lat - 35.0);
        double radLat = lat / 180.0 * PI;
        double magic = Math.sin(radLat);
        magic = 1 - EE * magic * magic;
        double sqrtMagic = Math.sqrt(magic);
        dLat = (dLat * 180.0) / ((A * (1 - EE)) / (magic * sqrtMagic) * PI);
        dLon = (dLon * 180.0) / (A / sqrtMagic * Math.cos(radLat) * PI);
        double mgLat = lat + dLat;
        double mgLon = lon + dLon;
        return new double[]{mgLat, mgLon};
    }
}
