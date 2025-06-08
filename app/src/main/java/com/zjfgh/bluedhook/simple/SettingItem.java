package com.zjfgh.bluedhook.simple;

public class SettingItem {
    private int id;
    private int functionId;
    private String functionName;
    private boolean switchOn;
    private String description;
    private String extraData;
    private String extraDataHint;

    // 构造函数
    public SettingItem() {}

    public SettingItem(int functionId,String functionName, boolean switchOn, String description,
                       String extraData, String extraDataHint) {
        this.functionId = functionId;
        this.functionName = functionName;
        this.switchOn = switchOn;
        this.description = description;
        this.extraData = extraData;
        this.extraDataHint = extraDataHint;
    }

    // Getter和Setter方法
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
    public int getFunctionId() {
        return functionId;
    }
    public void setFunctionId(int functionId) {
        this.functionId = functionId;
    }
    public String getFunctionName() {
        return functionName;
    }

    public void setFunctionName(String functionName) {
        this.functionName = functionName;
    }

    public boolean isSwitchOn() {
        return switchOn;
    }

    public void setSwitchOn(boolean switchOn) {
        this.switchOn = switchOn;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getExtraData() {
        return extraData;
    }

    public void setExtraData(String extraData) {
        this.extraData = extraData;
    }

    public String getExtraDataHint() {
        return extraDataHint;
    }

    public void setExtraDataHint(String extraDataHint) {
        this.extraDataHint = extraDataHint;
    }
}
