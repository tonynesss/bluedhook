package com.zjfgh.bluedhook.simple;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import androidx.annotation.NonNull;
import java.util.ArrayList;
import java.util.List;

public class SQLiteManagement extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "AppDatabase.db";
    private static final int DATABASE_VERSION = 1;

    // 单例实例
    private static SQLiteManagement instance;

    // 设置表常量
    private static final String TABLE_SETTINGS = "settings";
    private static final String COLUMN_SETTING_ID = "id";
    private static final String COLUMN_FUNCTION_ID = "function_id";
    private static final String COLUMN_FUNCTION_NAME = "function_name";
    private static final String COLUMN_SWITCH_STATE = "switch_state";
    private static final String COLUMN_DESCRIPTION = "description";
    private static final String COLUMN_EXTRA_DATA = "extra_data";
    private static final String COLUMN_EXTRA_DATA_HINT = "extra_data_hint";

    // 用户表常量
    private static final String TABLE_USERS = "users";
    private static final String COLUMN_USER_ID = "id";
    private static final String COLUMN_USER_NAME = "user_name";
    private static final String COLUMN_AVATAR_PATH = "avatar_path";
    private static final String COLUMN_LIVE_ID = "live_id";
    private static final String COLUMN_UID = "uid";
    private static final String COLUMN_UUID = "uuid";
    private static final String COLUMN_ENC_UID = "enc_uid";
    private static final String COLUMN_STRONG_REMIND = "strong_remind";
    private static final String COLUMN_VOICE_REMINDED = "voice_reminded";
    private static final String COLUMN_JOINED_LIVE = "joined_live";
    private static final String COLUMN_AVATAR_DOWNLOAD = "avatar_downloaded";
    private static final String COLUMN_VOICE_REMIND = "voice_remind";
    private static final String COLUMN_JOIN_LIVE = "join_live";

    // 私有构造函数
    private SQLiteManagement(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    // 获取单例实例
    public static synchronized SQLiteManagement getInstance() {
        if (instance == null) {
            // 使用应用上下文避免内存泄漏
            Context appContext = AppContainer.getInstance().getBluedContext();
            instance = new SQLiteManagement(appContext);
        }
        return instance;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // 创建设置表
        String CREATE_SETTINGS_TABLE = "CREATE TABLE " + TABLE_SETTINGS + "("
                + COLUMN_SETTING_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + COLUMN_FUNCTION_ID + " INTEGER UNIQUE,"  // 新增的功能唯一标识ID
                + COLUMN_FUNCTION_NAME + " TEXT,"
                + COLUMN_SWITCH_STATE + " INTEGER,"
                + COLUMN_DESCRIPTION + " TEXT,"
                + COLUMN_EXTRA_DATA + " TEXT,"
                + COLUMN_EXTRA_DATA_HINT + " TEXT"
                + ")";
        db.execSQL(CREATE_SETTINGS_TABLE);

        // 创建用户表
        String CREATE_USERS_TABLE = "CREATE TABLE " + TABLE_USERS + "("
                + COLUMN_USER_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + COLUMN_USER_NAME + " TEXT,"
                + COLUMN_AVATAR_PATH + " TEXT,"
                + COLUMN_LIVE_ID + " TEXT,"
                + COLUMN_UID + " TEXT,"
                + COLUMN_UUID + " TEXT,"
                + COLUMN_ENC_UID + " TEXT,"
                + COLUMN_STRONG_REMIND + " INTEGER,"
                + COLUMN_VOICE_REMIND + " INTEGER,"
                + COLUMN_VOICE_REMINDED + " INTEGER,"
                + COLUMN_JOIN_LIVE + " INTEGER,"    // 新增字段
                + COLUMN_JOINED_LIVE + " INTEGER,"
                + COLUMN_AVATAR_DOWNLOAD + " INTEGER"
                + ")";
        db.execSQL(CREATE_USERS_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_SETTINGS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USERS);
        onCreate(db);
    }

    // ==================== 设置表操作 ====================

    // 添加或更新设置项
    public void addOrUpdateSetting(SettingItem setting) {
        try {
            // 检查是否已存在相同功能ID的设置
            SettingItem existingSetting = getSettingByFunctionId(setting.getFunctionId());
            ContentValues values = getContentValues(setting, existingSetting);
            if (existingSetting != null) {
                // 更新现有记录 - 使用功能ID作为条件
                this.getWritableDatabase().update(TABLE_SETTINGS, values,
                        COLUMN_FUNCTION_ID + " = ?",
                        new String[]{String.valueOf(setting.getFunctionId())});
            } else {
                // 插入新记录
                this.getWritableDatabase().insert(TABLE_SETTINGS, null, values);
            }
        } finally {
            this.getWritableDatabase().close();
        }
    }

    @NonNull
    private static ContentValues getContentValues(SettingItem setting, SettingItem existingSetting) {
        ContentValues values = new ContentValues();
        values.put(COLUMN_FUNCTION_NAME, setting.getFunctionName());
        values.put(COLUMN_FUNCTION_ID, setting.getFunctionId());
        // 如果设置已存在，保留原有开关状态；否则使用新设置的开关状态
        boolean switchState = existingSetting != null ? existingSetting.isSwitchOn() : setting.isSwitchOn();
        values.put(COLUMN_SWITCH_STATE, switchState ? 1 : 0);
        values.put(COLUMN_DESCRIPTION, setting.getDescription());
        values.put(COLUMN_EXTRA_DATA_HINT, setting.getExtraDataHint());
        //如果设置已存在，使用原有extraData；否则使用新设置的extraData
        String extraData = existingSetting != null ? existingSetting.getExtraData() : setting.getExtraData();
        values.put(COLUMN_EXTRA_DATA, extraData);
        return values;
    }

    // 获取所有设置项
    @SuppressLint("Range")
    public List<SettingItem> getAllSettings() {
        List<SettingItem> settingsList = new ArrayList<>();
        String selectQuery = "SELECT * FROM " + TABLE_SETTINGS;

        try (SQLiteDatabase db = this.getReadableDatabase(); Cursor cursor = db.rawQuery(selectQuery, null)) {

            if (cursor.moveToFirst()) {
                do {
                    SettingItem setting = new SettingItem();
                    setting.setId(cursor.getInt(cursor.getColumnIndex(COLUMN_SETTING_ID)));
                    setting.setFunctionId(cursor.getInt(cursor.getColumnIndex(COLUMN_FUNCTION_ID)));
                    setting.setFunctionName(cursor.getString(cursor.getColumnIndex(COLUMN_FUNCTION_NAME)));
                    setting.setSwitchOn(cursor.getInt(cursor.getColumnIndex(COLUMN_SWITCH_STATE)) == 1);
                    setting.setDescription(cursor.getString(cursor.getColumnIndex(COLUMN_DESCRIPTION)));
                    setting.setExtraData(cursor.getString(cursor.getColumnIndex(COLUMN_EXTRA_DATA)));
                    setting.setExtraDataHint(cursor.getString(cursor.getColumnIndex(COLUMN_EXTRA_DATA_HINT)));

                    settingsList.add(setting);
                } while (cursor.moveToNext());
            }
        }
        return settingsList;
    }

    // 根据功能ID获取设置项
    @SuppressLint("Range")
    public SettingItem getSettingByFunctionId(int functionId) {
        SQLiteDatabase db = this.getReadableDatabase();
        SettingItem setting = null;
        Cursor cursor = null;
        try {
            cursor = db.query(TABLE_SETTINGS,
                    new String[]{COLUMN_SETTING_ID,
                            COLUMN_FUNCTION_ID,
                            COLUMN_FUNCTION_NAME,
                            COLUMN_SWITCH_STATE,
                            COLUMN_DESCRIPTION,
                            COLUMN_EXTRA_DATA,
                            COLUMN_EXTRA_DATA_HINT},
                    COLUMN_FUNCTION_ID + " = ?",
                    new String[]{String.valueOf(functionId)},
                    null, null, null);

            if (cursor != null && cursor.moveToFirst()) {
                setting = new SettingItem();
                setting.setId(cursor.getInt(cursor.getColumnIndex(COLUMN_SETTING_ID)));
                setting.setFunctionId(cursor.getInt(cursor.getColumnIndex(COLUMN_FUNCTION_ID)));
                setting.setFunctionName(cursor.getString(cursor.getColumnIndex(COLUMN_FUNCTION_NAME)));
                setting.setSwitchOn(cursor.getInt(cursor.getColumnIndex(COLUMN_SWITCH_STATE)) == 1);
                setting.setDescription(cursor.getString(cursor.getColumnIndex(COLUMN_DESCRIPTION)));
                setting.setExtraData(cursor.getString(cursor.getColumnIndex(COLUMN_EXTRA_DATA)));
                setting.setExtraDataHint(cursor.getString(cursor.getColumnIndex(COLUMN_EXTRA_DATA_HINT)));
            }
        } finally {
            if (cursor != null) {
                cursor.close();
            }
            db.close();
        }
        return setting;
    }

    // 更新设置项的开关状态（改为使用functionId）
    public void updateSettingSwitchState(int functionId, boolean isOn) {
        try (SQLiteDatabase db = this.getWritableDatabase()) {
            ContentValues values = new ContentValues();
            values.put(COLUMN_SWITCH_STATE, isOn ? 1 : 0);

            db.update(TABLE_SETTINGS, values,
                    COLUMN_FUNCTION_ID + " = ?",
                    new String[]{String.valueOf(functionId)});
        }
    }

    // 更新设置项的额外数据
    public void updateSettingExtraData(int functionID, String extraData) {
        try (SQLiteDatabase db = this.getWritableDatabase()) {
            ContentValues values = new ContentValues();
            values.put(COLUMN_EXTRA_DATA, extraData);

            db.update(TABLE_SETTINGS, values,
                    COLUMN_FUNCTION_ID + " = ?",
                    new String[]{String.valueOf(functionID)});
        }
    }

    // ==================== 用户表操作 ====================

    // 添加或更新用户
    public boolean addOrUpdateUser(User user) {
        SQLiteDatabase db = this.getWritableDatabase();
        boolean result;
        Cursor cursor = null;

        try {
            Log.i("UserInfoFragment", "db: " + db);
            ContentValues values = getContentValues(user);

            // 检查是否已存在相同UID的用户
            cursor = db.query(TABLE_USERS,
                    new String[]{COLUMN_USER_ID},
                    COLUMN_UID + " = ?",
                    new String[]{user.getUid()},
                    null, null, null);

            if (cursor != null && cursor.getCount() > 0) {
                // 更新现有记录
                cursor.moveToFirst();
                int rowsAffected = db.update(TABLE_USERS, values,
                        COLUMN_UID + " = ?",
                        new String[]{user.getUid()});
                result = rowsAffected > 0;
            } else {
                // 插入新记录
                long id = db.insert(TABLE_USERS, null, values);
                result = id != -1;
            }
        } finally {
            if (cursor != null) {
                cursor.close();
            }
            db.close();
        }

        return result;
    }

    @NonNull
    private static ContentValues getContentValues(User user) {
        ContentValues values = new ContentValues();
        values.put(COLUMN_USER_NAME, user.getName());
        values.put(COLUMN_AVATAR_PATH, user.getAvatar());
        values.put(COLUMN_LIVE_ID, user.getLive());
        values.put(COLUMN_UID, user.getUid());
        values.put(COLUMN_UUID, user.getUnion_uid());
        values.put(COLUMN_ENC_UID, user.getEnc_uid());
        values.put(COLUMN_STRONG_REMIND, user.isStrongRemind() ? 1 : 0);
        values.put(COLUMN_VOICE_REMINDED, user.isVoiceReminded() ? 1 : 0);
        values.put(COLUMN_JOINED_LIVE, user.isJoinedLive() ? 1 : 0);
        values.put(COLUMN_AVATAR_DOWNLOAD, user.isAvatarDownloaded() ? 1 : 0);
        values.put(COLUMN_VOICE_REMIND, user.isVoiceRemind() ? 1 : 0); // 新增字段
        values.put(COLUMN_JOIN_LIVE, user.isJoinLive() ? 1 : 0);       // 新增字段
        return values;
    }

    // 获取所有用户
    @SuppressLint("Range")
    public List<User> getAllUsers() {
        List<User> userList = new ArrayList<>();
        String selectQuery = "SELECT * FROM " + TABLE_USERS;

        try (SQLiteDatabase db = this.getReadableDatabase(); Cursor cursor = db.rawQuery(selectQuery, null)) {

            if (cursor.moveToFirst()) {
                do {
                    User user = new User();
                    user.setId(cursor.getInt(cursor.getColumnIndex(COLUMN_USER_ID)));
                    user.setName(cursor.getString(cursor.getColumnIndex(COLUMN_USER_NAME)));
                    user.setAvatar(cursor.getString(cursor.getColumnIndex(COLUMN_AVATAR_PATH)));
                    user.setLive(cursor.getString(cursor.getColumnIndex(COLUMN_LIVE_ID)));
                    user.setUid(cursor.getString(cursor.getColumnIndex(COLUMN_UID)));
                    user.setUnion_uid(cursor.getString(cursor.getColumnIndex(COLUMN_UUID)));
                    user.setEnc_uid(cursor.getString(cursor.getColumnIndex(COLUMN_ENC_UID)));
                    user.setStrongRemind(cursor.getInt(cursor.getColumnIndex(COLUMN_STRONG_REMIND)) == 1);
                    user.setVoiceReminded(cursor.getInt(cursor.getColumnIndex(COLUMN_VOICE_REMINDED)) == 1);
                    user.setJoinedLive(cursor.getInt(cursor.getColumnIndex(COLUMN_JOINED_LIVE)) == 1);
                    user.setAvatarDownloaded(cursor.getInt(cursor.getColumnIndex(COLUMN_AVATAR_DOWNLOAD)) == 1);
                    user.setVoiceRemind(cursor.getInt(cursor.getColumnIndex(COLUMN_VOICE_REMIND)) == 1); // 新增字段
                    user.setJoinLive(cursor.getInt(cursor.getColumnIndex(COLUMN_JOIN_LIVE)) == 1);       // 新增字段

                    userList.add(user);
                } while (cursor.moveToNext());
            }
        }
        return userList;
    }

    // 根据UID获取用户
    @SuppressLint("Range")
    public User getUserByUid(String uid) {
        try (SQLiteDatabase db = this.getReadableDatabase(); Cursor cursor = db.query(TABLE_USERS,
                new String[]{COLUMN_USER_ID, COLUMN_USER_NAME, COLUMN_AVATAR_PATH,
                        COLUMN_LIVE_ID, COLUMN_UID, COLUMN_UUID, COLUMN_ENC_UID,
                        COLUMN_STRONG_REMIND, COLUMN_VOICE_REMINDED,
                        COLUMN_JOINED_LIVE, COLUMN_AVATAR_DOWNLOAD,
                        COLUMN_VOICE_REMIND, COLUMN_JOIN_LIVE},
                COLUMN_UID + " = ?",
                new String[]{uid},
                null, null, null, null)) {

            if (cursor != null && cursor.moveToFirst()) {
                User user = new User();
                user.setId(cursor.getInt(cursor.getColumnIndex(COLUMN_USER_ID)));
                user.setName(cursor.getString(cursor.getColumnIndex(COLUMN_USER_NAME)));
                user.setAvatar(cursor.getString(cursor.getColumnIndex(COLUMN_AVATAR_PATH)));
                user.setLive(cursor.getString(cursor.getColumnIndex(COLUMN_LIVE_ID)));
                user.setUid(cursor.getString(cursor.getColumnIndex(COLUMN_UID)));
                user.setUnion_uid(cursor.getString(cursor.getColumnIndex(COLUMN_UUID)));
                user.setEnc_uid(cursor.getString(cursor.getColumnIndex(COLUMN_ENC_UID)));
                user.setStrongRemind(cursor.getInt(cursor.getColumnIndex(COLUMN_STRONG_REMIND)) == 1);
                user.setVoiceReminded(cursor.getInt(cursor.getColumnIndex(COLUMN_VOICE_REMINDED)) == 1);
                user.setJoinedLive(cursor.getInt(cursor.getColumnIndex(COLUMN_JOINED_LIVE)) == 1);
                user.setAvatarDownloaded(cursor.getInt(cursor.getColumnIndex(COLUMN_AVATAR_DOWNLOAD)) == 1);
                user.setVoiceRemind(cursor.getInt(cursor.getColumnIndex(COLUMN_VOICE_REMIND)) == 1);
                user.setJoinLive(cursor.getInt(cursor.getColumnIndex(COLUMN_JOIN_LIVE)) == 1);

                return user;
            }
        }
        return null;
    }

    // 更新用户头像下载状态
    public void updateUserAvatarDownload(String uid, boolean isDownloaded) {
        try (SQLiteDatabase db = this.getWritableDatabase()) {
            ContentValues values = new ContentValues();
            values.put(COLUMN_AVATAR_DOWNLOAD, isDownloaded ? 1 : 0);

            db.update(TABLE_USERS, values,
                    COLUMN_UID + " = ?",
                    new String[]{uid});
        }
    }

    // 更新用户强提醒状态
    public void updateUserStrongRemind(String uid, boolean isStrongRemind) {
        try (SQLiteDatabase db = this.getWritableDatabase()) {
            ContentValues values = new ContentValues();
            values.put(COLUMN_STRONG_REMIND, isStrongRemind ? 1 : 0);

            db.update(TABLE_USERS, values,
                    COLUMN_UID + " = ?",
                    new String[]{uid});
        }
    }

    // 更新用户加入直播状态
    public void updateUserJoinLive(String uid, boolean isJoinLive) {
        try (SQLiteDatabase db = this.getWritableDatabase()) {
            ContentValues values = new ContentValues();
            values.put(COLUMN_JOIN_LIVE, isJoinLive ? 1 : 0);

            db.update(TABLE_USERS, values,
                    COLUMN_UID + " = ?",
                    new String[]{uid});
        }
    }

    // 更新用户语音提醒状态
    public void updateUserVoiceRemind(String uid, boolean isVoiceRemind) {
        try (SQLiteDatabase db = this.getWritableDatabase()) {
            ContentValues values = new ContentValues();
            values.put(COLUMN_VOICE_REMIND, isVoiceRemind ? 1 : 0);

            db.update(TABLE_USERS, values,
                    COLUMN_UID + " = ?",
                    new String[]{uid});
        }
    }
    public void updateUserVoiceReminded(String uid, boolean isVoiceReminded) {
        try (SQLiteDatabase db = this.getWritableDatabase()) {
            ContentValues values = new ContentValues();
            values.put(COLUMN_VOICE_REMINDED, isVoiceReminded ? 1 : 0);

            db.update(TABLE_USERS, values,
                    COLUMN_UID + " = ?",
                    new String[]{uid});
        }
    }
    // 删除用户
    /**
     * 删除指定UID的用户
     * @param uid 要删除的用户UID
     * @return 是否成功删除 (true表示成功删除至少1行，false表示删除失败或用户不存在)
     */
    public boolean deleteUser(String uid) {
        try (SQLiteDatabase db = this.getWritableDatabase()) {
            // 执行删除并获取受影响的行数
            int rowsAffected = db.delete(TABLE_USERS,
                    COLUMN_UID + " = ?",
                    new String[]{uid});

            // 返回是否成功删除(至少影响1行)
            return rowsAffected > 0;
        } catch (Exception e) {
            // 记录错误日志
            Log.e("SQLiteManagement", "删除用户失败: " + e.getMessage());
            return false;
        }
    }
    public void updateUserLive(String uid, long live) {
        try (SQLiteDatabase db = this.getWritableDatabase()) {
            ContentValues values = new ContentValues();
            values.put(COLUMN_LIVE_ID, live);

            db.update(TABLE_USERS, values,
                    COLUMN_UID + " = ?",
                    new String[]{uid});
        }
    }
}