package com.zjfgh.bluedhook.simple;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.graphics.Color;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.speech.tts.TextToSpeech;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.util.Log;
import android.util.TypedValue;
import android.widget.Toast;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import de.robv.android.xposed.XposedHelpers;

public class ModuleTools {
    public static void showBluedToast(String text, ClassLoader classLoader) {
        if (classLoader != null) {
            Class<?> ToastUtils = XposedHelpers.findClass("com.blued.android.module.common.utils.ToastUtils", classLoader);
            XposedHelpers.callStaticMethod(ToastUtils, "b", text);
        }
    }

    public static void showBluedToast(String text) {
        ClassLoader classLoader = AppContainer.getInstance().getClassLoader();
        if (classLoader != null) {
            Class<?> ToastUtils = XposedHelpers.findClass("com.blued.android.module.common.utils.ToastUtils", classLoader);
            XposedHelpers.callStaticMethod(ToastUtils, "b", text);
        }
    }

    public static void showToast(final String text, final int toastLength) {
        new Handler(Looper.getMainLooper()).post(() -> Toast.makeText(AppContainer.getInstance().getBluedContext(), text, toastLength).show());
    }

    public static CharSequence charSequence(Pattern word, CharSequence charSequence, String str) {
        boolean z;
        if (TextUtils.isEmpty(charSequence)) {
            return charSequence;
        }
        SpannableStringBuilder spannableStringBuilder = null;
        Matcher matcher = word.matcher(charSequence);
        int i = 0;
        while (matcher.find()) {
            if (spannableStringBuilder == null) {
                spannableStringBuilder = new SpannableStringBuilder();
            }
            String group = matcher.group(1);
            spannableStringBuilder.append(charSequence.subSequence(i, matcher.start()));
            int length = spannableStringBuilder.length();
            assert group != null;
            int length2 = group.length() + length;
            spannableStringBuilder.append(group);
            try {
                Color.parseColor(str);
                z = true;
            } catch (Exception unused) {
                z = false;
            }
            spannableStringBuilder.setSpan(new ForegroundColorSpan(Color.parseColor(z ? str : "#ffef5f")), length, length2, 33);
            spannableStringBuilder.setSpan(new StyleSpan(1), length, length2, 33);
            i = matcher.end();
        }
        if (spannableStringBuilder != null && i < charSequence.length() - 1) {
            spannableStringBuilder.append(charSequence.subSequence(i, charSequence.length()));
        }
        return spannableStringBuilder != null ? spannableStringBuilder : charSequence;
    }

    public static int dpToPx(int dp) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_PX, dp, AppContainer.getInstance().getBluedContext().getResources().getDisplayMetrics());
    }

    public static String AesDecrypt(String urlCrypto) {
        Class<?> AesCryptoClass = XposedHelpers.findClass("com.blued.android.framework.utils.AesCrypto", AppContainer.getInstance().getClassLoader());
        String str = (String) XposedHelpers.callStaticMethod(AesCryptoClass, "e", urlCrypto);
        Log.i("BluedHook", "AesDecrypt解密：" + str);
        return str;
    }

    public static String getParamFromUrl(String url, String param) {
        return Uri.parse(url).getQueryParameter(param);
    }

    /**
     * 将HTML颜色代码转换为Android ARGB整数
     *
     * @param htmlColor 支持格式：#RGB、#ARGB、#RRGGBB、#AARRGGBB
     * @return ARGB颜色值（如 0xFFAABBCC）
     * @throws IllegalArgumentException 颜色格式错误时抛出
     */
    public static int htmlToArgb(String htmlColor) {
        if (htmlColor == null || !htmlColor.startsWith("#")) {
            throw new IllegalArgumentException("Invalid HTML color format");
        }

        String color = htmlColor.substring(1); // 去掉#

        // 处理不同长度
        if (color.length() == 3) { // #RGB → #FFRRGGBB
            color = "FF" +
                    color.charAt(0) + color.charAt(0) +
                    color.charAt(1) + color.charAt(1) +
                    color.charAt(2) + color.charAt(2);
        } else if (color.length() == 4) { // #ARGB → #AARRGGBB
            color = "" +
                    color.charAt(0) + color.charAt(0) +
                    color.charAt(1) + color.charAt(1) +
                    color.charAt(2) + color.charAt(2) +
                    color.charAt(3) + color.charAt(3);
        } else if (color.length() == 6) { // #RRGGBB → #FFRRGGBB
            color = "FF" + color;
        } else if (color.length() != 8) { // #AARRGGBB
            throw new IllegalArgumentException("Invalid HTML color format");
        }

        // 转换为long类型处理（避免负数问题）
        long longValue = Long.parseLong(color, 16);

        // 处理透明度（如果原始格式是#RRGGBB，我们添加了FF作为alpha）
        return (int) longValue;
    }

    public static void writeFile(String fileName, String content) {
        Date date = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy年MM月dd日 HH:mm:ss", Locale.getDefault());
        String currentTime = sdf.format(date);
        SimpleDateFormat ymd = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        String currentTMD = ymd.format(date);
        // 2. 使用目标应用的存储目录（无需额外权限）
        File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS) + "/小蓝抽奖记录/" + currentTMD + "/" + fileName);
        Log.d("BluedHook", "写出文件路径: " + file.getPath());
        String output = currentTime + " " + content + "\n";
        try {
            if (!file.getParentFile().exists()) {
                file.getParentFile().mkdirs();
            }
            FileWriter fw = new FileWriter(file, true);
            BufferedWriter bw = new BufferedWriter(fw);
            bw.write(output);
            bw.close();
        } catch (IOException e) {
            Log.e("BluedHook", "写出文件失败" + file.getPath(), e);
        }
    }

    public static void copyToClipboard(Context context, String label, String text) {
        if (text == null) {
            ModuleTools.showToast(label + "复制失败", 1);
            return;
        }
        ClipboardManager clipboard = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clip = ClipData.newPlainText(label, text);
        clipboard.setPrimaryClip(clip);
        ModuleTools.showToast(label + "复制成功", 1);
    }
}
