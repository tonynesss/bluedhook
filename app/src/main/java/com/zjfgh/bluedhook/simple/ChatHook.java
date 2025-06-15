package com.zjfgh.bluedhook.simple;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.XModuleResources;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONObject;

import java.lang.ref.WeakReference;
import java.util.List;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedHelpers;

public class ChatHook {
    private final ClassLoader classLoader;
    private static ChatHook instance;
    private final WeakReference<Context> contextRef;
    private final XModuleResources modRes;

    private ChatHook(Context context, XModuleResources modRes) {
        this.contextRef = new WeakReference<>(context);
        this.classLoader = context.getClassLoader();
        this.modRes = modRes;
        messageRecallHook();
        snapChatHook();
        chatHelperV4MdHook();
        chatReadedHook();
        chatProtectScreenshotHook();
        hookMsgChattingTitle();
    }

    // 获取单例实例
    public static synchronized ChatHook getInstance(Context context, XModuleResources modRes) {
        if (instance == null) {
            instance = new ChatHook(context, modRes);
        }
        return instance;
    }

    public void messageRecallHook() {
        Class<?> PushMsgPackage = XposedHelpers.findClass("com.blued.android.chat.core.pack.PushMsgPackage", classLoader);
        XposedHelpers.findAndHookMethod("com.blued.android.chat.core.worker.chat.Chat", classLoader, "receiveOrderMessage",
                PushMsgPackage, new XC_MethodHook() {
                    @Override
                    protected void beforeHookedMethod(MethodHookParam param) {
                        Object pushMsgPackage = param.args[0];
                        short msgType = XposedHelpers.getShortField(pushMsgPackage, "msgType");
                        if (msgType == 55) {
                            // 获取原始消息ID和会话信息
                            long msgId = XposedHelpers.getLongField(pushMsgPackage, "msgId");
                            short sessionType = XposedHelpers.getShortField(pushMsgPackage, "sessionType");
                            long sessionId = XposedHelpers.getLongField(pushMsgPackage, "sessionId");
                            Class<?> ChatManager = XposedHelpers.findClass("com.blued.android.chat.ChatManager", classLoader);
                            Object dbOperImpl = XposedHelpers.getStaticObjectField(ChatManager, "dbOperImpl");
                            // 获取原始消息对象
                            Object originalMsg = XposedHelpers.callMethod(
                                    dbOperImpl,
                                    "findMsgData",
                                    sessionType,
                                    sessionId,
                                    msgId,
                                    0L
                            );
                            if (originalMsg != null) {
                                // 获取原始消息类型
                                short originalType = XposedHelpers.getShortField(originalMsg, "msgType");
                                // 获取原始消息发送者昵称
                                String originalNickName = (String) XposedHelpers.getObjectField(originalMsg, "fromNickName");
                                // 获取原始消息内容
                                String originalContent = (String) XposedHelpers.getObjectField(originalMsg, "msgContent");
                                if (originalType == 55) {
                                    Log.e("BluedHook", "原始消息已被成功撤回，无法恢复。");
                                    return;
                                }
                                // 同时修改内存和数据库中的消息类型
                                XposedHelpers.setShortField(pushMsgPackage, "msgType", originalType);
                                XposedHelpers.setShortField(originalMsg, "msgType", originalType);
                                XposedHelpers.callMethod(dbOperImpl, "updateChattingModel", originalMsg);
                                // 处理不同类型的闪消息
                                switch (originalType) {
                                    case 1:
                                        ModuleTools.showBluedToast("[" + originalNickName + "]撤回了消息：\n" + originalContent);
                                        break;
                                    case 2:
                                        ModuleTools.showBluedToast("[" + originalNickName + "]撤回了图片");
                                        break;
                                    case 5:
                                        ModuleTools.showBluedToast("[" + originalNickName + "]撤回了视频");
                                        break;
                                    case 24: // 闪照
                                        ModuleTools.showBluedToast("[" + originalNickName + "]撤回了闪照");
                                        break;
                                    case 25: // 闪照视频
                                        ModuleTools.showBluedToast("[" + originalNickName + "]撤回了闪照视频");
                                        break;
                                }
                            }
                        }
                        param.setResult(null);
                    }
                });
    }

    public void chatHelperV4MdHook() {
        Class<?> ChattingModel = XposedHelpers.findClass("com.blued.android.chat.model.ChattingModel", classLoader);
        XposedHelpers.findAndHookMethod("com.soft.blued.ui.msg.controller.tools.ChatHelperV4", classLoader, "b", android.content.Context.class, ChattingModel, new XC_MethodHook() {
            @Override
            protected void beforeHookedMethod(XC_MethodHook.MethodHookParam param) throws Throwable {
                super.beforeHookedMethod(param);
                handleChatMessage(param, "b");
            }

            @Override
            protected void afterHookedMethod(XC_MethodHook.MethodHookParam param) throws Throwable {
                super.afterHookedMethod(param);
            }
        });
        XposedHelpers.findAndHookMethod("com.soft.blued.ui.msg.controller.tools.ChatHelperV4", classLoader, "c", android.content.Context.class, ChattingModel, new XC_MethodHook() {
            @Override
            protected void beforeHookedMethod(XC_MethodHook.MethodHookParam param) throws Throwable {
                super.beforeHookedMethod(param);
                handleChatMessage(param, "c");
            }

            @Override
            protected void afterHookedMethod(XC_MethodHook.MethodHookParam param) throws Throwable {
                super.afterHookedMethod(param);
            }
        });
        XposedHelpers.findAndHookMethod("com.soft.blued.ui.msg.controller.tools.ChatHelperV4", classLoader, "d", android.content.Context.class, ChattingModel, new XC_MethodHook() {
            @Override
            protected void beforeHookedMethod(XC_MethodHook.MethodHookParam param) throws Throwable {
                super.beforeHookedMethod(param);
                handleChatMessage(param, "d");
            }

            @Override
            protected void afterHookedMethod(XC_MethodHook.MethodHookParam param) throws Throwable {
                super.afterHookedMethod(param);
            }
        });
        XposedHelpers.findAndHookMethod("com.soft.blued.ui.msg.controller.tools.ChatHelperV4", classLoader, "e", android.content.Context.class, ChattingModel, new XC_MethodHook() {
            @Override
            protected void beforeHookedMethod(XC_MethodHook.MethodHookParam param) throws Throwable {
                super.beforeHookedMethod(param);
                handleChatMessage(param, "e");
            }

            @Override
            protected void afterHookedMethod(XC_MethodHook.MethodHookParam param) throws Throwable {
                super.afterHookedMethod(param);
            }
        });
        XposedHelpers.findAndHookMethod("com.soft.blued.ui.msg.controller.tools.ChatHelperV4", classLoader, "f", android.content.Context.class, ChattingModel, new XC_MethodHook() {
            @Override
            protected void beforeHookedMethod(XC_MethodHook.MethodHookParam param) throws Throwable {
                super.beforeHookedMethod(param);
                handleChatMessage(param, "f");
            }

            @Override
            protected void afterHookedMethod(XC_MethodHook.MethodHookParam param) throws Throwable {
                super.afterHookedMethod(param);
            }
        });

        XposedHelpers.findAndHookMethod("com.soft.blued.ui.msg.presenter.MsgChattingPresent", classLoader, "c", ChattingModel, new XC_MethodHook() {
            @Override
            protected void beforeHookedMethod(XC_MethodHook.MethodHookParam param) throws Throwable {
                super.beforeHookedMethod(param);
                Log.i("BluedHook", "开始发送消息" + param.args[0]);
            }

            @Override
            protected void afterHookedMethod(XC_MethodHook.MethodHookParam param) throws Throwable {
                super.afterHookedMethod(param);
            }
        });
    }

    public void snapChatHook() {
        try {
            XposedHelpers.findAndHookMethod(
                    "com.soft.blued.ui.msg.presenter.MsgChattingPresent",
                    classLoader,
                    "F",
                    new XC_MethodHook() {
                        @Override
                        protected void beforeHookedMethod(MethodHookParam param) {
                            getTvRecallMsg().setVisibility(View.VISIBLE);
                            handleFlashMessages(param);
                        }
                    });
        } catch (Throwable e) {
            Log.e("BluedHook", "Hook MsgChattingPresent.E()方法失败", e);
        }
    }

    /**
     * 处理闪照和闪照视频消息
     */
    private void handleFlashMessages(XC_MethodHook.MethodHookParam param) {
        try {
            Object thisObject = param.thisObject;
            Object t = XposedHelpers.getObjectField(thisObject, "t");
            Object E = XposedHelpers.callMethod(t, "E");
            Object a = XposedHelpers.callMethod(E, "a");
            if (!(a instanceof List)) {
                return;
            }

            for (Object msg : (List<?>) a) {
                processSingleMessage(msg);
            }
        } catch (Throwable e) {
            Log.e("BluedHook", "处理闪照消息时出错", e);
        }
    }

    /**
     * 处理单条消息
     */
    private void processSingleMessage(Object msgObj) {
        try {
            // 获取消息类型和内容
            short msgType = XposedHelpers.getShortField(msgObj, "msgType");
            String msgContent = (String) XposedHelpers.getObjectField(msgObj, "msgContent");
            Log.i("BluedHook", "processSingleMessage: \n" + msgType + ", 原始内容: " + msgContent);
            Class<?> fieldType = XposedHelpers.findField(msgObj.getClass(), "msgType").getType();
            // 检查字段类型是否为short
            if (!(fieldType == short.class || fieldType == Short.class)) {
                return;
            }
            boolean isFromSelf = (boolean) XposedHelpers.callMethod(msgObj, "isFromSelf");
            if (isFromSelf) {
                Log.i("BluedHook", "消息来自自己，跳过处理");
                return;
            }
            // 处理不同类型的闪图消息
            switch (msgType) {
                case 24: // 闪照
                    convertFlashMessage(msgObj, (short) 2, msgContent, "照片");
                    break;
                case 25: // 闪照视频
                    convertFlashMessage(msgObj, (short) 5, msgContent, "视频");
                    break;
            }
        } catch (Throwable e) {
            Log.e("BluedHook", "处理单条消息时出错", e);
        }
    }

    /**
     * 转换闪消息为普通消息
     *
     * @param msgObj           消息对象s
     * @param newType          转换后的消息类型
     * @param encryptedContent 加密的内容
     * @param typeName         类型名称(用于日志)
     */
    private void convertFlashMessage(Object msgObj, short newType, String encryptedContent, String typeName) {
        try {
            // 转换消息类型
            short msgType = XposedHelpers.getShortField(msgObj, "msgType");

            if (msgType == 55) {
                XposedHelpers.setShortField(msgObj, "msgType", newType);
                XposedHelpers.setAdditionalInstanceField(msgObj, "oldMsgType", msgType);
            } else {
                XposedHelpers.setShortField(msgObj, "msgType", newType);
                // 新增字段以存储原始消息类型
                XposedHelpers.setAdditionalInstanceField(msgObj, "oldMsgType", msgType);
                // 解密内容
                String decryptedContent = ModuleTools.AesDecrypt(encryptedContent);
                XposedHelpers.setObjectField(msgObj, "msgContent", decryptedContent);
                Log.i("BluedHook", "已转换闪" + typeName + "为普通" + typeName + ": " + decryptedContent);
            }

        } catch (Throwable e) {
            Log.e("BluedHook", "转换闪" + typeName + "失败", e);
        }
    }

    public static class ChatContent {
        public int msgType;
        public String fromNickName;
        public String extraMsg;
        public String msgContent;
        public long sessionId;
        public long fromId;
        public String fromAvatar;
    }

    // 创建统一的处理方法
    private void handleChatMessage(XC_MethodHook.MethodHookParam param, String methodTag) {
        // 参数校验
        if (param.args == null || param.args.length < 2) {
            Log.e("BluedHook", methodTag + "-参数无效");
            return;
        }
        try {
            Object chattingModel = param.args[1];
            // 使用一次性反射获取所有字段
            ChatContent chatContent = new ChatContent();
            chatContent.msgType = XposedHelpers.getIntField(chattingModel, "msgType");
            chatContent.fromNickName = (String) XposedHelpers.getObjectField(chattingModel, "fromNickName");
            chatContent.extraMsg = (String) XposedHelpers.callMethod(chattingModel, "getMsgExtra");
            chatContent.msgContent = (String) XposedHelpers.getObjectField(chattingModel, "msgContent");
            chatContent.sessionId = XposedHelpers.getLongField(chattingModel, "sessionId");
            chatContent.fromId = XposedHelpers.getLongField(chattingModel, "fromId");
            if (chatContent.extraMsg.isEmpty()) {
                try {
                    JSONObject json = new JSONObject(chatContent.extraMsg);
                    // 检查字段是否存在
                    boolean hasCustomPushContent = json.has("custom_push_content");
                    boolean hasLid = json.has("lid");
                    Log.i("BluedHook", "ChatHook\n" +
                            "handleChatMessage\n" +
                            "custom_push_content 存在: " + hasCustomPushContent + "\n" +
                            "lid 存在: " + hasLid);
                    // 如果字段存在，获取值
                    if (hasCustomPushContent) {
                        String customPushContent = json.getString("custom_push_content");
                        Log.i("BluedHook", "ChatHook\n" +
                                "custom_push_content 值: " + customPushContent);
                        if (hasLid) {
                            int lid = json.getInt("lid");
                            if (lid > 0) {
                                VoiceTTS.getInstance(getSafeContext()).speakAdd(customPushContent);
                            }
                        }
                    }


                } catch (Exception e) {
                    System.err.println("JSON 解析错误: " + e.getMessage());
                }
            }
            // 记录日志
            Log.i("BluedHook", methodTag + "-消息类型:" + chatContent.msgType);
            Log.i("BluedHook", methodTag + "-附加消息:" + chatContent.extraMsg);
            Log.i("BluedHook", methodTag + "-发送方昵称:" + chatContent.fromNickName);
            Log.i("BluedHook", methodTag + "-消息内容:" + chatContent.msgContent);
            Log.i("BluedHook", methodTag + "-会话ID:" + chatContent.sessionId);
            Log.i("BluedHook", methodTag + "-发送方ID:" + chatContent.fromId);
            // 消息类型处理
            String toastMsg;
            switch (chatContent.msgType) {
                case 2:
                    toastMsg = "收到[" + chatContent.fromNickName + "]的私信图片";
                    break;
                case 5:
                    toastMsg = "收到[" + chatContent.fromNickName + "]的私信视频";
                    break;
                case 24:
                    toastMsg = "收到[" + chatContent.fromNickName + "]的私信闪图\n" +
                            "已改为普通图片";
                    XposedHelpers.setIntField(chattingModel, "msgType", 2);
                    break;
                case 25:
                    XposedHelpers.setIntField(chattingModel, "msgType", 5);
                    XposedHelpers.setObjectField(chattingModel, "msgContent", ModuleTools.AesDecrypt(chatContent.msgContent));
                    toastMsg = "收到[" + chatContent.fromNickName + "]的私信视频闪图\n" +
                            "已改为普通视频";
                    break;
                default:
                    toastMsg = "收到[" + chatContent.fromNickName + "]的私信：" + chatContent.msgContent;
            }

            if (toastMsg != null) {
                ModuleTools.showToast(toastMsg, Toast.LENGTH_LONG);
            }

        } catch (Exception e) {
            Log.e("BluedHook", methodTag + "-处理消息异常", e);
        }
    }

    public void chatReadedHook() {
        XposedHelpers.findAndHookMethod("io.grpc.MethodDescriptor", classLoader, "generateFullMethodName", String.class, String.class, new XC_MethodHook() {
            @Override
            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                super.beforeHookedMethod(param);
                String serviceName = (String) param.args[0];
                String methodName = (String) param.args[1];
                if (serviceName.equals("com.blued.im.private_chat.Receipt") && methodName.equals("Read")) {
                    param.args[0] = "";
                    param.args[1] = "";
                    //ModuleTools.showBluedToast("已开启悄悄查看消息功能");
                }
            }

            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                super.afterHookedMethod(param);
            }
        });
    }

    public void chatProtectScreenshotHook() {
        XposedHelpers.findAndHookMethod("com.soft.blued.ui.msg.MsgChattingFragment", classLoader, "c", boolean.class, new XC_MethodHook() {
            @Override
            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                super.beforeHookedMethod(param);
                if ((boolean) param.args[0]) {
                    param.args[0] = false;
                    getTvScreenshotProtection().setVisibility(View.VISIBLE);
                    //ModuleTools.showBluedToast("对方已开启聊天截屏保护功能(无法截图聊天界面)，已关闭此功能");
                }
            }

            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                super.afterHookedMethod(param);
            }
        });
    }

    private TextView tv_chat_read_msg;
    private TextView tv_recall_msg;
    private TextView tv_screenshot_protection;

    public TextView getTvScreenshotProtection() {
        return tv_screenshot_protection;
    }

    public TextView getTvRecallMsg() {
        return tv_recall_msg;
    }

    private void hookMsgChattingTitle() {
        XposedHelpers.findAndHookMethod("com.soft.blued.ui.msg.MsgChattingFragment", classLoader, "af", new XC_MethodHook() {
            @SuppressLint("UseCompatLoadingForDrawables")
            @Override
            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                super.beforeHookedMethod(param);
                View n = (View) XposedHelpers.getObjectField(param.thisObject, "n");
                @SuppressLint("DiscouragedApi") int msg_chatting_titleId = getSafeContext().getResources().getIdentifier("msg_chatting_title", "id", getSafeContext().getPackageName());
                View findViewById = n.findViewById(msg_chatting_titleId);
                @SuppressLint("DiscouragedApi") int ll_center_distanceId = getSafeContext().getResources().getIdentifier("ll_center_distance", "id", getSafeContext().getPackageName());
                LinearLayout ll_center_distance = findViewById.findViewById(ll_center_distanceId);
                TagLayout tlTitle = new TagLayout(n.getContext());
                tv_chat_read_msg = tlTitle.addTextView("悄悄查看", 9, modRes.getDrawable(R.drawable.bg_orange, null));
                tv_recall_msg = tlTitle.addTextView("防撤回", 9, modRes.getDrawable(R.drawable.bg_gradient_orange, null));
                tv_recall_msg.setVisibility(View.GONE);
                tv_screenshot_protection = tlTitle.addTextView("私信截图", 9, modRes.getDrawable(R.drawable.bg_rounded, null));
                tv_screenshot_protection.setVisibility(View.GONE);
                ll_center_distance.addView(tlTitle);
            }

            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                super.afterHookedMethod(param);
            }
        });
    }

    private Context getSafeContext() {
        return contextRef != null ? contextRef.get() : null;
    }
}
