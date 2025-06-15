package com.zjfgh.bluedhook.simple;

import de.robv.android.xposed.XposedHelpers;

public class LiveMsgSendManagerHook {
    public static void startSendMsg(String msg) {
        Class<?> LiveMsgSendManagerClass = XposedHelpers.findClass("com.blued.android.module.live_china.msg.LiveMsgSendManager", AppContainer.getInstance().getClassLoader());
        Object msgSendManager = XposedHelpers.callStaticMethod(LiveMsgSendManagerClass, "a");
        XposedHelpers.callMethod(msgSendManager, "a", msg);
    }
}
