package com.zjfgh.bluedhook.simple;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.res.XmlResourceParser;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowInsetsController;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.alibaba.fastjson2.JSON;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class UserPopupWindow {
    private PopupWindow popupWindow;
    RecyclerView recyclerView;
    private UserListAdapter adapter;
    Button anchorStartButton;
    @SuppressLint("StaticFieldLeak")
    private static UserPopupWindow instance;
    private Handler handler;
    private Runnable checkRunnable;
    private int currentCheckIndex = 0;
    private boolean isChecking = false;
    // 保存原始状态
    private int originalStatusBarColor;
    private int originalNavigationBarColor;
    private int originalSystemUiVisibility;
    private boolean originalLightStatusBars;
    private boolean originalLightNavBars;
    private boolean hasSavedOriginalState = false;
    SQLiteManagement manger = SQLiteManagement.getInstance();
    public static UserPopupWindow getInstance() {
        if (instance == null) {
            instance = new UserPopupWindow();
        }
        return instance;
    }

    public void show(Context context) {
        if (popupWindow != null && popupWindow.isShowing()) {
            return;
        }
        Activity activity = (Activity) context;
        // 保存原始状态（仅在第一次或状态未保存时）
        if (!hasSavedOriginalState) {
            saveOriginalSystemBarState(activity);
            hasSavedOriginalState = true;
        }
        XmlResourceParser anchorMonitorLayoutXml = AppContainer.getInstance().getModuleRes().getLayout(R.layout.anchor_monitor_layout);
        View rootView = LayoutInflater.from(context).inflate(anchorMonitorLayoutXml,null,false);
        LinearLayout anchorMonitorList = rootView.findViewById(R.id.anchor_monitor_list);
        recyclerView = new RecyclerView(context);
        anchorMonitorList.addView(recyclerView);
        LinearLayout.LayoutParams recyclerParams = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT);
        recyclerView.setLayoutParams(recyclerParams);
        // 修改适配器初始化部分
        adapter = new UserListAdapter(context, user -> delAnchor(user.getUid(),user.getName()));
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        // 创建自定义高度的透明分割线
        DividerItemDecoration divider = new DividerItemDecoration(context, DividerItemDecoration.VERTICAL);
        // 创建带高度的透明Drawable
        GradientDrawable transparentDivider = new GradientDrawable();
        transparentDivider.setSize(0, 10); // 宽度为0，高度为10px
        transparentDivider.setColor(Color.TRANSPARENT);
        divider.setDrawable(transparentDivider);
        recyclerView.addItemDecoration(divider);
        recyclerView.setAdapter(adapter);
        // 创建按钮
        anchorStartButton = rootView.findViewById(R.id.anchor_start_button);
        LinearLayout.LayoutParams buttonParams = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        buttonParams.setMargins(20, 20, 20, 20);
        @SuppressLint("UseCompatLoadingForDrawables")
        Drawable anchorStartButtonBg = AppContainer.getInstance().getModuleRes().getDrawable(R.drawable.neon_button,null);
        anchorStartButton.setBackground(anchorStartButtonBg);
        anchorStartButton.setLayoutParams(buttonParams);
        if (isChecking){
            anchorStartButton.setText("停止检测");
        }else {
            anchorStartButton.setText("开始定时检测");
        }

        // 然后在show方法中完善按钮点击逻辑
        anchorStartButton.setOnClickListener(v -> {
            if (isChecking) {
                stopChecking();
                anchorStartButton.setText("开始定时检测");
            } else {
                startChecking();
                anchorStartButton.setText("停止检测");
            }
        });

        // 创建PopupWindow
        popupWindow = new PopupWindow(
                rootView,
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT,
                true
        );
        // 设置弹出窗口关闭时的监听器
        popupWindow.setOnDismissListener(() -> restoreOriginalSystemBarState(activity));
        popupWindow.setAnimationStyle(android.R.style.Animation_Dialog);
        popupWindow.showAtLocation(rootView, Gravity.CENTER, 0, 0);
        // 修改系统栏颜色
        updateSystemBarsForPopup(activity);
        // 观察数据变化
        UserDataManager.getInstance().getUserLiveData().observeForever(users -> {
            if (adapter != null) {
                adapter.submitList(users);
            }
        });
    }
    /**
     * 保存原始的系统栏状态
     */
    private void saveOriginalSystemBarState(Activity activity) {
        Window window = activity.getWindow();
        originalStatusBarColor = window.getStatusBarColor();
        originalNavigationBarColor = window.getNavigationBarColor();

        originalSystemUiVisibility = window.getDecorView().getSystemUiVisibility();
        originalLightStatusBars = (originalSystemUiVisibility & View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR) != 0;

        originalLightNavBars = (originalSystemUiVisibility & View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR) != 0;
    }

    private void restoreOriginalSystemBarState(Activity activity) {
        Window window = activity.getWindow();

        // 恢复颜色
        window.setStatusBarColor(originalStatusBarColor);
        window.setNavigationBarColor(originalNavigationBarColor);

        // 恢复系统UI标志
        // 清除所有可能影响状态栏的标志位
        int newVisibility = originalSystemUiVisibility;

        // 确保只恢复我们关心的标志位
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
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
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

    private void updateSystemBarsForPopup(Activity activity) {
        Window window = activity.getWindow();
        int newColor = Color.parseColor("#FF1A1A1A"); // 你的主题色

        // 设置新颜色
        window.setStatusBarColor(newColor);
        window.setNavigationBarColor(newColor);

        // 根据颜色亮度调整文字颜色
        boolean isLightColor = isColorLight(newColor);

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
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
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
    public void dismiss() {
        if (popupWindow != null && popupWindow.isShowing()) {
            // 恢复原始状态
            if (popupWindow.getContentView() != null) {
                restoreOriginalSystemBarState((Activity) popupWindow.getContentView().getContext());
            }
            popupWindow.dismiss();
            popupWindow = null;
        }
    }
    private void startChecking() {
        if (adapter == null || adapter.getCurrentList().isEmpty()) {
            Toast.makeText(popupWindow.getContentView().getContext(),
                    "用户列表为空", Toast.LENGTH_SHORT).show();
            return;
        }

        isChecking = true;
        currentCheckIndex = 0;
        handler = new Handler(Looper.getMainLooper());

        checkRunnable = new Runnable() {
            @Override
            public void run() {
                if (!isChecking || adapter == null) return;

                List<User> users = adapter.getCurrentList();
                if (currentCheckIndex >= users.size()) {
                    currentCheckIndex = 0; // 循环检测
                }

                User currentUser = users.get(currentCheckIndex);
                // 设置当前检测的用户
                adapter.setCurrentCheckingUid(currentUser.getUid());
                checkUserHomepage(currentUser);
                currentCheckIndex++;
                handler.postDelayed(this, 5000); // 10秒后执行下一次
            }
        };

        handler.post(checkRunnable);
    }

    private void stopChecking() {
        isChecking = false;
        anchorStartButton.setText("开始定时检测");
        if (handler != null && checkRunnable != null) {
            handler.removeCallbacks(checkRunnable);
        }
    }
    private void checkUserHomepage(User user) {
        String uid = user.getUid();
        Map<String, String> authMap = AuthManager.auHook(false, AppContainer.getInstance().getClassLoader(), AppContainer.getInstance().getBluedContext());
        NetworkManager.getInstance().getAsync(NetworkManager.getBluedUserBasicAPI(uid), authMap, new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                Log.e("BluedHook", "检测用户 " + user.getName() + " 失败: " + e.getMessage());
                // 检测完成后清除标记
                new Handler(Looper.getMainLooper()).post(() -> {
                    if (adapter != null && uid.equals(adapter.currentCheckingUid)) {
                        adapter.setCurrentCheckingUid(null);
                    }
                });
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                assert response.body() != null;
                String result = response.body().string();
                try {
                    JSONObject data = new JSONObject(result);
                    int code = data.getInt("code");
                    String message = data.getString("message");
                    if (code == 200){
                        JSONArray dataArr = data.getJSONArray("data");
                        JSONObject userObj = dataArr.getJSONObject(0);
                        String name = userObj.getString("name");
                        long live = userObj.getLong("live");
                        Log.i("BluedHook","检查用户：" + name + "(" + user.getName() + ") live: " + live);
                        User sqlUser = manger.getUserByUid(user.getUid());
                        if (live > 0){
                            if (Long.parseLong(sqlUser.getLive()) != live){
                                manger.updateUserLive(user.getUid(), live);
                            }
                            if (!sqlUser.isVoiceReminded() && sqlUser.isVoiceRemind()){
                                manger.updateUserVoiceReminded(uid,true);
                                VoiceTTS.getInstance(AppContainer.getInstance().getBluedContext()).speakAdd(user.getName() + " 正在 Blued 直播。");
                                Log.i("BluedHook",user.getName() + " 正在 Blued 直播。");
                            }
                        }else {
                            if (sqlUser.isVoiceReminded()){
                                manger.updateUserVoiceReminded(uid,false);
                                Log.i("BluedHook",user.getName() + " 直播已结束。");
                            }
                        }
                    }else {
                        Log.e("BluedHook", "检测用户 " + user.getName() + " 失败: " + code + "|" + message);
                    }
                    // 在UserPopupWindow中，替换原来的refreshUsers()
                    new Handler(Looper.getMainLooper()).post(() -> {
                        if (adapter != null && uid.equals(adapter.currentCheckingUid)) {
                            adapter.setCurrentCheckingUid(null);
                        }
                        UserDataManager.getInstance().getUserLiveData().observeForever(users -> {
                            if (adapter != null) {
                                // 局部更新差异项（需配合DiffUtil）
                                adapter.submitList(users, () -> {
                                    // 可选：滚动到当前位置
                                    //recyclerView.scrollToPosition(currentCheckIndex);
                                });
                            }
                        });
                    });

                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }

            }
        });
    }
    public void addAnchor(User user){
        // 执行本地删除操作
        boolean localAddSuccess = SQLiteManagement.getInstance().addOrUpdateUser(user);
        if(localAddSuccess){
            UserDataManager.getInstance().addUser(user);
            // 准备云端删除
            try {
                String bodyString = getString(user);
                Map<String, String> header = new HashMap<>();
                header.put("AirScript-Token", "5e56J7bsc45egjJUlfFM6C");
                NetworkManager.getInstance().postAsync(NetworkManager.getJinShanDocBluedUsersApi(), bodyString, header, new Callback() {
                    @Override
                    public void onFailure(@NonNull Call call, @NonNull IOException e) {
                        Log.e("BluedHook-UserPopupWindow","addAnchor方法-云端访问失败：" + e.getMessage());
                        ModuleTools.showBluedToast("用户 " + user.getName() + " 添加成功\n云端添加失败，云端访问失败！");
                    }

                    @Override
                    public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                        assert response.body() != null;
                        String result = response.body().string();
                        JinShanDocApiResponse apiResponse = JSON.parseObject(result, JinShanDocApiResponse.class);
                        String result2 = apiResponse.getData().getResult();
                        if (result2 == null || result2.equals("null")){
                            // 解析响应
                            Log.e("BluedHook-UserPopupWindow","addAnchor方法-云端返回结果为空，云端原始数据：" + result);
                            ModuleTools.showBluedToast("用户 " + user.getName() + " 添加成功\n云端添加失败，返回结果为空！",AppContainer.getInstance().getClassLoader());
                        }else {
                            ModuleTools.showBluedToast("用户 " + user.getName() + " 添加成功\n云端 " + result2,AppContainer.getInstance().getClassLoader());
                        }
                    }
                });
            }catch (JSONException e){
                Log.e("BluedHook-UserPopupWindow","addAnchor方法-构造JSON数据异常：" + e.getMessage());
                ModuleTools.showBluedToast("用户" + user.getName() + "添加成功\n云端 本地构造请求JSON数据异常",AppContainer.getInstance().getClassLoader());
            }

        }
    }

    @NonNull
    private static String getString(User user) throws JSONException {
        JSONObject jsonObj = new JSONObject();
        JSONObject context1 = new JSONObject();
        JSONObject argv = new JSONObject();
        jsonObj.put("context", context1);
        context1.put("argv", argv);
        argv.put("type", "addAnchor");
        JSONObject anchor = new JSONObject();
        anchor.put("name", user.getName());
        anchor.put("avatar", user.getAvatar());
        anchor.put("uid", user.getUid());
        anchor.put("live_id", user.getLive());
        anchor.put("union_uid", user.getUnion_uid());
        anchor.put("enc_uid",user.getEnc_uid());
        anchor.put("is_voice_reminder",0);
        anchor.put("is_force_reminder",0);
        anchor.put("is_live_join",0);
        anchor.put("is_download_avatar",0);
        argv.put("data",anchor);
        return jsonObj.toString();
    }

    public void delAnchor(String uid, String name){
        try {
            // 执行本地删除操作
            boolean localDeleteSuccess = SQLiteManagement.getInstance().deleteUser(uid);
            if(localDeleteSuccess){
                UserDataManager.getInstance().removeUser(uid);
            }
            // 准备云端删除
            JSONObject jsonObj = new JSONObject();
            JSONObject context1 = new JSONObject();
            JSONObject argv = new JSONObject();
            jsonObj.put("context", context1);
            context1.put("argv", argv);
            argv.put("type", "delAnchor");
            argv.put("uid", uid);
            String bodyString = jsonObj.toString();
            Map<String, String> header = new HashMap<>();
            header.put("AirScript-Token", "5e56J7bsc45egjJUlfFM6C");
            NetworkManager.getInstance().postAsync(NetworkManager.getJinShanDocBluedUsersApi(), bodyString, header, new Callback() {
                @Override
                public void onFailure(@NonNull Call call, @NonNull IOException e) {
                    // 云端删除失败处理
                    if (localDeleteSuccess){
                        ModuleTools.showBluedToast("用户 " + name + " 删除成功\n云端请求失败！", AppContainer.getInstance().getClassLoader());
                    }else {
                        ModuleTools.showBluedToast("用户 " + name + " 删除失败\n云端请求失败！", AppContainer.getInstance().getClassLoader());
                    }
                }
                @Override
                public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                    assert response.body() != null;
                    String result = response.body().string();
                    JinShanDocApiResponse apiResponse = JSON.parseObject(result, JinShanDocApiResponse.class);
                    String result2 = apiResponse.getData().getResult();
                    if (result2 == null || result2.equals("null")){
                        // 解析响应
                        result2 = "数据异常！";
                    }
                    if (localDeleteSuccess){
                        ModuleTools.showBluedToast("用户 " + name + " 删除成功\n云端 " + result2, AppContainer.getInstance().getClassLoader());
                    }else {
                        ModuleTools.showBluedToast("用户 " + name + " 删除失败\n云端请求失败！", AppContainer.getInstance().getClassLoader());
                    }
                }
            });
            // 停止检测如果正在检测被删除的用户
            if (isChecking && uid.equals(adapter.currentCheckingUid)) {
                stopChecking();
            }
        }catch (Exception e){
            ModuleTools.showBluedToast("用户 " + name + " 删除发生了异常！", AppContainer.getInstance().getClassLoader());
            Log.e("UserPopupWindow","removeUser方法异常：" + e);
        }
    }
} 