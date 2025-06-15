package com.zjfgh.bluedhook.simple;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.alibaba.fastjson2.TypeReference;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedHelpers;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class LiveHook {
    private static LiveHook instance;
    private final NetworkManager networkManager = NetworkManager.getInstance();
    private final Context appContext;
    private final ClassLoader classLoader;
    private WeakReference<ImageView> anchorMonitorIvRef;

    public static synchronized LiveHook getInstance(Context appContext) {
        if (instance == null) {
            instance = new LiveHook(appContext);
        }
        return instance;
    }

    private LiveHook(Context appContext) {
        this.appContext = appContext;
        this.classLoader = appContext.getClassLoader();
        anchorMonitorHook();
        joinLiveHook();
    }

    public void anchorMonitorHook() {
        try {
            Class<?> LiveFragment = XposedHelpers.findClass("com.soft.blued.ui.live.fragment.LiveFragment", classLoader);
            XposedHelpers.findAndHookConstructor("com.soft.blued.ui.live.fragment.LiveFragment_ViewBinding", classLoader, LiveFragment, View.class, new XC_MethodHook() {
                @Override
                protected void beforeHookedMethod(XC_MethodHook.MethodHookParam param) throws Throwable {
                    super.beforeHookedMethod(param);
                    View view = (View) param.args[1];
                    // 获取标题栏
                    @SuppressLint("DiscouragedApi")
                    int titleId = appContext.getResources().getIdentifier("title", "id", appContext.getPackageName());
                    LinearLayout title = view.findViewById(titleId);
                    // 创建并添加图标
                    ImageView anchorMonitorIv = new ImageView(appContext);
                    anchorMonitorIvRef = new WeakReference<>(anchorMonitorIv);
                    Bitmap iconBitmap = ResourceManager.getResDrawableBitmap("anchor_monitor_icon");
                    if (iconBitmap != null) {
                        anchorMonitorIv.setImageBitmap(iconBitmap);
                    } else {
                        // 设置默认图标或日志记录
                        Log.w("LiveHook", "anchor_monitor_icon-资源未找到");
                    }
                    anchorMonitorIv.setLayoutParams(new LinearLayout.LayoutParams(1, LinearLayout.LayoutParams.MATCH_PARENT));
                    title.addView(anchorMonitorIv, 2);
                    // 动态调整图标大小
                    adjustIconSize(anchorMonitorIv);
                    anchorMonitorIv.setTag(view);
                    anchorMonitorIv.setOnClickListener(anchorMonitorIconListener);
                    anchorMonitorIv.post(() -> {
                        boolean isSwitchOn = SQLiteManagement.getInstance().getSettingByFunctionId(SettingsViewCreator.ANCHOR_MONITOR_LIVE_HOOK).isSwitchOn();
                        setAnchorMonitorIvVisibility(isSwitchOn);
                    });
                }
            });
        } catch (Exception e) {
            Log.e("LiveHook", "AnchorMonitorHook 执行时发生异常", e);
        }
    }

    private void adjustIconSize(ImageView icon) {
        icon.post(() -> {
            int height = icon.getHeight();
            if (height > 0) {
                LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(height - 50, height - 50);
                layoutParams.gravity = Gravity.CENTER_VERTICAL;
                icon.setLayoutParams(layoutParams);
            }
        });
    }

    public void setAnchorMonitorIvVisibility(boolean isVisibility) {
        if (anchorMonitorIvRef == null || anchorMonitorIvRef.get() == null) {
            Log.e("LiveHook", "方法 setAnchorMonitorIvVisibility" +
                    "anchorMonitorIvRef是空的");
            return;
        }
        anchorMonitorIvRef.get().setVisibility(isVisibility ? View.VISIBLE : View.GONE);
    }

    private final View.OnClickListener anchorMonitorIconListener = v -> {
        // 获取当前按钮的上层视图
        View view = (View) v.getTag();
        UserPopupWindow.getInstance().show(view.getContext());
        JSONObject jsonObj = new JSONObject();
        JSONObject context = new JSONObject();
        JSONObject argv = new JSONObject();
        jsonObj.put("context", context);
        context.put("argv", argv);
        argv.put("type", "getAllData");
        String bodyString = jsonObj.toString();
        Map<String, String> header = new HashMap<>();
        header.put("AirScript-Token", "5e56J7bsc45egjJUlfFM6C");
        networkManager.postAsync(NetworkManager.getJinShanDocBluedUsersApi(), bodyString, header, new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                UserDataManager.getInstance().refreshUsers();
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                assert response.body() != null;
                String result = response.body().string();
                // 1. 解析外层 JSON → JinShanDocApiResponse
                JinShanDocApiResponse apiResponse = JSON.parseObject(result, JinShanDocApiResponse.class);
                // 2. 解析 data.result → JinShanDocApiResponse.ResultData
                JinShanDocApiResponse.ResultData resultData = JSON.parseObject(
                        apiResponse.getData().getResult(),
                        new TypeReference<>() {
                        }
                );
                // 3. 获取 List<User> 并遍历
                List<User> users = resultData.getData();
                for (User user : users) {
                    String uid = user.getUid();
                    SQLiteManagement dbManger = SQLiteManagement.getInstance();
                    User searchUser = dbManger.getUserByUid(uid);
                    if (searchUser == null) {
                        Log.i("BluedHook", "云端 " + user.getName() + " 不存在，同步至本地中...");
                        dbManger.addOrUpdateUser(user);
                    }

                }
                UserDataManager.getInstance().refreshUsers();
            }
        });
    };

    public void joinLiveHook() {
        Class<?> BluedUIHttpResponse = XposedHelpers.findClass("com.blued.android.framework.http.BluedUIHttpResponse", classLoader);
        XposedHelpers.findAndHookMethod("com.blued.android.module.live_china.utils.LiveRoomHttpUtils", classLoader, "a", BluedUIHttpResponse, long.class, java.lang.String.class, int.class, int.class, java.lang.String.class, new XC_MethodHook() {
            @Override
            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                super.beforeHookedMethod(param);
                SettingItem settingItem = SQLiteManagement.getInstance().getSettingByFunctionId(SettingsViewCreator.LIVE_JOIN_HIDE_HOOK);
                if (settingItem.isSwitchOn()) {
                    param.args[3] = 2;
                }
            }

            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                super.afterHookedMethod(param);
            }
        });
    }
}
