package com.zjfgh.bluedhook.simple;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.XModuleResources;

public class AppContainer {
    @SuppressLint("StaticFieldLeak")
    private static volatile AppContainer instance;
    private Context bluedContext;
    private ClassLoader classLoader;
    private String modulePath;
    private XModuleResources moduleRes;
    // 私有构造
    private AppContainer() {
        // 初始化工作
    }
    // 双重校验锁单例
    public static AppContainer getInstance() {
        if (instance == null) {
            synchronized (AppContainer.class) {
                if (instance == null) {
                    instance = new AppContainer();
                }
            }
        }
        return instance;
    }
    public void setBluedContext(Context bluedContext){
        this.bluedContext = bluedContext;
    }
    public Context getBluedContext(){
        return bluedContext;
    }
    public void setClassLoader(ClassLoader classLoader){
        this.classLoader = classLoader;
    }

    public ClassLoader getClassLoader() {
        return classLoader;
    }

    public void setModulePath(String modulePath) {
        this.modulePath = modulePath;
    }
    public String getModulePath() {
        return modulePath;
    }
    public void setModuleRes(XModuleResources moduleRes) {
        this.moduleRes = moduleRes;
    }
    public XModuleResources getModuleRes() {
        return moduleRes;
    }
}
