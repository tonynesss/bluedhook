package com.zjfgh.bluedhook.simple;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;

import de.robv.android.xposed.XposedHelpers;

import java.lang.reflect.Proxy;

public class AMapHookHelper {
    private static final String TAG = "AMapHookHelper";

    private final Context mContext;
    private final ClassLoader mClassLoader;
    private Object mMapView;  // com.amap.api.maps.MapView
    private Object mAMap;     // com.amap.api.maps.AMap

    public AMapHookHelper(Context context, ClassLoader classLoader) {
        this.mContext = context;
        this.mClassLoader = classLoader;
    }

    /**
     * 初始化并返回 MapView（需手动调用生命周期方法）
     */
    public View createMapView() {
        try {
            // 1. 反射创建 MapView
            Class<?> mapViewClass = XposedHelpers.findClass("com.amap.api.maps.MapView", mClassLoader);
            Constructor<?> constructor = mapViewClass.getConstructor(Context.class);
            mMapView = constructor.newInstance(mContext);

            // 2. 获取 AMap 对象
            Method getMap = mapViewClass.getMethod("getMap");
            mAMap = getMap.invoke(mMapView);

            Log.d(TAG, "MapView created successfully");
            return (View) mMapView;

        } catch (Exception e) {
            Log.e(TAG, "Failed to create MapView", e);
            return null;
        }
    }

    /**
     * 调用 MapView.onCreate()
     */
    public void onCreate(Bundle savedInstanceState) {
        try {
            Method onCreate = mMapView.getClass().getMethod("onCreate", Bundle.class);
            onCreate.invoke(mMapView, savedInstanceState);
            Log.d(TAG, "MapView.onCreate() called");
        } catch (Exception e) {
            Log.e(TAG, "Failed to call MapView.onCreate()", e);
        }
    }

    /**
     * 调用 MapView.onResume()
     */
    public void onResume() {
        try {
            Method onResume = mMapView.getClass().getMethod("onResume");
            onResume.invoke(mMapView);
            Log.d(TAG, "MapView.onResume() called");
        } catch (Exception e) {
            Log.e(TAG, "Failed to call MapView.onResume()", e);
        }
    }

    /**
     * 调用 MapView.onPause()
     */
    public void onPause() {
        try {
            Method onPause = mMapView.getClass().getMethod("onPause");
            onPause.invoke(mMapView);
            Log.d(TAG, "MapView.onPause() called");
        } catch (Exception e) {
            Log.e(TAG, "Failed to call MapView.onPause()", e);
        }
    }

    /**
     * 调用 MapView.onDestroy()
     */
    public void onDestroy() {
        try {
            Method onDestroy = mMapView.getClass().getMethod("onDestroy");
            onDestroy.invoke(mMapView);
            Log.d(TAG, "MapView.onDestroy() called");
        } catch (Exception e) {
            Log.e(TAG, "Failed to call MapView.onDestroy()", e);
        }
    }

    /**
     * 移动地图到指定位置
     */
    public void moveCamera(double lat, double lng, float zoomLevel) {
        try {
            // 创建 LatLng
            Class<?> latLngClass = XposedHelpers.findClass("com.amap.api.maps.model.LatLng", mClassLoader);
            Object latLng = latLngClass.getConstructor(double.class, double.class)
                    .newInstance(lat, lng);

            // 创建 CameraUpdate
            Class<?> cameraUpdateFactory = XposedHelpers.findClass("com.amap.api.maps.CameraUpdateFactory", mClassLoader);
            Method newLatLngZoom = cameraUpdateFactory.getMethod("newLatLngZoom", latLngClass, float.class);
            Object cameraUpdate = newLatLngZoom.invoke(null, latLng, zoomLevel);

            // 移动镜头
            Method moveCamera = mAMap.getClass().getMethod("moveCamera",
                    XposedHelpers.findClass("com.amap.api.maps.CameraUpdate", mClassLoader));
            moveCamera.invoke(mAMap, cameraUpdate);

            Log.d(TAG, "Map moved to (" + lat + ", " + lng + ")");
        } catch (Exception e) {
            Log.e(TAG, "Failed to move camera", e);
        }
    }

    /**
     * 添加标记到地图
     */
    public void addMarker(double lat, double lng, String title) {
        try {
            // 创建 LatLng
            Class<?> latLngClass = XposedHelpers.findClass("com.amap.api.maps.model.LatLng", mClassLoader);
            Object latLng = latLngClass.getConstructor(double.class, double.class)
                    .newInstance(lat, lng);

            // 创建 MarkerOptions
            Class<?> markerOptionsClass = XposedHelpers.findClass("com.amap.api.maps.model.MarkerOptions", mClassLoader);
            Object markerOptions = markerOptionsClass.getDeclaredConstructor().newInstance();

            // 设置位置和标题
            Method position = markerOptionsClass.getMethod("position", latLngClass);
            position.invoke(markerOptions, latLng);

            Method setTitle = markerOptionsClass.getMethod("title", String.class);
            setTitle.invoke(markerOptions, title);

            // 添加标记到地图
            Method addMarker = mAMap.getClass().getMethod("addMarker", markerOptionsClass);
            addMarker.invoke(mAMap, markerOptions);

            Log.d(TAG, "Marker added at (" + lat + ", " + lng + ")");
        } catch (Exception e) {
            Log.e(TAG, "Failed to add marker", e);
        }
    }

    /**
     * 添加带自定义图标的标记到地图
     *
     * @param lat       纬度
     * @param lng       经度
     * @param title     标题
     * @param iconResId 图标资源ID（来自你的Xposed模块资源）
     */
    public Object addMarkerWithIcon(double lat, double lng, String title, int iconResId) {
        try {
            // 1. 创建 LatLng
            Class<?> latLngClass = XposedHelpers.findClass(
                    "com.amap.api.maps.model.LatLng", mClassLoader);
            Object latLng = latLngClass.getConstructor(double.class, double.class)
                    .newInstance(lat, lng);

            // 2. 创建 MarkerOptions
            Class<?> markerOptionsClass = XposedHelpers.findClass(
                    "com.amap.api.maps.model.MarkerOptions", mClassLoader);
            Object markerOptions = markerOptionsClass.getDeclaredConstructor().newInstance();

            // 3. 设置基本属性
            Method position = markerOptionsClass.getMethod("position", latLngClass);
            position.invoke(markerOptions, latLng);

            if (title != null) {
                Method setTitle = markerOptionsClass.getMethod("title", String.class);
                setTitle.invoke(markerOptions, title);
            }

            // 4. 设置自定义图标
            if (iconResId != 0) {
                // 获取 BitmapDescriptorFactory 类
                Class<?> bitmapDescFactoryClass = XposedHelpers.findClass(
                        "com.amap.api.maps.model.BitmapDescriptorFactory", mClassLoader);

                // 从资源创建 BitmapDescriptor
                Method fromResource = bitmapDescFactoryClass.getMethod(
                        "fromResource", int.class);
                Object iconDescriptor = fromResource.invoke(null, iconResId);

                // 设置图标
                Method iconMethod = markerOptionsClass.getMethod("icon",
                        XposedHelpers.findClass("com.amap.api.maps.model.BitmapDescriptor", mClassLoader));
                iconMethod.invoke(markerOptions, iconDescriptor);
            }

            // 5. 添加标记到地图
            Method addMarker = mAMap.getClass().getMethod("addMarker", markerOptionsClass);
            Object marker = addMarker.invoke(mAMap, markerOptions);
            Log.d(TAG, "Marker with icon added at (" + lat + ", " + lng + ")");
            return marker;

        } catch (Exception e) {
            Log.e(TAG, "Failed to add marker with icon", e);
            return null;
        }
    }

    /**
     * 添加带自定义图标（Drawable）的标记到地图
     *
     * @param lat      纬度
     * @param lng      经度
     * @param title    标题
     * @param drawable 图标Drawable
     * @return 标记对象
     */
    public Object addMarkerWithDrawable(double lat, double lng, String title, Drawable drawable) {
        try {
            // 1. 将Drawable转换为Bitmap
            Bitmap bitmap = drawableToBitmap(drawable);
            if (bitmap == null) {
                Log.e(TAG, "Failed to convert Drawable to Bitmap");
                return null;
            }

            // 2. 创建LatLng
            Class<?> latLngClass = XposedHelpers.findClass(
                    "com.amap.api.maps.model.LatLng", mClassLoader);
            Object latLng = latLngClass.getConstructor(double.class, double.class)
                    .newInstance(lat, lng);

            // 3. 创建MarkerOptions
            Class<?> markerOptionsClass = XposedHelpers.findClass(
                    "com.amap.api.maps.model.MarkerOptions", mClassLoader);
            Object markerOptions = markerOptionsClass.getDeclaredConstructor().newInstance();

            // 4. 设置基本属性
            Method position = markerOptionsClass.getMethod("position", latLngClass);
            position.invoke(markerOptions, latLng);

            if (title != null) {
                Method setTitle = markerOptionsClass.getMethod("title", String.class);
                setTitle.invoke(markerOptions, title);
            }

            // 5. 设置自定义图标
            Class<?> bitmapDescFactoryClass = XposedHelpers.findClass(
                    "com.amap.api.maps.model.BitmapDescriptorFactory", mClassLoader);

            Method fromBitmap = bitmapDescFactoryClass.getMethod("fromBitmap", Bitmap.class);
            Object iconDescriptor = fromBitmap.invoke(null, bitmap);

            Method iconMethod = markerOptionsClass.getMethod("icon",
                    XposedHelpers.findClass("com.amap.api.maps.model.BitmapDescriptor", mClassLoader));
            iconMethod.invoke(markerOptions, iconDescriptor);

            // 6. 添加标记到地图
            Method addMarker = mAMap.getClass().getMethod("addMarker", markerOptionsClass);
            Object marker = addMarker.invoke(mAMap, markerOptions);

            Log.d(TAG, "Marker with Drawable icon added at (" + lat + ", " + lng + ")");
            return marker;

        } catch (Exception e) {
            Log.e(TAG, "Failed to add marker with Drawable", e);
            return null;
        }
    }

    /**
     * 将Drawable转换为Bitmap
     */
    private Bitmap drawableToBitmap(Drawable drawable) {
        if (drawable instanceof BitmapDrawable) {
            return ((BitmapDrawable) drawable).getBitmap();
        }
        int width = drawable.getIntrinsicWidth();
        int height = drawable.getIntrinsicHeight();
        // 如果Drawable没有固有尺寸，使用默认尺寸
        if (width <= 0) width = 48;  // 默认宽度48px
        if (height <= 0) height = 48; // 默认高度48px
        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);
        return bitmap;
    }

    /**
     * 设置地图点击监听器
     *
     * @param listener 点击事件的回调接口
     */
    public void setOnMapClickListener(final OnMapClickListener listener) {
        try {
            Class<?> onMapClickListenerClass = XposedHelpers.findClass(
                    "com.amap.api.maps.AMap.OnMapClickListener", mClassLoader);

            // Create a proxy implementation of the listener interface
            Object proxy = Proxy.newProxyInstance(mClassLoader,
                    new Class<?>[]{onMapClickListenerClass}, (proxy1, method, args) -> {
                        if (method.getName().equals("onMapClick") && args != null && args.length == 1) {
                            Object latLng = args[0];
                            Class<?> latLngClass = XposedHelpers.findClass(
                                    "com.amap.api.maps.model.LatLng", mClassLoader);

                            double lat = (double) XposedHelpers.getObjectField(latLng, "latitude");
                            double lng = (double) XposedHelpers.getObjectField(latLng, "longitude");

                            listener.onMapClick(lat, lng);
                        }
                        return null;
                    });

            // Set the listener on AMap
            Method setOnMapClickListener = mAMap.getClass().getMethod(
                    "setOnMapClickListener", onMapClickListenerClass);
            setOnMapClickListener.invoke(mAMap, proxy);

            Log.d(TAG, "Map click listener set successfully");
        } catch (Exception e) {
            Log.e(TAG, "Failed to set map click listener", e);
        }
    }

    /**
     * 在地图上添加圆形覆盖物（支持HTML颜色格式）
     *
     * @param lat         圆心纬度
     * @param lng         圆心经度
     * @param radius      半径(米)
     * @param strokeColor 边框颜色（支持格式：#RGB、#RRGGBB、#AARRGGBB）
     * @param fillColor   填充颜色（支持格式同上）
     * @return 圆形对象
     */
    public Object addCircle(double lat, double lng, double radius, Object strokeColor, Object fillColor) {
        try {
            // 转换颜色格式（支持Integer或String格式）
            int strokeColorInt = parseColor(strokeColor);
            int fillColorInt = parseColor(fillColor);

            // 创建 LatLng
            Class<?> latLngClass = XposedHelpers.findClass(
                    "com.amap.api.maps.model.LatLng", mClassLoader);
            Object latLng = latLngClass.getConstructor(double.class, double.class)
                    .newInstance(lat, lng);

            // 创建 CircleOptions
            Class<?> circleOptionsClass = XposedHelpers.findClass(
                    "com.amap.api.maps.model.CircleOptions", mClassLoader);
            Object circleOptions = circleOptionsClass.getDeclaredConstructor().newInstance();
            // 设置圆形参数
            Method center = circleOptionsClass.getMethod("center", latLngClass);
            center.invoke(circleOptions, latLng);

            Method setRadius = circleOptionsClass.getMethod("radius", double.class);
            setRadius.invoke(circleOptions, radius);

            Method setStrokeColor = circleOptionsClass.getMethod("strokeColor", int.class);
            setStrokeColor.invoke(circleOptions, strokeColorInt);

            Method setFillColor = circleOptionsClass.getMethod("fillColor", int.class);
            setFillColor.invoke(circleOptions, fillColorInt);

            Method setStrokeWidth = circleOptionsClass.getMethod("strokeWidth", float.class);
            setStrokeWidth.invoke(circleOptions, 1.0f); // 默认边框宽度1像素

            // 添加圆形到地图
            Method addCircle = mAMap.getClass().getMethod("addCircle", circleOptionsClass);
            Object circle = addCircle.invoke(mAMap, circleOptions);

            Log.d(TAG, String.format("Circle added at (%.6f, %.6f) with radius %.1fm",
                    lat, lng, radius));
            return circle;
        } catch (Exception e) {
            Log.e(TAG, "Failed to add circle", e);
            return null;
        }
    }

    /**
     * 颜色格式转换（支持Integer或String格式）
     */
    private int parseColor(Object color) throws IllegalArgumentException {
        if (color instanceof Integer) {
            return (int) color;
        } else if (color instanceof String) {
            String htmlColor = (String) color;
            if (!htmlColor.startsWith("#")) {
                htmlColor = "#" + htmlColor;
            }

            // 扩展简写格式
            if (htmlColor.length() == 4) { // #RGB → #RRGGBB
                htmlColor = "#" +
                        htmlColor.charAt(1) + htmlColor.charAt(1) +
                        htmlColor.charAt(2) + htmlColor.charAt(2) +
                        htmlColor.charAt(3) + htmlColor.charAt(3);
            } else if (htmlColor.length() == 5) { // #ARGB → #AARRGGBB
                htmlColor = "#" +
                        htmlColor.charAt(1) + htmlColor.charAt(1) +
                        htmlColor.charAt(2) + htmlColor.charAt(2) +
                        htmlColor.charAt(3) + htmlColor.charAt(3) +
                        htmlColor.charAt(4) + htmlColor.charAt(4);
            } else if (htmlColor.length() == 7) { // #RRGGBB → #FFRRGGBB
                htmlColor = "#FF" + htmlColor.substring(1);
            }

            // 转换为long避免负数问题
            long longColor = Long.parseLong(htmlColor.substring(1), 16);
            return (int) longColor;
        }
        throw new IllegalArgumentException("Unsupported color format");
    }

    /**
     * 清除地图上所有覆盖物（标记、线、圆等）
     */
    public void clearAllOverlays() {
        try {
            // 获取 AMap 的 clear 方法
            Method clearMethod = mAMap.getClass().getMethod("clear");
            clearMethod.invoke(mAMap);
            Log.d(TAG, "All map overlays cleared");
        } catch (Exception e) {
            Log.e(TAG, "Failed to clear map overlays", e);
        }
    }

    public interface OnMapClickListener {
        void onMapClick(double lat, double lng);
    }
}