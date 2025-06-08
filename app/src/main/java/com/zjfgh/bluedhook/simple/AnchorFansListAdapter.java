package com.zjfgh.bluedhook.simple;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.XModuleResources;
import android.content.res.XmlResourceParser;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Space;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;
import java.util.logging.Handler;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;
import okhttp3.ResponseBody;

public class AnchorFansListAdapter extends RecyclerView.Adapter<AnchorFansListAdapter.MyViewHolder> {
    protected Context context;
    private final List<AnchorFansListBean> data;
    private final LayoutInflater inflater;
    private final XModuleResources modRes;

    public AnchorFansListAdapter(Context context, List<AnchorFansListBean> data, XModuleResources modRes) {
        this.context = context;
        this.data = data;
        this.inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.modRes = modRes;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        XmlResourceParser anchorFansListItemLayout = modRes.getLayout(R.layout.anchor_fans_list_item);
        View view = inflater.inflate(anchorFansListItemLayout, parent, false);
        return new MyViewHolder(view);
    }

    @SuppressLint({"SetTextI18n", "DiscouragedApi", "UseCompatLoadingForDrawables"})
    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, @SuppressLint("RecyclerView") int position) {
        AnchorFansListBean anchorFansListBean = data.get(position);
        int img = modRes.getIdentifier("live_fans_ygb_dis_open_icon", "drawable", "com.zjfgh.bluedhook.simple");
        Bitmap bitmap = BitmapFactory.decodeResource(modRes, img);
        holder.ll_item_root.setBackground(modRes.getDrawable(R.drawable.bg_tech_item, null));
        holder.ll_root.setBackground(modRes.getDrawable(R.drawable.bg_tech_item_inner, null));
        holder.ivYgbIcon.setImageBitmap(bitmap);
        holder.tvAnchorName.setText(anchorFansListBean.anchor_name);
        holder.tvGiveState.setBackground(modRes.getDrawable(R.drawable.bg_tech_tag, null));
        holder.dataContainerBgLL.setBackground(modRes.getDrawable(R.drawable.bg_tech_data_container, null));
        holder.anchorFansRelationProgress.setBackground(modRes.getDrawable(R.drawable.bg_tech_progress_fill, null));
        holder.anchorFansRelationRoot.setBackground(modRes.getDrawable(R.drawable.bg_tech_progress_track, null));
        // 计算当前进度百分比 (0-1之间)
        float progressPercent = (float) anchorFansListBean.relation / anchorFansListBean.next_level_relation;
        // 确保百分比在0-1范围内
        float progressPercent1 = Math.max(0, Math.min(1, progressPercent));
        // 动画到70%进度，持续500毫秒
        ProgressBarAnimator.animateProgress(holder.anchorFansRelationProgress, holder.anchorFansRelationSpace, progressPercent1, 1000);
        holder.tvNowRelation.setText("亲密值" + anchorFansListBean.relation + ",距下一级还剩" + (anchorFansListBean.next_level_relation - anchorFansListBean.relation) + "亲密值");

        holder.tvLimitRelation.setText("今日已获亲密值/上限:" + anchorFansListBean.relation_today + "/" + anchorFansListBean.relation_limit);
        holder.itemView.setOnClickListener(view -> {
            if (onItemClickListener != null) {
                onItemClickListener.onItemClick(position);
            }
        });
        holder.tvGiveStart.setBackground(modRes.getDrawable(R.drawable.bg_tech_button_small, null));
        // 设置荧光棒赠送按钮的背景和点击事件
        final int[] intimacyNeeded = {anchorFansListBean.relation_limit - anchorFansListBean.relation_today};
        AtomicReference<String> title = new AtomicReference<>("");
        AtomicReference<String> message = new AtomicReference<>("");
        holder.tvGiveStart.setOnClickListener(view -> {
            String lid = PlayingOnLiveBaseModeFragmentHook.getInstance(AppContainer.getInstance().getBluedContext(), AppContainer.getInstance().getModuleRes()).getWatchingAnchor().getLive();
            if (lid == null || lid.isEmpty()) {
                ModuleTools.showBluedToast("赠送礼物需要直播间ID\n请先进入正在直播的任意一个直播间，然后尝试赠送。");
                return;
            }
            if (intimacyNeeded[0] == 0) {
                title.set("提示：亲密值已满，是否需要继续赠送？");
                message.set("请在下方输入需要赠送的荧光棒数量：");
            } else {
                title.set("请在下方输入需要赠送的荧光棒数量：");
                message.set("");
            }
            new TechDialog.Builder(context, modRes)
                    .setTitle(title.get())
                    .setMessage(message.get())
                    .setEditContent(String.valueOf(intimacyNeeded[0]))
                    .setEditHint("请输入需要赠送的荧光棒数量：")
                    .setConfirmText("赠送")
                    .setCancelText("取消")
                    .setOnConfirmListener((techDialog, editSrt) -> {
                        // 确认操作
                        JSONObject jsonObject = new JSONObject();
                        try {
                            jsonObject.put("target_uid", anchorFansListBean.anchor + "");
                            jsonObject.put("pay_token", "");
                            jsonObject.put("pay_code", "");
                            jsonObject.put("count", editSrt);
                            jsonObject.put("goods_id", 181412);
                            jsonObject.put("live_id", lid);
                            jsonObject.put("hit_id", "1745844379311");
                            jsonObject.put("from", "push");
                            jsonObject.put("remember_me", "0");
                            jsonObject.put("is_continue", "true");
                            jsonObject.put("buy_goods_from", "pack");
                            jsonObject.put("discount_id", "");
                            jsonObject.put("room_type", 0);
                            Map<String, String> au = AuthManager.fetchAuthHeaders(context.getClassLoader());
                            NetworkManager.getInstance().postAsync(NetworkManager.getBuyGoodsApi(), jsonObject.toString(), au, new Callback() {
                                @Override
                                public void onFailure(@NonNull Call call, @NonNull IOException e) {
                                    techDialog.getBtnCancel().setText("赠送失败");
                                    holder.tvGiveStart.postDelayed(techDialog::dismiss, 500);
                                }

                                @Override
                                public void onResponse(@NonNull Call call, @NonNull Response response) {
                                    try {
                                        ResponseBody responseBody = response.body();
                                        if (responseBody != null) {
                                            String responseString = responseBody.string();
                                            JSONObject root = new JSONObject(responseString);
                                            // 提取 code
                                            int code = root.getInt("code");
                                            if (code != 200) {
                                                if (root.has("message")) {
                                                    String message = root.getString("message");
                                                    ModuleTools.showBluedToast(message);
                                                    intimacyNeeded[0] = 1;
                                                }
                                                return;
                                            }
                                            // 提取 user_store_count（嵌套在 extra 里）
                                            JSONObject extra = root.getJSONObject("extra");
                                            int userStoreCount = extra.getInt("user_store_count");
                                            ModuleTools.showToast("当前剩余荧光棒：" + userStoreCount, Toast.LENGTH_LONG);
                                            Log.d("BluedHook-JSON解析", "code: " + code);
                                            Log.d("BluedHook-JSON解析", "user_store_count: " + userStoreCount); // 4020
                                            Log.i("BluedHook", "荧光棒赠送成功：" + editSrt + "|" + responseString);

                                            anchorFansListBean.relation_today = anchorFansListBean.relation_today + Integer.parseInt(editSrt);
                                            holder.tvGiveStart.post(() -> {
                                                holder.tvGiveStart.setText("赠送成功");
                                                techDialog.getBtnCancel().setText("赠送成功");
                                                holder.tvGiveStart.postDelayed(techDialog::dismiss, 500);
                                                holder.tvLimitRelation.setText("今日已获亲密值/上限:" + anchorFansListBean.relation_today + "/" + anchorFansListBean.relation_limit);
                                            });

                                        } else {
                                            techDialog.getBtnCancel().setText("赠送失败");
                                            holder.tvGiveStart.postDelayed(techDialog::dismiss, 500);
                                            Log.i("BluedHook", "荧光棒赠送失败：响应内容为空。");
                                        }
                                    } catch (JSONException | IOException e) {
                                        techDialog.getBtnCancel().setText("赠送失败");
                                        holder.tvGiveStart.postDelayed(techDialog::dismiss, 500);
                                        Log.e("BluedHook", "荧光棒赠送异常：" + e);
                                        ModuleTools.showBluedToast("荧光棒赠送异常");
                                    }
                                }
                            });
                        } catch (JSONException e) {
                            throw new RuntimeException(e);
                        }
                    })
                    .setOnCancelListener(() -> {
                        // 取消操作

                    })
                    .show();
        });
        if (anchorFansListBean.status == 1) {
            Log.i("BluedHook", "anchorFansListBean.status" + anchorFansListBean.status);
            img = modRes.getIdentifier("live_fans_ygb_open_icon", "drawable", "com.zjfgh.bluedhook.simple");
            bitmap = BitmapFactory.decodeResource(modRes, img);
            holder.ivYgbIcon.setImageBitmap(bitmap);
            holder.tvGiveState.setText("荧光棒" + anchorFansListBean.message + ",共" + anchorFansListBean.gift_count + "根");
        } else {
            holder.tvGiveState.setText("荧光棒待领取");
        }
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public static class AnchorFansListBean {
        public int status = 0;
        public int gift_count;
        public String message;
        public long anchor;
        public int relation;
        public String anchor_name;
        public String name;
        public int level;
        public int relation_level;
        public int level_next;
        public int next_level_relation;
        public int relation_limit;
        public int relation_today;
    }

    private OnItemClickListener onItemClickListener;

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public interface OnItemClickListener {
        void onItemClick(int position);
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        LinearLayout ll_item_root;
        LinearLayout ll_root;
        public ImageView ivYgbIcon;
        public TextView tvAnchorName;
        public TextView tvGiveState;
        public LinearLayout dataContainerBgLL;
        public LinearLayout anchorFansRelationRoot;
        public View anchorFansRelationProgress;
        public Space anchorFansRelationSpace;
        public TextView tvNowRelation;
        public TextView tvLimitRelation;
        public TextView tvGiveStart;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            ll_item_root = itemView.findViewById(R.id.ll_item_root);
            ll_root = itemView.findViewById(R.id.ll_anchor_fans_item_root);
            tvAnchorName = itemView.findViewById(R.id.tv_anchor_fans_name);
            tvGiveState = itemView.findViewById(R.id.tv_anchor_fans_give_state);
            dataContainerBgLL = itemView.findViewById(R.id.data_container_bg_ll);
            anchorFansRelationRoot = itemView.findViewById(R.id.anchor_fans_relation_root);
            anchorFansRelationProgress = itemView.findViewById(R.id.anchor_fans_relation_progress);
            anchorFansRelationSpace = itemView.findViewById(R.id.anchor_fans_relation_space);
            tvNowRelation = itemView.findViewById(R.id.tv_anchor_fans_now_relation);
            tvLimitRelation = itemView.findViewById(R.id.tv_anchor_fans_limit_relation);
            ivYgbIcon = itemView.findViewById(R.id.iv_ygb_icon);
            tvGiveStart = itemView.findViewById(R.id.tv_anchor_fans_give_start);
        }
    }
}
