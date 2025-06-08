package com.zjfgh.bluedhook.simple;

import static com.zjfgh.bluedhook.simple.ModuleTools.charSequence;

import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.text.SpannableString;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.regex.Pattern;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedHelpers;

public class HornViewNewHook {
    public static void autoHornViewNew() {
        Class<?> LiveHornModelNew = XposedHelpers.findClass("com.blued.android.module.live_china.model.LiveHornModelNew", AppContainer.getInstance().getClassLoader());
        XposedHelpers.findAndHookMethod("com.blued.android.module.live_china.view.HornViewNew", AppContainer.getInstance().getClassLoader(), "a", LiveHornModelNew, new XC_MethodHook() {
            @Override
            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                super.beforeHookedMethod(param);

            }

            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                super.afterHookedMethod(param);
                String outFileName = "抽奖.txt";
                //拿到liveHornModelNew对象
                Object liveHornModelNew = param.args[0];
                String contents = (String) XposedHelpers.getObjectField(liveHornModelNew, "contents");
                String highlight_color = (String) XposedHelpers.getObjectField(liveHornModelNew, "highlight_color");
                Pattern word = Pattern.compile("@\\(word:([\\s\\S]*?)\\)");
                CharSequence a2 = charSequence(word, new SpannableString(contents), highlight_color);
            }
        });
    }
}
