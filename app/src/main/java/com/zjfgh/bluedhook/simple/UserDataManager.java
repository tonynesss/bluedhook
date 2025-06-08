package com.zjfgh.bluedhook.simple;

import androidx.lifecycle.MutableLiveData;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class UserDataManager {
    private static UserDataManager instance;
    private final MutableLiveData<List<User>> userLiveData = new MutableLiveData<>();
    private final SQLiteManagement dbManager = SQLiteManagement.getInstance();

    private UserDataManager() {}

    public static synchronized UserDataManager getInstance() {
        if (instance == null) {
            instance = new UserDataManager();
        }
        return instance;
    }

    public MutableLiveData<List<User>> getUserLiveData() {
        return userLiveData;
    }

    public void refreshUsers() {
        List<User> users = dbManager.getAllUsers();
        userLiveData.postValue(users);
    }

    public void removeUser(String uid) {

        // 创建原始列表的副本（防御性拷贝）
        List<User> copyList = getSafeUserList();
        synchronized (this) {
            Iterator<User> iterator = copyList.iterator();
            while (iterator.hasNext()) {
                User user = iterator.next();
                if (user.getUid().equals(uid)) {
                    iterator.remove();
                    break;
                }
            }
            userLiveData.postValue(new ArrayList<>(copyList));
        }
    }
    public void addUser(User user) {
        // 创建原始列表的副本（防御性拷贝）
        List<User> copyList = getSafeUserList();
        synchronized (this) {
            copyList.add(user);
            userLiveData.postValue(new ArrayList<>(copyList));
        }
    }
    public List<User> getSafeUserList() {
        List<User> current = userLiveData.getValue();
        return current != null ? new ArrayList<>(current) : new ArrayList<>();
    }
}