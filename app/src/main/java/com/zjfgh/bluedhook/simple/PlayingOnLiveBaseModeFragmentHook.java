package com.zjfgh.bluedhook.simple;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.XModuleResources;
import android.os.Bundle;
import android.os.Handler;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.alibaba.fastjson2.JSON;
import com.bumptech.glide.Glide;
import com.zjfgh.bluedhook.simple.module.LiveChattingModelMsgExtra;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.Timer;
import java.util.TimerTask;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedHelpers;
import okhttp3.Response;

public class PlayingOnLiveBaseModeFragmentHook {
    private static PlayingOnLiveBaseModeFragmentHook instance;
    private WeakReference<TextView> textView1Ref;
    private WeakReference<TextView> textView2Ref;
    private final WeakReference<Context> appContextRef;
    private final XModuleResources modRes;
    private final ClassLoader classLoader;
    protected User watchingAnchor;
    private WeakReference<LinearLayout> liveMsgListRef;
    private WeakReference<LinearLayout> llLiveGiftTipsRef;
    private volatile long liveMessageTime;
    private short isFirstConnect = 1;// 用于标记是否是第一次连接弹幕消息
    private boolean isWatchingLive = false; // 用于标记是否正在观看直播
    private Timer timer;
    private Object liveRoomManager;

    private PlayingOnLiveBaseModeFragmentHook(Context appContext, XModuleResources modRes) {
        this.appContextRef = new WeakReference<>(appContext);
        this.classLoader = appContextRef.get().getClassLoader();
        this.modRes = modRes;
        this.watchingAnchor = new User();
        chattingModelHook();
        anchorBeansHook();
        liveMultiBoyItemModelHook();
        watchingAnchorHook();
        liveMsgHandlerHook();
        grpcMsgSenderHook();
        startTimer();
    }

    // 获取单例实例
    public static synchronized PlayingOnLiveBaseModeFragmentHook getInstance(Context appContext, XModuleResources modRes) {
        if (instance == null) {
            instance = new PlayingOnLiveBaseModeFragmentHook(appContext, modRes);
        }
        return instance;
    }

    // 启动定时器
    public void startTimer() {
        timer = new Timer();
        //Log.d("BluedHook", "收到消息->等待首次连接弹幕消息服务...");
        // 每隔10秒执行一次
        TimerTask timerTask = new TimerTask() {
            @Override
            public void run() {
                if (isFirstConnect == 1) {
                    //Log.d("BluedHook", "收到消息->等待首次连接弹幕消息服务...");
                } else {
                    // 每隔10秒执行一次
                    if (!isWatchingLive) {
                        Log.d("BluedHook", "收到消息->当前用户不在直播间页面，检查后台直播弹幕消息是否超时。");
                        Log.e("BluedHook", "收到消息->" + System.currentTimeMillis() + " - " + liveMessageTime + " = " + (System.currentTimeMillis() - liveMessageTime));
                        if (System.currentTimeMillis() - liveMessageTime > 30000) {
                            liveMessageTime = System.currentTimeMillis();
                            Log.e("BluedHook", "收到消息->直播弹幕消息超时，重新连接直播间弹幕消息。");
                            ModuleTools.showBluedToast("直播弹幕消息接收超时，尝试重新连接...");
                            VoiceTTS.getInstance(appContextRef.get()).speak("直播弹幕消息接收超时，尝试重新连接...");
                            reConnectLive();
                        } else {
                            Log.d("BluedHook", "收到消息->直播弹幕消息未超时，等待下次检查。");
                        }
                    } else {
                        Log.d("BluedHook", "收到消息->当前用户在直播间页面，跳过后台直播弹幕消息超时检查。");
                    }

                }
            }
        };
        // 延迟0ms后执行，每隔1000ms执行一次
        timer.schedule(timerTask, 5000, 5000);
    }

    // 停止定时器
    public void stopTimer() {
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
    }

    public void chattingModelHook() {
        XposedHelpers.findAndHookMethod("com.blued.android.chat.model.ChattingModel", classLoader,
                "getMsgExtra", new XC_MethodHook() {
                    @Override
                    protected void beforeHookedMethod(MethodHookParam param) {
                        SettingItem settingItem = SQLiteManagement.getInstance()
                                .getSettingByFunctionId(SettingsViewCreator.PLAYING_ON_LIVE_BASE_MODE_FRAGMENT_HOOK);
                        if (settingItem.isSwitchOn()) {
                            int fromPrivilege = XposedHelpers.getIntField(param.thisObject, "fromPrivilege");
                            if (fromPrivilege == 1) {
                                String fromNickName = (String) XposedHelpers.getObjectField(param.thisObject, "fromNickName");
                                XposedHelpers.setObjectField(param.thisObject, "fromNickName", "[隐]" + fromNickName);
                                XposedHelpers.setIntField(param.thisObject, "fromPrivilege", 0);
                            }
                        }
                    }
                });
    }

    public void anchorBeansHook() {
        XposedHelpers.findAndHookMethod("com.blued.android.module.live_china.fragment.PlayingOnliveBaseModeFragment",
                AppContainer.getInstance().getClassLoader(), "onCreateView", LayoutInflater.class, ViewGroup.class, Bundle.class,
                new XC_MethodHook() {
                    @SuppressLint("UseCompatLoadingForDrawables")
                    @Override
                    protected void afterHookedMethod(MethodHookParam param) {
                        if (textView1Ref != null && textView2Ref != null) {
                            textView1Ref = null;
                            textView2Ref = null;
                            Log.i("BluedHook", "重新进入直播间");
                        }
                        View view = (View) param.getResult();
                        @SuppressLint("DiscouragedApi") int id = appContextRef.get().getResources().getIdentifier("onlive_current_beans", "id",
                                appContextRef.get().getPackageName());

                        TextView textView = view.findViewById(id);
                        if (textView1Ref == null) {
                            textView1Ref = new WeakReference<>(textView);
                            @SuppressLint("DiscouragedApi") int live_msg_listID = appContextRef.get().getResources().getIdentifier("live_msg_list", "id",
                                    appContextRef.get().getPackageName());
                            liveMsgListRef = new WeakReference<>(view.findViewById(live_msg_listID));
                            LayoutInflater inflater = (LayoutInflater) appContextRef.get().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                            View llLiveGiftTips = inflater.inflate(modRes.getLayout(R.layout.live_gift_tips), null, true);
                            llLiveGiftTipsRef = new WeakReference<>((LinearLayout) llLiveGiftTips);
                            llLiveGiftTips.setVisibility(View.GONE);
                            LinearLayout ll_gift_tips = llLiveGiftTips.findViewById(R.id.ll_gift_tips);
                            ll_gift_tips.setBackground(modRes.getDrawable(R.drawable.bg_zise_tag, null));
                            liveMsgListRef.get().addView(llLiveGiftTips, 0);
                        } else {
                            textView2Ref = new WeakReference<>(textView);
                        }
                    }
                });

        XposedHelpers.findAndHookMethod("com.blued.android.module.live_china.fragment.PlayingOnliveBaseModeFragment",
                AppContainer.getInstance().getClassLoader(), "a", double.class, double.class, new XC_MethodHook() {
                    @Override
                    protected void afterHookedMethod(MethodHookParam param) {
                        SettingItem settingItem = SQLiteManagement.getInstance()
                                .getSettingByFunctionId(SettingsViewCreator.PLAYING_ON_LIVE_BASE_MODE_FRAGMENT_HOOK);
                        if (settingItem.isSwitchOn() && appContextRef.get() != null) {
                            Double beans_count = (Double) param.args[0];
                            Double beans_current_count = (Double) param.args[1];
                            Class<?> CommonStringUtils = XposedHelpers.findClass(
                                    "com.blued.android.module.common.utils.CommonStringUtils",
                                    AppContainer.getInstance().getClassLoader());
                            String beans_count_str = (String) XposedHelpers.callStaticMethod(
                                    CommonStringUtils, "e", String.valueOf(beans_count));
                            String beans_current_count_str = (String) XposedHelpers.callStaticMethod(
                                    CommonStringUtils, "e", String.valueOf(beans_current_count));

                            updateTextView(textView1Ref != null ? textView1Ref.get() : null,
                                    beans_count_str, beans_current_count_str);
                            updateTextView(textView2Ref != null ? textView2Ref.get() : null,
                                    beans_count_str, beans_current_count_str);
                        }
                    }
                });
    }

    private static void updateTextView(TextView textView, String beansCount, String beansCurrentCount) {
        if (textView != null) {
            String beans = "本场" + beansCurrentCount + "/总豆" + beansCount;
            textView.setText(beans);
        }
    }

    public void liveMultiBoyItemModelHook() {
        XposedHelpers.findAndHookMethod("com.blued.android.module.live_china.view.LiveMultiBoyItemView", classLoader, "c", new XC_MethodHook() {
            @Override
            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                super.beforeHookedMethod(param);
                SettingItem settingItem = SQLiteManagement.getInstance().getSettingByFunctionId(SettingsViewCreator.PLAYING_ON_LIVE_BASE_MODE_FRAGMENT_HOOK);
                if (settingItem.isSwitchOn()) {
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

    private void watchingAnchorHook() {
        Class<?> LiveRoomDataClass = XposedHelpers.findClass("com.blued.android.module.live_china.model.LiveRoomData", classLoader);
        XposedHelpers.findAndHookMethod("com.blued.android.module.live_china.fragment.PlayingOnliveBaseModeFragment", classLoader, "a", LiveRoomDataClass, new XC_MethodHook() {
            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                super.beforeHookedMethod(param);
                Object liveRoomData = param.args[0];
                Object liveRoomAnchorModel = XposedHelpers.getObjectField(liveRoomData, "profile");
                watchingAnchor.setName((String) XposedHelpers.getObjectField(liveRoomAnchorModel, "name"));
                watchingAnchor.setUid((String) XposedHelpers.getObjectField(liveRoomAnchorModel, "uid"));
                watchingAnchor.setLive(String.valueOf(XposedHelpers.getObjectField(liveRoomData, "lid")));
                watchingAnchor.setAvatar((String) XposedHelpers.getObjectField(liveRoomAnchorModel, "avatar"));
                isWatchingLive = true;
            }

            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                super.afterHookedMethod(param);
            }
        });

    }

    private void liveMsgHandlerHook() {
        Class<?> LiveChattingModelClass = XposedHelpers.findClass("com.blued.android.module.live_china.model.LiveChattingModel", classLoader);
        XposedHelpers.findAndHookMethod("com.blued.android.module.live_china.msg.LiveMsgHandler", classLoader, "a", LiveChattingModelClass, new XC_MethodHook() {
            @SuppressLint("SetTextI18n")
            @Override
            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                super.beforeHookedMethod(param);
                Object liveChattingModel = param.args[0];
                short msgType = XposedHelpers.getShortField(liveChattingModel, "msgType");
                String msgExtra = (String) XposedHelpers.callMethod(liveChattingModel, "getMsgExtra");
                String fromNickName = (String) XposedHelpers.getObjectField(liveChattingModel, "fromNickName");
                Log.e("Blued", "msgType = " + msgType + "|fromNickName = " + fromNickName + "|msgExtra = " + msgExtra);
                if (msgType == 33 || msgType == 289) {
                    XposedHelpers.setShortField(liveChattingModel, "msgType", (short) 11001);
                    LiveChattingModelMsgExtra msgExtraObj = JSON.parseObject(msgExtra, LiveChattingModelMsgExtra.class);
                    String giftName = msgExtraObj.getGiftModel().getName();
                    String receiverName = msgExtraObj.getGiftModel().getReceiverName();
                    int giftCount = msgExtraObj.getGiftModel().getCount();
                    String giftIcon = msgExtraObj.getGiftModel().getImagesStatic();
                    // 取消之前的隐藏任务
                    mHideHandler.removeCallbacks(mHideRunnable);
                    // 显示礼物图标
                    if (llLiveGiftTipsRef.get() != null) {
                        llLiveGiftTipsRef.get().setVisibility(View.VISIBLE);
                        TextView tvGiftTips = llLiveGiftTipsRef.get().findViewById(R.id.tv_gift_tips);
                        ImageView ivGiftIcon = llLiveGiftTipsRef.get().findViewById(R.id.iv_gift_icon);
                        Glide.with(appContextRef.get())
                                .load(giftIcon) // 可以是URL、资源ID、文件等
                                .placeholder(0) // 加载中显示
                                .error(0) // 加载失败显示
                                .centerCrop() // 图片裁剪方式
                                .into(ivGiftIcon); // 目标ImageView
                        String htmlText = "<font color='#4BD9ED'>" + fromNickName + " 送</font>" +
                                "<font color='#F8CC53'> " + receiverName + " </font>" +
                                "<font color='#4BD9ED'>" + giftName + "</font>" +
                                "<font color='#F8CC53'> x " + giftCount + "</font>";
                        // 初始状态：在屏幕外（Y = 自身高度，即完全在屏幕下方）
                        tvGiftTips.setTranslationY(tvGiftTips.getHeight());
                        tvGiftTips.setAlpha(0f); // 初始透明
                        tvGiftTips.setText(Html.fromHtml(htmlText, Html.FROM_HTML_MODE_LEGACY));
                        // 执行动画：滑入 + 渐显
                        tvGiftTips.animate()
                                .translationY(0f)  // 回到Y=0（正常位置）
                                .alpha(1f)         // 渐显
                                .setDuration(600)
                                .setInterpolator(new DecelerateInterpolator())
                                .start();
                    }
                    mHideHandler.postDelayed(mHideRunnable, 5000);
                }
            }

            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                super.afterHookedMethod(param);
            }
        });
    }

    private Object bluedUIHttpResponse;

    public void grpcMsgSenderHook() {
        Object Any = XposedHelpers.findClass("com.google.protobuf.Any", classLoader);
        XposedHelpers.findAndHookMethod("com.blued.android.module.live_china.msg.GrpcMsgSender$2", classLoader, "onReceive", Any, new XC_MethodHook() {
            @Override
            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                super.beforeHookedMethod(param);
                Object any = param.args[0];
                Class<?> LiveMessage = XposedHelpers.findClass("cn.irisgw.live.LiveMessage", classLoader);
                boolean isLiveMessage = (boolean) XposedHelpers.callMethod(any, "is", LiveMessage);
                if (isLiveMessage) {

                    Object liveMessage = XposedHelpers.callMethod(any, "unpack", LiveMessage);
                    // 处理 LiveMessage
                    if (liveMessage != null) {
                        liveMessageTime = System.currentTimeMillis();
                        Log.d("BluedHook", "收到消息->直播间消息，liveMessageTime：" + liveMessageTime);
                        Object msgExtra = XposedHelpers.callMethod(liveMessage, "getExtra");
                        Log.d("BluedHook", "收到消息->直播间消息，类型：" + XposedHelpers.callMethod(msgExtra, "getTypeUrl"));
                        int typeValue = (int) XposedHelpers.callMethod(liveMessage, "getTypeValue");
                        if (typeValue == 27) {
                            //进入直播间
                            if (msgExtra != null) {
                                Class<?> JoinLiveExtra = XposedHelpers.findClass("cn.irisgw.live.JoinLiveExtra", classLoader);
                                boolean isJoinLiveExtra = (boolean) XposedHelpers.callMethod(msgExtra, "is", JoinLiveExtra);
                                if (isJoinLiveExtra) {
                                    Object profile = XposedHelpers.callMethod(liveMessage, "getProfile");
                                    String name = (String) XposedHelpers.callMethod(profile, "getName");
                                    Log.i("BluedHook", "收到消息->直播间消息：" + name + " 进入直播间");
                                }
                            }
                        }
                        if (typeValue == 28) {
                            //离开直播间
                            Class<?> LeaveLiveExtra = XposedHelpers.findClass(" cn.irisgw.live.LeaveLiveExtra", classLoader);
                            boolean isLeaveLiveExtra = (boolean) XposedHelpers.callMethod(msgExtra, "is", LeaveLiveExtra);
                            if (isLeaveLiveExtra) {
                                Object profile = XposedHelpers.callMethod(liveMessage, "getProfile");
                                String name = (String) XposedHelpers.callMethod(profile, "getName");
                                Log.i("BluedHook", "收到消息->直播间消息：" + name + " 退出了直播间");
                                ModuleTools.showBluedToast(name + " 退出了直播间");
                                if (BluedHook.wsServerManager.isServerRunning()) {
                                    JSONObject jsonObject = new JSONObject();
                                    jsonObject.put("msgType", typeValue);
                                    jsonObject.put("msgExtra", name + " 退出了直播间");
                                    Log.d("BluedHook", jsonObject.toString());
                                    BluedHook.wsServerManager.broadcastMessage(jsonObject.toString());
                                }
                            }
                        }
                        if (typeValue == 233) {
                            Class<?> NewHorn = XposedHelpers.findClass("cn.irisgw.live.NewHorn", classLoader);
                            boolean isHewHorn = (boolean) XposedHelpers.callMethod(msgExtra, "is", NewHorn);
                            if (isHewHorn) {
                                Object hewHorn = XposedHelpers.callMethod(msgExtra, "unpack", NewHorn);
                                String contents = (String) XposedHelpers.callMethod(hewHorn, "getContents");
                                ModuleTools.writeFile("所有飘屏记录.txt", contents);
                                if (BluedHook.wsServerManager.isServerRunning()) {
                                    JSONObject jsonObject = new JSONObject();
                                    jsonObject.put("msgType", typeValue);
                                    jsonObject.put("msgExtra", contents);
                                    Log.d("BluedHook", jsonObject.toString());
                                    BluedHook.wsServerManager.broadcastMessage(jsonObject.toString());
                                }
                                LiveMessageParser liveMessageParser = new LiveMessageParser();
                                LiveMessageParser.ParseResult parseResult = liveMessageParser.parse(contents);
                                Log.i("BluedHook", "飘屏类型 -> " + parseResult.getMessageType() + "|" + parseResult.getCharSequence() + "|" + parseResult.getKeywords().toString());
                                if (parseResult.getMessageType() == LiveMessageParser.MessageType.MULTIPLIER_REWARD) {
                                    // 只有关键字是3个的时候，才是倍数奖励飘屏
                                    if (parseResult.getKeywords().size() == 3) {
                                        // 处理倍数奖励飘屏
                                        String rewardMultiplier = parseResult.getKeywords().get(1);
                                        String rewardBean = parseResult.getKeywords().get(2);
                                        //计算幸运礼物本身的价值
                                        int LuckyGiftsBean = Integer.parseInt(rewardBean) / Integer.parseInt(rewardMultiplier);
                                        switch (LuckyGiftsBean) {
                                            case 4:
                                                //幸运手镯
                                                ModuleTools.writeFile("抽奖_幸运手镯_飘屏记录.txt", parseResult.getCharSequence());
                                                Log.e("BluedHook", "抽奖_幸运手镯_飘屏记录：" + parseResult.getCharSequence());
                                                break;
                                            case 12:
                                                //幸运魔法棒
                                                ModuleTools.writeFile("抽奖_幸运魔法棒_飘屏记录.txt", parseResult.getCharSequence());
                                                Log.e("BluedHook", "抽奖_幸运魔法棒_飘屏记录：" + parseResult.getCharSequence());
                                                break;
                                            case 36:
                                                //幸运魔镜
                                                ModuleTools.writeFile("抽奖_幸运魔镜_飘屏记录.txt", parseResult.getCharSequence());
                                                Log.e("BluedHook", "抽奖_幸运魔镜_飘屏记录：" + parseResult.getCharSequence());
                                                break;
                                            default:
                                                // 其他倍数奖励飘屏
                                                ModuleTools.writeFile("抽奖_其他倍数奖励_飘屏记录.txt", parseResult.getCharSequence());
                                                Log.e("BluedHook", "抽奖_其他倍数奖励_飘屏记录：" + parseResult.getCharSequence());
                                                break;
                                        }
                                    }
                                }
                                if (parseResult.getMessageType() == LiveMessageParser.MessageType.CHAMELEON_LIFE) {
                                    // 处理百变人生飘屏
                                    ModuleTools.writeFile("百变人生_208_飘屏记录.txt", parseResult.getCharSequence());
                                    Log.e("BluedHook", "百变人生_208_飘屏记录：" + parseResult.getCharSequence());
                                }
                                if (parseResult.getMessageType() == LiveMessageParser.MessageType.A_DESERT_DREAM) {
                                    // 处理一梦敦煌飘屏
                                    ModuleTools.writeFile("一梦敦煌_168_飘屏记录.txt", parseResult.getCharSequence());
                                    Log.e("BluedHook", "一梦敦煌_168_飘屏记录：" + parseResult.getCharSequence());
                                }
                                if (parseResult.getMessageType() == LiveMessageParser.MessageType.HOLY_SWORDSMAN) {
                                    // 处理神圣剑士飘屏
                                    ModuleTools.writeFile("神圣剑士_58_飘屏记录.txt", parseResult.getCharSequence());
                                    Log.e("BluedHook", "神圣剑士_58_飘屏记录：" + parseResult.getCharSequence());
                                }
                                if (parseResult.getMessageType() == LiveMessageParser.MessageType.PRIMARY_TREASURE) {
                                    // 处理初级宝藏飘屏
                                    ModuleTools.writeFile("初级宝藏_10_飘屏记录.txt", parseResult.getCharSequence());
                                    Log.e("BluedHook", "初级宝藏_10_飘屏记录：" + parseResult.getCharSequence());
                                }
                                if (parseResult.getMessageType() == LiveMessageParser.MessageType.ADVANCED_TREASURE) {
                                    // 处理高级宝藏飘屏
                                    ModuleTools.writeFile("高级宝藏_200_飘屏记录.txt", parseResult.getCharSequence());
                                    Log.e("BluedHook", "高级宝藏_200_飘屏记录：" + parseResult.getCharSequence());
                                }
                                if (parseResult.getMessageType() == LiveMessageParser.MessageType.GLOWING_TREASURE) {
                                    // 处理璀璨宝藏飘屏
                                    ModuleTools.writeFile("璀璨宝藏_500_飘屏记录.txt", parseResult.getCharSequence());
                                    Log.e("BluedHook", "璀璨宝藏_500_飘屏记录：" + parseResult.getCharSequence());
                                }
                            }
                        }
                        if (msgExtra != null) {
                            // 获取 CloseLiveExtra 类
                            Class<?> CloseLiveExtra = XposedHelpers.findClass("cn.irisgw.live.CloseLiveExtra", classLoader);

                            // 检查是否是 CloseLiveExtra 类型
                            boolean isCloseLiveExtra = (boolean) XposedHelpers.callMethod(msgExtra, "is", CloseLiveExtra);

                            if (isCloseLiveExtra) {
                                // 解包 CloseLiveExtra 数据
                                Object unpackedExtra = XposedHelpers.callMethod(msgExtra, "unpack", CloseLiveExtra);
                                // 获取 KickInfo 信息
                                Object kickInfo = XposedHelpers.callMethod(unpackedExtra, "getKickInfo");
                                // 提取关闭信息
                                String title = (String) XposedHelpers.callMethod(kickInfo, "getTitle");
                                String message = (String) XposedHelpers.callMethod(kickInfo, "getMessage");
                                String audienceMessage = (String) XposedHelpers.callMethod(kickInfo, "getAudienceMessage");
                                int reasonValue = (int) XposedHelpers.callMethod(kickInfo, "getReasonValue");
                                // 判断关闭原因
                                String closeReason = (reasonValue < 1) ? "主播主动关闭" : "管理员强制关闭";
                                // 打印日志
                                Log.i("BluedHook", "收到消息->直播结束 - 原因: " + closeReason);
                                Log.i("BluedHook", "收到消息->标题: " + title);
                                Log.i("BluedHook", "收到消息->主播端提示: " + message);
                                Log.i("BluedHook", "收到消息->观众端提示: " + audienceMessage);
                                reConnectLive();
                            }
                        }
                    }
                } else {
                    Log.d("BluedHook", "收到消息->类型：非直播间消息");
                }
            }

            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                super.afterHookedMethod(param);
            }
        });
        Class<?> BluedUIHttpResponse = XposedHelpers.findClass("com.blued.android.framework.http.BluedUIHttpResponse", classLoader);
        XposedHelpers.findAndHookMethod("com.blued.android.module.live_china.utils.LiveRoomHttpUtils", classLoader, "a", BluedUIHttpResponse, long.class, java.lang.String.class, int.class, int.class, new XC_MethodHook() {
            @Override
            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                super.beforeHookedMethod(param);
                // 获取 BluedUIHttpResponse，用于后续的直播结束后重新连接新直播间的参数
                bluedUIHttpResponse = param.args[0];
            }

            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                super.afterHookedMethod(param);
            }
        });
        XposedHelpers.findAndHookMethod("com.blued.android.module.live_china.msg.GrpcMsgSender$2", classLoader, "onConnected", new XC_MethodHook() {
            @Override
            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                super.beforeHookedMethod(param);
                Log.e("BluedHook", "收到消息->弹幕消息服务已连接" + "|isFirstConnect = " + isFirstConnect);
                isFirstConnect = 0;
                Class<?> LiveRoomManager = XposedHelpers.findClass("com.blued.android.module.live_china.manager.LiveRoomManager", classLoader);
                liveRoomManager = XposedHelpers.callStaticMethod(LiveRoomManager, "a");
                long lid = (long) XposedHelpers.callMethod(liveRoomManager, "d");
                if (lid <= 0) {
                    Log.d("BluedHook", "直播ID为" + lid + "，拉取推荐直播数据");
                    Response response = NetworkManager.getInstance().get(NetworkManager.getUsersRecommendApi(), AuthManager.auHook(false, classLoader, appContextRef.get()));
                    try {
                        JSONObject jsonObject = new JSONObject(response.body().string());
                        int code = jsonObject.getInt("code");
                        if (code == 200) {
                            JSONArray data = jsonObject.getJSONArray("data");
                            long live = data.getJSONObject(0).getLong("live");
                            String anchorTitle = data.getJSONObject(0).getString("title");
                            Object liveRoomData = XposedHelpers.callMethod(liveRoomManager, "q");
                            XposedHelpers.setLongField(liveRoomData, "lid", live);
                            Log.i("BluedHook", "进入 " + anchorTitle + " 的直播间，LID：" + XposedHelpers.callMethod(liveRoomManager, "d"));
                        }
                    } catch (JSONException e) {
                        throw new RuntimeException(e);
                    }
                }

            }

            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                super.afterHookedMethod(param);
            }
        });
        XposedHelpers.findAndHookMethod("com.blued.android.module.live_china.utils.LiveRoomHttpUtils", classLoader, "a", java.lang.String.class, java.lang.String.class, java.lang.String.class, new XC_MethodHook() {
            @Override
            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                super.beforeHookedMethod(param);
                Log.i("BluedHook", "收到消息->直播间消息，类型：手动退出直播间");
                isWatchingLive = false;
                Thread thread = new Thread(() -> {
                    // 重新连接直播间
                    reConnectLive();
                });
                thread.start();
            }

            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                super.afterHookedMethod(param);
            }
        });
    }

    private void reConnectLive() {
        try {
            //从直播推荐列表获取新的直播间ID
            Response response = NetworkManager.getInstance().get(NetworkManager.getUsersRecommendApi(), AuthManager.auHook(false, classLoader, appContextRef.get()));
            assert response.body() != null;
            JSONObject jsonObject = new JSONObject(response.body().string());
            int code = jsonObject.getInt("code");
            if (code == 200) {
                JSONArray data = jsonObject.getJSONArray("data");
                long live = data.getJSONObject(0).getLong("live");
                String anchorTitle = data.getJSONObject(0).getString("title");
                //调用连接直播间的方法
                Class<?> LiveRoomHttpUtils = XposedHelpers.findClass("com.blued.android.module.live_china.utils.LiveRoomHttpUtils", classLoader);
                XposedHelpers.callStaticMethod(LiveRoomHttpUtils, "a", bluedUIHttpResponse, live, "", 3, 0);
                Log.i("BluedHook", "收到消息->尝试连接新的 " + anchorTitle + " 的直播间，直播ID：" + live);
            }
        } catch (JSONException | IOException e) {
            Log.e("BluedHook", "reConnectLive->重新连接直播间失败", e);
        }
    }

    // 在类中定义这些变量
    private final Handler mHideHandler = new Handler();
    private final Runnable mHideRunnable = new Runnable() {
        @Override
        public void run() {
            if (llLiveGiftTipsRef.get() != null) {
                llLiveGiftTipsRef.get().setVisibility(View.GONE);
            }
        }
    };

    public User getWatchingAnchor() {
        return watchingAnchor;
    }
}