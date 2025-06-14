package com.zjfgh.bluedhook.simple;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.res.XModuleResources;
import android.content.res.XmlResourceParser;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedHelpers;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class FragmentMineNewBindingHook {
    private static FragmentMineNewBindingHook instance;
    private final ClassLoader classLoader;
    private final XModuleResources modRes;
    private final WeakReference<Context> contextRef; // 使用 WeakReference

    private FragmentMineNewBindingHook(Context context, XModuleResources modRes) {
        this.classLoader = context.getClassLoader();
        this.contextRef = new WeakReference<>(context); // 弱引用
        this.modRes = modRes;
        hook();
    }

    public static synchronized FragmentMineNewBindingHook getInstance(Context context, XModuleResources modRes) {
        if (instance == null) {
            instance = new FragmentMineNewBindingHook(context, modRes);
        }
        return instance;
    }

    // 使用时检查 Context 是否还存在
    public Context getSafeContext() {
        Context context = contextRef.get();
        if (context == null) {
            throw new IllegalStateException("Context was garbage collected");
        }
        return context;
    }

    public void hook() {
        XposedHelpers.findAndHookMethod("com.soft.blued.databinding.FragmentMineNewBinding", classLoader, "a", View.class, new XC_MethodHook() {
            @Override
            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                super.beforeHookedMethod(param);

            }

            @SuppressLint({"NotifyDataSetChanged", "UseCompatLoadingForDrawables"})
            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                super.afterHookedMethod(param);
                View view = (View) param.args[0];
                @SuppressLint("DiscouragedApi")
                int ll_liveID = getSafeContext().getResources().getIdentifier("ll_live", "id", getSafeContext().getPackageName());
                LinearLayout ll_live = view.findViewById(ll_liveID);
                LayoutInflater inflater = (LayoutInflater) ll_live.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                XmlResourceParser anchorFansOpenLayoutRes = modRes.getLayout(R.layout.anchor_fans_open_layout);
                LinearLayout anchorFansOpenLayout = (LinearLayout) inflater.inflate(anchorFansOpenLayoutRes, null, false);
                // 创建一个GradientDrawable对象
                LinearLayout ll_ygb_give = anchorFansOpenLayout.findViewById(R.id.ll_ygb_give);
                ll_ygb_give.setBackground(modRes.getDrawable(R.drawable.anchor_fans_open_item_bg, null));
                LinearLayout ll_data_analyzer = anchorFansOpenLayout.findViewById(R.id.ll_data_analyzer);
                ll_data_analyzer.setBackground(modRes.getDrawable(R.drawable.anchor_fans_open_item_bg, null));
                ll_live.addView(anchorFansOpenLayout, 1);
                ll_ygb_give.setOnClickListener(setToastTgbListener());
                ll_data_analyzer.setOnClickListener(openDataAnalyzerView());
            }
        });
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    private View.OnClickListener openDataAnalyzerView() {
        return v -> {
            Activity activity = (Activity) v.getContext();
            DataAnalyzerView dataAnalyzerView = new DataAnalyzerView(activity);
            CustomPopupWindow customPopupWindow = new CustomPopupWindow(activity, dataAnalyzerView, Color.parseColor("#FF0A121F"));
            customPopupWindow.setBackgroundDrawable(modRes.getDrawable(R.drawable.bg_tech_space, null));
            customPopupWindow.setAnimationStyle(android.R.style.Animation_Dialog);
            customPopupWindow.showAtCenter();
            customPopupWindow.setOnDismissListener(dataAnalyzerView::onDestroy);
        };
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    private View.OnClickListener setToastTgbListener() {
        return v -> {
            Activity activity = (Activity) v.getContext();
            LayoutInflater inflater = (LayoutInflater) v.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            XmlResourceParser anchorFansListLayoutRes = modRes.getLayout(R.layout.anchor_fans_list_layout);
            LinearLayout anchorFansListLayout = (LinearLayout) inflater.inflate(anchorFansListLayoutRes, null, false);
            anchorFansListLayout.setBackground(modRes.getDrawable(R.drawable.bg_tech_space, null));
            CustomPopupWindow customPopupWindow = new CustomPopupWindow(activity, anchorFansListLayout, Color.parseColor("#FF0A121F"));
            customPopupWindow.setBackgroundDrawable(modRes.getDrawable(R.drawable.bg_tech_space, null));
            customPopupWindow.setAnimationStyle(android.R.style.Animation_Dialog);
            customPopupWindow.showAtCenter();
            Button giveYbgButton = anchorFansListLayout.findViewById(R.id.give_ybg_button);
            giveYbgButton.setBackground(modRes.getDrawable(R.drawable.button_state, null));
            TextView anchorFansJoinCount = anchorFansListLayout.findViewById(R.id.anchor_fans_join_count);
            GradientDrawable getYbgButtonDrawable = new GradientDrawable();
            getYbgButtonDrawable.setCornerRadius(20f);
            giveYbgButton.setText("加载粉丝团列表...");
            giveYbgButton.setEnabled(false);

            List<AnchorFansListAdapter.AnchorFansListBean> anchorFansList = new ArrayList<>();
            RecyclerView recyclerView = new RecyclerView(v.getContext());
            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(v.getContext());
            linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
            recyclerView.setBackgroundColor(Color.parseColor("#FF041451"));
            recyclerView.setLayoutManager(linearLayoutManager);
            AnchorFansListAdapter adapter = new AnchorFansListAdapter(v.getContext(), anchorFansList, modRes);
            recyclerView.setAdapter(adapter);
            recyclerView.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
            @SuppressLint({"SetTextI18n", "NotifyDataSetChanged"}) Thread thread = new Thread(() -> {
                final int[] page = {1};
                final int[] hasMore = {1};
                final int[] joinFansCount = {0, 0};
                while (hasMore[0] != 0) {
                    try {
                        Response response = NetworkManager.getInstance().get(NetworkManager.getBluedAnchorFansAPI(page[0]), AuthManager.fetchAuthHeaders(classLoader));
                        assert response.body() != null;
                        String resultStr = response.body().string();
                        JSONObject rootObj = new JSONObject(resultStr);
                        JSONArray dataArr = rootObj.getJSONArray("data");
                        int dataCount = dataArr.length();
                        hasMore[0] = dataCount;
                        for (int i = 0; i < dataCount; i++) {
                            JSONObject dataI = (JSONObject) dataArr.get(i);
                            AnchorFansListAdapter.AnchorFansListBean anchorFansListBean = new AnchorFansListAdapter.AnchorFansListBean();
                            anchorFansListBean.anchor = dataI.getLong("anchor");
                            anchorFansListBean.relation = dataI.getInt("relation");
                            anchorFansListBean.anchor_name = dataI.getString("anchor_name");
                            anchorFansListBean.name = dataI.getString("name");
                            anchorFansListBean.level = dataI.getInt("level");
                            anchorFansListBean.relation_level = dataI.getInt("relation_level");
                            anchorFansListBean.level_next = dataI.getInt("level_next");
                            anchorFansListBean.next_level_relation = dataI.getInt("next_level_relation");
                            anchorFansListBean.relation_limit = dataI.getInt("relation_limit");
                            anchorFansListBean.relation_today = dataI.getInt("relation_today");
                            anchorFansListBean.gift_count = dataI.getInt("gift_count");
                            anchorFansList.add(anchorFansListBean);
                            joinFansCount[0]++;
                            if (page[0] == 1 && i == 0) {
                                joinFansCount[1] = anchorFansListBean.gift_count;
                            }
                        }
                        page[0]++;
                        v.post(() -> {
                            adapter.notifyDataSetChanged();
                            v.post(() -> {
                                anchorFansJoinCount.setText("已加入粉丝团" + joinFansCount[0] + "个，预计可领取" + (joinFansCount[1] * joinFansCount[0]) + "根荧光棒");
                            });
                        });
                        Thread.sleep(200);
                    } catch (IOException | JSONException | InterruptedException e) {
                        v.post(() -> {
                            Toast.makeText(getSafeContext(), "JSONException = " + e, Toast.LENGTH_LONG).show();
                        });
                    }
                }
                v.post(() -> {
                    giveYbgButton.setText("领取荧光棒");
                    giveYbgButton.setEnabled(true);
                });

            });
            thread.start();

            giveYbgButton.setOnClickListener(view2 -> {
                Thread thread1 = new Thread(() -> {
                    try {
                        for (int i = 0; i < anchorFansList.size(); i++) {
                            AnchorFansListAdapter.AnchorFansListBean anchorFansListBean = anchorFansList.get(i);
                            String json = new JSONObject()
                                    .put("anchor", String.valueOf(anchorFansListBean.anchor))
                                    .toString();

                            Response response = NetworkManager.getInstance().post(NetworkManager.getAnchorFansFreeGoodsAPI(), json, AuthManager.fetchAuthHeaders(classLoader));
                            assert response.body() != null;
                            String responseStr = response.body().string();
                            JSONObject jsonObject = new JSONObject(responseStr);
                            JSONArray data = jsonObject.getJSONArray("data");
                            JSONObject object = data.getJSONObject(0);
                            int giftCount = 0;
                            if (object.has("gift_count")) {
                                giftCount = object.getInt("gift_count");
                            }
                            String message = object.getString("message");
                            anchorFansListBean.status = 1;
                            anchorFansListBean.gift_count = giftCount;
                            anchorFansListBean.message = message;
                            int finalI = i;
                            v.post(() -> {
                                adapter.notifyItemChanged(finalI);
                            });
                            Thread.sleep(100);
                        }
                        v.post(() -> {
                            giveYbgButton.setText("领取荧光棒");
                            giveYbgButton.setEnabled(true);
                        });
                    } catch (Exception e) {
                        v.post(() -> {
                            giveYbgButton.setText("领取荧光棒");
                            giveYbgButton.setEnabled(true);
                        });
                    }
                });
                String giveYbgButtonStr = giveYbgButton.getText().toString();
                if (giveYbgButtonStr.equals("领取荧光棒")) {
                    giveYbgButton.setText("领取中...");
                    giveYbgButton.setEnabled(false);
                    thread1.start();
                }
            });
            LinearLayout anchorFansListview = anchorFansListLayout.findViewById(R.id.anchor_fans_listview);
            anchorFansListview.addView(recyclerView);
        };
    }
}