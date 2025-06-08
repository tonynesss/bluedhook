package com.zjfgh.bluedhook.simple.module;

import com.alibaba.fastjson.annotation.JSONField;

public class ErSanSanModelExtra {
    @JSONField(name = "color")
    private String color;
    @JSONField(name = "tail_image")
    private String tailImage;
    @JSONField(name = "lid")
    private String lid;
    @JSONField(name = "icon_image")
    private String iconImage;
    @JSONField(name = "highlight_color")
    private String highlightColor;
    @JSONField(name = "disable_redirect")
    private Integer disableRedirect;
    @JSONField(name = "scene")
    private Integer scene;
    @JSONField(name = "link_type")
    private Integer linkType;
    @JSONField(name = "head_image")
    private String headImage;
    @JSONField(name = "uid")
    private String uid;
    @JSONField(name = "back_image")
    private String backImage;
    @JSONField(name = "contents")
    private String contents;
    @JSONField(name = "effect")
    private Integer effect;
    @JSONField(name = "position")
    private Integer position;

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public String getTailImage() {
        return tailImage;
    }

    public void setTailImage(String tailImage) {
        this.tailImage = tailImage;
    }

    public String getLid() {
        return lid;
    }

    public void setLid(String lid) {
        this.lid = lid;
    }

    public String getIconImage() {
        return iconImage;
    }

    public void setIconImage(String iconImage) {
        this.iconImage = iconImage;
    }

    public String getHighlightColor() {
        return highlightColor;
    }

    public void setHighlightColor(String highlightColor) {
        this.highlightColor = highlightColor;
    }

    public Integer getDisableRedirect() {
        return disableRedirect;
    }

    public void setDisableRedirect(Integer disableRedirect) {
        this.disableRedirect = disableRedirect;
    }

    public Integer getScene() {
        return scene;
    }

    public void setScene(Integer scene) {
        this.scene = scene;
    }

    public Integer getLinkType() {
        return linkType;
    }

    public void setLinkType(Integer linkType) {
        this.linkType = linkType;
    }

    public String getHeadImage() {
        return headImage;
    }

    public void setHeadImage(String headImage) {
        this.headImage = headImage;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getBackImage() {
        return backImage;
    }

    public void setBackImage(String backImage) {
        this.backImage = backImage;
    }

    public String getContents() {
        return contents;
    }

    public void setContents(String contents) {
        this.contents = contents;
    }

    public Integer getEffect() {
        return effect;
    }

    public void setEffect(Integer effect) {
        this.effect = effect;
    }

    public Integer getPosition() {
        return position;
    }

    public void setPosition(Integer position) {
        this.position = position;
    }
}
