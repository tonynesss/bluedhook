package com.zjfgh.bluedhook.simple;


import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.res.XModuleResources;
import android.graphics.Color;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import org.java_websocket.WebSocket;
import org.json.JSONObject;

import java.util.Objects;

import de.robv.android.xposed.IXposedHookInitPackageResources;
import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.IXposedHookZygoteInit;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_InitPackageResources;
import de.robv.android.xposed.callbacks.XC_LayoutInflated;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

public class BluedHook implements IXposedHookLoadPackage, IXposedHookInitPackageResources, IXposedHookZygoteInit {
    public static WSServerManager wsServerManager;

    @Override
    public void handleLoadPackage(XC_LoadPackage.LoadPackageParam param) {
        if (param.packageName.equals("com.soft.blued")) {
            XposedHelpers.findAndHookMethod("com.soft.blued.StubWrapperProxyApplication", param.classLoader, "initProxyApplication", Context.class, new XC_MethodHook() {
                @Override
                protected void beforeHookedMethod(XC_MethodHook.MethodHookParam param) throws Throwable {
                    super.beforeHookedMethod(param);
                }

                @Override
                protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                    super.afterHookedMethod(param);
                    Context bluedContext = (Context) param.args[0];
                    AppContainer.getInstance().setBluedContext(bluedContext);
                    AppContainer.getInstance().setClassLoader(bluedContext.getClassLoader());
                    Toast.makeText(bluedContext, "外挂成功！", Toast.LENGTH_LONG).show();
                    try {
                        VoiceTTS.getInstance(bluedContext);
                    } catch (Exception e) {
                        Log.e("BluedHook", "VoiceTTS模块异常：\n" +
                                e);
                    }
                    NetworkManager.getInstance();
                    UserInfoFragmentNewHook.getInstance(bluedContext, AppContainer.getInstance().getModuleRes());
                    LiveHook.getInstance(bluedContext);
                    PlayingOnLiveBaseModeFragmentHook.getInstance(bluedContext, AppContainer.getInstance().getModuleRes());
                    FragmentMineNewBindingHook.getInstance(bluedContext, AppContainer.getInstance().getModuleRes());
                    LiveMultiBoyItemViewHook.getInstance();
                    ChatHook.getInstance(bluedContext, AppContainer.getInstance().getModuleRes());
                    NearbyPeopleFragment_ViewBindingHook.getInstance(AppContainer.getInstance().getBluedContext(), AppContainer.getInstance().getClassLoader());
                    HornViewNewHook.autoHornViewNew();
                    wsServerManager = new WSServerManager(new WSServerManager.WSServerListener() {
                        @Override
                        public void onServerStarted(int port) {
                            ModuleTools.showBluedToast("WS服务已启动在" + port + "端口上");
                        }

                        @Override
                        public void onServerStopped() {
                            ModuleTools.showBluedToast("WS服务已停止");
                        }

                        @Override
                        public void onServerError(String error) {
                            ModuleTools.showBluedToast("WS服务发生了错误：" + error);
                        }

                        @Override
                        public void onClientConnected(String address) {

                        }

                        @Override
                        public void onClientDisconnected(String address) {

                        }

                        @Override
                        public void onMessageReceived(WebSocket conn, String message) {
                            if (message.equals("同步数据")) {
                                try {
                                    // 1. 构建基础响应结构
                                    JSONObject response = new JSONObject();
                                    response.put("msgType", 1995);

                                    // 2. 构建msgExtra部分
                                    JSONObject msgExtra = new JSONObject();
                                    msgExtra.put("msgType", "lotteryRecords");

                                    // 3. 获取并转换文件数据为JSON
                                    JSONObject recordsData = new FileToJsonConverter().convertFilesToJson();
                                    msgExtra.put("msgExtra", recordsData);

                                    // 4. 将msgExtra放入主响应
                                    response.put("msgExtra", msgExtra);
                                    // 5. 广播消息
                                    String jsonResponse = response.toString();
                                    Log.d("WebSocketServer", "Broadcasting records: " + jsonResponse);
                                    wsServerManager.broadcastMessage(jsonResponse);

                                } catch (Exception e) {
                                    Log.e("WebSocketServer", "Error processing sync request", e);
                                }
                            }
                        }
                    });
                }
            });
        }
    }

    @Override
    public void handleInitPackageResources(XC_InitPackageResources.InitPackageResourcesParam resParam) {
        if (resParam.packageName.equals("com.soft.blued")) {
            String modulePath = AppContainer.getInstance().getModulePath();
            XModuleResources moduleRes = XModuleResources.createInstance(modulePath, resParam.res);
            AppContainer.getInstance().setModuleRes(moduleRes);
            resParam.res.hookLayout("com.soft.blued", "layout", "fragment_settings", new XC_LayoutInflated() {
                @SuppressLint({"ResourceType", "SetTextI18n"})
                @Override
                public void handleLayoutInflated(LayoutInflatedParam liParam) {
                    LayoutInflater inflater = (LayoutInflater) liParam.view.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                    Context bluedContext = AppContainer.getInstance().getBluedContext();
                    int scrollView1ID = bluedContext.getResources().getIdentifier("scrollView1", "id", bluedContext.getPackageName());
                    ScrollView scrollView = liParam.view.findViewById(scrollView1ID);
                    LinearLayout scrollLinearLayout = (LinearLayout) scrollView.getChildAt(0);
                    LinearLayout mySettingsLayoutAu = (LinearLayout) inflater.inflate(moduleRes.getLayout(R.layout.module_settings_layout), null, false);
                    TextView auCopyTitleTv = mySettingsLayoutAu.findViewById(R.id.settings_name);
                    auCopyTitleTv.setText("复制授权信息(请勿随意泄漏)");
                    mySettingsLayoutAu.setOnClickListener(v -> AuthManager.auHook(true, AppContainer.getInstance().getClassLoader(), bluedContext));
                    LinearLayout moduleSettingsLayout = (LinearLayout) inflater.inflate(moduleRes.getLayout(R.layout.module_settings_layout), null, false);
                    TextView moduleSettingsTitleTv = moduleSettingsLayout.findViewById(R.id.settings_name);
                    moduleSettingsTitleTv.setText("外挂模块设置");
                    moduleSettingsLayout.setOnClickListener(view -> {
                        AlertDialog dialog = getAlertDialog(liParam);
                        Objects.requireNonNull(dialog.getWindow()).setGravity(Gravity.CENTER);
                        dialog.getWindow().setLayout(100, 300);
                        dialog.setOnShowListener(dialogInterface -> {
                            View parentView = dialog.getWindow().getDecorView();
                            parentView.setBackgroundColor(Color.parseColor("#F7F6F7")); // 自定义背景色
                        });
                        dialog.show();

                    });
                    scrollLinearLayout.addView(mySettingsLayoutAu, 0);
                    scrollLinearLayout.addView(moduleSettingsLayout, 1);
                }

                private AlertDialog getAlertDialog(LayoutInflatedParam liParam) {
                    SettingsViewCreator creator = new SettingsViewCreator(liParam.view.getContext());
                    View settingsView = creator.createSettingsView();
                    creator.setOnSwitchCheckedChangeListener((functionId, isChecked) -> {
                        if (functionId == SettingsViewCreator.ANCHOR_MONITOR_LIVE_HOOK) {
                            LiveHook.getInstance(AppContainer.getInstance().getBluedContext()).setAnchorMonitorIvVisibility(isChecked);
                        }
                    });
                    AlertDialog.Builder builder = new AlertDialog.Builder(liParam.view.getContext());
                    builder.setView(settingsView);
                    return builder.create();
                }
            });
        }
    }


    @Override
    public void initZygote(StartupParam startupParam) {
        AppContainer.getInstance().setModulePath(startupParam.modulePath);
    }
}