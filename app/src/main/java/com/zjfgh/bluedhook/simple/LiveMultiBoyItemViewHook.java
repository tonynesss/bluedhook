package com.zjfgh.bluedhook.simple;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedHelpers;

public class LiveMultiBoyItemViewHook {
    private static LiveMultiBoyItemViewHook instance;
    private final ClassLoader classLoader;

    private LiveMultiBoyItemViewHook() {
        this.classLoader = AppContainer.getInstance().getClassLoader();
        liveMultiBoyItemModelHook();
    }
    // 获取单例实例
    public static synchronized LiveMultiBoyItemViewHook getInstance() {
        if (instance == null) {
            instance = new LiveMultiBoyItemViewHook();
        }
        return instance;
    }
    public void liveMultiBoyItemModelHook(){
        XposedHelpers.findAndHookMethod("com.blued.android.module.live_china.view.LiveMultiBoyItemView", classLoader, "c", new XC_MethodHook() {
            @Override
            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                super.beforeHookedMethod(param);
                SettingItem settingItem = SQLiteManagement.getInstance().getSettingByFunctionId(SettingsViewCreator.PLAYING_ON_LIVE_BASE_MODE_FRAGMENT_HOOK);
                if (settingItem.isSwitchOn()){
                    Object liveMultiBoyItemView = param.thisObject;
                    Object liveMultiBoyItemModel = XposedHelpers.getObjectField(liveMultiBoyItemView, "e");
                    boolean isHide = (boolean) XposedHelpers.callMethod(liveMultiBoyItemModel, "getHide");
                    if (isHide) {
                        XposedHelpers.callMethod(liveMultiBoyItemModel, "setHide", false);
                        String name = (String) XposedHelpers.callMethod(liveMultiBoyItemModel, "getName");

                        // 1. 如果 name 不为空，并且以 "[隐]" 开头，则移除它
                        if (name != null && name.startsWith("[隐]")) {
                            name = name.substring(3); // 移除前3个字符（"[隐]" 占3个字符）
                        }
                        // 2. 重新添加 "[隐]" 前缀
                        XposedHelpers.callMethod(liveMultiBoyItemModel, "setName", "[隐]" + name);
                    }
                }
            }
            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                super.afterHookedMethod(param);
            }
        });
    }
}
