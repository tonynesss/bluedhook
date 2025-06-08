package com.zjfgh.bluedhook.simple;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.XModuleResources;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import org.json.JSONException;
import java.lang.reflect.InvocationTargetException;

public class UserListAdapter extends ListAdapter<User, UserListAdapter.UserViewHolder> {
    private final SQLiteManagement dbManger = SQLiteManagement.getInstance();
    private final Context context;
    String currentCheckingUid = null; // 新增字段
    // 1. 首先定义删除回调接口
    public interface OnUserDeleteListener {
        void onUserDelete(User user) throws JSONException;
    }

    // 2. 添加字段和构造方法修改
    private final OnUserDeleteListener deleteListener;

    protected UserListAdapter(@NonNull Context context, OnUserDeleteListener deleteListener) {
        super(new UserDiffCallback());
        this.context = context;
        this.deleteListener = deleteListener;
    }

    @NonNull
    @Override
    public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(AppContainer.getInstance().getModuleRes().getLayout(R.layout.anchor_monitor_item_layout), parent, false);
        return new UserViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UserViewHolder holder, int position) {
        User user = getItem(position);
        try {
            holder.bind(user);
        } catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }
    // 添加设置当前检测用户的方法
    @SuppressLint("NotifyDataSetChanged")
    public void setCurrentCheckingUid(String uid) {
        this.currentCheckingUid = uid;
        notifyDataSetChanged(); // 通知列表更新
    }
    public class UserViewHolder extends RecyclerView.ViewHolder {
        // 视图引用
        LinearLayout anchor_monitor_item_root;
        ImageView avatar;
        TextView userName,liveId,uid,uuid,encUid;
        CheckBox strongRemind, voiceRemind, joinLive, avatarDownload;

        UserViewHolder(View itemView) {
            super(itemView);
            // 初始化视图
            anchor_monitor_item_root = itemView.findViewById(R.id.anchor_monitor_item_root);
            avatar = itemView.findViewById(R.id.avatar);
            userName = itemView.findViewById(R.id.user_name);
            uid = itemView.findViewById(R.id.uid);
            uuid = itemView.findViewById(R.id.uuid);
            encUid = itemView.findViewById(R.id.enc_uid);
            liveId = itemView.findViewById(R.id.live_id);
            strongRemind = itemView.findViewById(R.id.strong_remind);
            voiceRemind = itemView.findViewById(R.id.voice_remind);
            joinLive = itemView.findViewById(R.id.join_live);
            avatarDownload = itemView.findViewById(R.id.avatar_download);
        }
        @SuppressLint({"SetTextI18n", "UseCompatLoadingForDrawables", "DiscouragedApi"})
        void bind(User user) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
            // 先移除所有监听器，避免触发不必要的回调
            strongRemind.setOnCheckedChangeListener(null);
            voiceRemind.setOnCheckedChangeListener(null);
            joinLive.setOnCheckedChangeListener(null);
            avatarDownload.setOnCheckedChangeListener(null);

            XModuleResources moduleRes = AppContainer.getInstance().getModuleRes();
            // 根据是否正在检测设置不同背景
            Drawable checkBgDrawable;
            if (user.getUid().equals(currentCheckingUid)) {
                // 检测中的颜色（例如橙色）
                checkBgDrawable = new Gradient()
                        .setColorLeft("#FFA500")
                        .setColorRight("#FF8C00")
                        .setRadius(14f)
                        .build();
            } else {
                // 正常颜色
                checkBgDrawable = moduleRes.getDrawable(R.drawable.card_background,null);
            }
            anchor_monitor_item_root.setBackground(checkBgDrawable);
            int iconUserFace = moduleRes.getIdentifier("icon_user_face","drawable","package com.zjfgh.bluedhook.simple;");
            // 用户头像
            Glide.with(context)
                    .load(user.getAvatar())
                    .placeholder(iconUserFace)
                    .error(0)
                    .into(avatar);

            // 用户信息
            userName.setText(user.getName());
            liveId.setText("直播ID " + user.getLive());
            uid.setText("注册ID " + user.getUid());
            uuid.setText("用户ID " + user.getUnion_uid());

            if (user.getEnc_uid().isEmpty()){
                encUid.setVisibility(View.GONE);
            }else {
                encUid.setVisibility(View.VISIBLE);
                encUid.setText("加密ID " + user.getEnc_uid());
            }

            // 设置复选框状态（在设置监听器之前）
            strongRemind.setChecked(user.isStrongRemind());
            voiceRemind.setChecked(user.isVoiceRemind());
            joinLive.setChecked(user.isJoinLive());
            avatarDownload.setChecked(user.isAvatarDownload());

            // 设置监听器
            strongRemind.setOnCheckedChangeListener((buttonView, isChecked) -> {
                user.setStrongRemind(isChecked);
                dbManger.updateUserStrongRemind(user.getUid(), isChecked);
            });

            voiceRemind.setOnCheckedChangeListener((buttonView, isChecked) -> {
                user.setVoiceRemind(isChecked);
                dbManger.updateUserVoiceRemind(user.getUid(), isChecked);
            });

            joinLive.setOnCheckedChangeListener((buttonView, isChecked) -> {
                user.setJoinLive(isChecked);
                dbManger.updateUserJoinLive(user.getUid(), isChecked);
            });

            avatarDownload.setOnCheckedChangeListener((buttonView, isChecked) -> {
                user.setAvatarDownload(isChecked);
                dbManger.updateUserAvatarDownload(user.getUid(), isChecked);
            });

            // 添加长按监听
            itemView.setOnLongClickListener(v -> {
                DeleteConfirmationDialog.show(context, user.getName(), new DeleteConfirmationDialog.DeleteConfirmationListener() {
                    @Override
                    public void onConfirmDelete() {
                        if (deleteListener != null) {
                            try {
                                deleteListener.onUserDelete(user);
                            } catch (JSONException e) {
                                Log.e("UserListAdapter:161",e.toString());
                            }
                        }
                    }
                    @Override
                    public void onCancel() {
                        // 用户取消操作，不做任何处理
                    }
                });
                return true; // 消费长按事件
            });
        }
    }
    static class UserDiffCallback extends DiffUtil.ItemCallback<User> {
        @Override
        public boolean areItemsTheSame(@NonNull User oldItem, @NonNull User newItem) {
            return oldItem.getUid().equals(newItem.getUid());
        }

        @Override
        public boolean areContentsTheSame(@NonNull User oldItem, @NonNull User newItem) {
            // 添加复选框状态的比较
            return oldItem.getName().equals(newItem.getName())
                    && oldItem.getLive().equals(newItem.getLive())
                    && oldItem.isStrongRemind() == newItem.isStrongRemind()
                    && oldItem.isVoiceRemind() == newItem.isVoiceRemind()
                    && oldItem.isJoinLive() == newItem.isJoinLive()
                    && oldItem.isAvatarDownload() == newItem.isAvatarDownload();
        }
        @Override
        public Object getChangePayload(@NonNull User oldItem, @NonNull User newItem) {
            // 可选：实现局部更新逻辑
            return super.getChangePayload(oldItem, newItem);
        }
    }
}