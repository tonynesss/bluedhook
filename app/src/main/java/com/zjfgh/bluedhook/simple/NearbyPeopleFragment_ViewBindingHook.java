package com.zjfgh.bluedhook.simple;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.LinkedList;
import java.util.Queue;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedHelpers;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class NearbyPeopleFragment_ViewBindingHook {
    private static NearbyPeopleFragment_ViewBindingHook instance;
    private final Context context;
    private final ClassLoader classLoader;
    private final Handler handler;
    private final Queue<Long> userQueue;

    // 状态枚举
    private enum State {
        IDLE,       // 空闲状态
        RUNNING,    // 运行中
        PAUSED,     // 已暂停
        COMPLETED    // 已完成
    }

    private State currentState = State.IDLE;
    private int currentPage = 0;
    private final int maxPages = 6;
    private final long pageDelay = 10000; // 页间延迟10秒
    private final long userDelay = 2000;  // 用户间延迟2秒
    protected final int countTotal = maxPages * 60 - 60;
    protected int currentCount;
    protected TextView tvFindPeople;
    protected TextView tvCurrentUser;

    // 私有构造方法
    private NearbyPeopleFragment_ViewBindingHook(Context context, ClassLoader classLoader) {
        this.context = context;
        this.classLoader = classLoader;
        this.handler = new Handler();
        this.userQueue = new LinkedList<>();
        initHook();
    }

    // 获取单例
    public static synchronized NearbyPeopleFragment_ViewBindingHook getInstance(Context context, ClassLoader classLoader) {
        if (instance == null) {
            instance = new NearbyPeopleFragment_ViewBindingHook(context, classLoader);
        }
        return instance;
    }

    public void initHook() {
        Class<?> NearbyPeopleFragment = XposedHelpers.findClass(
                "com.soft.blued.ui.find.fragment.NearbyPeopleFragment",
                classLoader);

        XposedHelpers.findAndHookConstructor(
                "com.soft.blued.ui.find.fragment.NearbyPeopleFragment_ViewBinding",
                classLoader,
                NearbyPeopleFragment,
                View.class,
                new XC_MethodHook() {
                    @SuppressLint("UseCompatLoadingForDrawables")
                    @Override
                    protected void beforeHookedMethod(MethodHookParam param) {
                        LinearLayout layoutSort = getLayoutSortFromParam(param);
                        if (layoutSort == null) {
                            Log.e("BluedHook", "Failed to find layoutSort");
                            return;
                        }

                        Log.e("BluedHook", "layoutSort: " + layoutSort);
                        TagLayout tagLayout = new TagLayout(context);
                        tagLayout.isFirstMargin(true);
                        tagLayout.setMarginStart(15);
                        tvFindPeople = tagLayout.addTextView(
                                "寻找目标",
                                12,
                                AppContainer.getInstance().getModuleRes().getDrawable(R.drawable.bg_rounded, null));
                        tvFindPeople.setOnClickListener(v -> toggleProcess());
                        tagLayout.setMarginStart(5);
                        tvCurrentUser = tagLayout.addTextView("等待访问", 12, AppContainer.getInstance().getModuleRes().getDrawable(R.drawable.bg_orange, null));
                        layoutSort.addView(tagLayout, 0);
                    }
                });
    }

    /**
     * 从MethodHookParam中提取layoutSort视图
     */
    private LinearLayout getLayoutSortFromParam(XC_MethodHook.MethodHookParam param) {
        try {
            View view = (View) param.args[1];
            Class<?> Utils = XposedHelpers.findClass(
                    "butterknife.internal.Utils",
                    classLoader);

            return (LinearLayout) XposedHelpers.callStaticMethod(
                    Utils,
                    "a",
                    view,
                    0x7f0a1f6e,
                    "field 'layoutSort'");
        } catch (Exception e) {
            Log.e("BluedHook", "Error getting layoutSort", e);
            return null;
        }
    }

    // 切换处理状态
    private synchronized void toggleProcess() {
        switch (currentState) {
            case IDLE:
                startProcess();
                break;
            case RUNNING:
                pauseProcess();
                break;
            case PAUSED:
                resumeProcess();
                break;
            case COMPLETED:
                resetProcess();
                startProcess();
                break;
        }
    }

    // 开始处理
    private void startProcess() {
        tvFindPeople.setText("用户访问中...");
        Log.d("BluedHook", "用户访问中...");
        currentState = State.RUNNING;
        currentPage = 0;
        currentCount = 0;
        userQueue.clear();
        startPageRequest();
    }

    // 暂停处理
    private void pauseProcess() {
        tvFindPeople.setText("暂停寻找");
        Log.d("BluedHook", "暂停处理");
        currentState = State.PAUSED;
        handler.removeCallbacksAndMessages(null); // 移除所有回调
    }

    // 恢复处理
    private void resumeProcess() {
        tvFindPeople.setText("访问用户中...");
        Log.d("BluedHook", "恢复处理");
        currentState = State.RUNNING;
        if (!userQueue.isEmpty()) {
            processUserQueue();
        } else {
            startPageRequest();
        }
    }

    // 重置处理
    private void resetProcess() {
        Log.d("BluedHook", "重置处理");
        handler.removeCallbacksAndMessages(null);
        currentPage = 0;
        currentCount = 0;
        userQueue.clear();
    }

    // 开始分页请求
    // 修改后的startPageRequest方法
    private void startPageRequest() {
        if (currentPage >= maxPages) {
            Log.d("BluedHook", "所有分页已加载完成");
            currentState = State.COMPLETED;
            tvFindPeople.setText("访问完成");
            return;
        }

        int pageSize = 60;
        int offset = currentPage * pageSize;

        NetworkManager.getInstance().getAsync(
                NetworkManager.getBluedUserConnTypeApi(offset),
                AuthManager.auHook(false, classLoader, context),
                new Callback() {
                    @Override
                    public void onFailure(@NonNull Call call, @NonNull IOException e) {
                        Log.e("BluedHook", "分页请求失败: " + e.getMessage());
                        if (currentState == State.RUNNING) {
                            handler.postDelayed(NearbyPeopleFragment_ViewBindingHook.this::startPageRequest, pageDelay);
                        }
                    }

                    @Override
                    public void onResponse(@NonNull Call call, @NonNull Response response) {
                        try {
                            assert response.body() != null;
                            JSONObject json = new JSONObject(response.body().string());
                            JSONArray data = json.getJSONArray("data");

                            // 清空队列并添加新页面的用户
                            userQueue.clear();
                            for (int i = 0; i < data.length(); i++) {
                                long uid = data.getJSONObject(i).getLong("uid");
                                String name = data.getJSONObject(i).getString("name");
                                double distance = data.getJSONObject(i).getDouble("distance");
                                Log.d("BluedHook", currentPage + "|" + uid + "|" + name + "|" + distance + "km");
                                userQueue.add(uid);
                            }

                            if (currentState == State.RUNNING && !userQueue.isEmpty()) {
                                // 开始处理当前页用户
                                processUserQueue();
                            } else if (userQueue.isEmpty()) {
                                // 当前页无用户，直接请求下一页
                                currentPage++;
                                handler.postDelayed(NearbyPeopleFragment_ViewBindingHook.this::startPageRequest, pageDelay);
                            }

                        } catch (Exception e) {
                            Log.e("BluedHook", "解析错误", e);
                            if (currentState == State.RUNNING) {
                                handler.postDelayed(NearbyPeopleFragment_ViewBindingHook.this::startPageRequest, pageDelay);
                            }
                        }
                    }
                });
    }

    // 处理用户队列
    // 修改后的processUserQueue方法
    private void processUserQueue() {
        if (currentState != State.RUNNING || userQueue.isEmpty()) {
            // 当前页处理完成，请求下一页
            if (currentState == State.RUNNING) {
                currentPage++;
                handler.postDelayed(this::startPageRequest, pageDelay);
            }
            return;
        }

        Long uid = userQueue.poll();
        if (uid == null) {
            return;
        }

        NetworkManager.getInstance().getAsync(
                NetworkManager.getBluedUserAPI(String.valueOf(uid)),
                AuthManager.auHook(false, classLoader, context),
                new Callback() {
                    @Override
                    public void onFailure(@NonNull Call call, @NonNull IOException e) {
                        if (currentState == State.RUNNING) {
                            handler.postDelayed(NearbyPeopleFragment_ViewBindingHook.this::processUserQueue, userDelay);
                        }
                    }

                    @SuppressLint("SetTextI18n")
                    @Override
                    public void onResponse(@NonNull Call call, @NonNull Response response) {
                        try {
                            assert response.body() != null;
                            JSONObject json = new JSONObject(response.body().string());
                            JSONArray data = json.getJSONArray("data");
                            long uid = data.getJSONObject(0).getLong("uid");
                            String name = data.getJSONObject(0).getString("name");
                            String location = data.getJSONObject(0).getString("location");
                            currentCount++;
                            tvCurrentUser.post(() ->
                                    tvCurrentUser.setText("访问 " + name + " 成功" + "-" + location + "(" + currentCount + "/" + countTotal + ")")
                            );
                            Log.d("BluedHook", uid + "|" + name + "|" + location + "km|访问结束");
                        } catch (Exception e) {
                            Log.e("BluedHook", "用户详情解析错误", e);
                        } finally {
                            if (currentState == State.RUNNING) {
                                // 无论成功失败，继续处理队列中的下一个用户
                                handler.postDelayed(NearbyPeopleFragment_ViewBindingHook.this::processUserQueue, userDelay);
                            }
                        }
                    }
                });
    }
}