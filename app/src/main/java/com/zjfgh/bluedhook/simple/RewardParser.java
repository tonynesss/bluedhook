package com.zjfgh.bluedhook.simple;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RewardParser {
    private String nickname;
    private String multiplier;
    private String beans;

    // 构造函数，传入待解析的字符串
    public RewardParser(String input) {
        if (isValidRewardString(input)) {
            parseInput(input);
        }
    }

    // 检查字符串是否包含 "恭喜"、"触发"、"倍"、"获得"、"豆"（按顺序）
    private boolean isValidRewardString(String input) {
        String[] requiredKeywords = {"恭喜", "触发", "倍", "获得", "豆"};
        int lastIndex = -1;

        for (String keyword : requiredKeywords) {
            int currentIndex = input.indexOf(keyword, lastIndex + 1);
            if (currentIndex == -1) {
                return false; // 关键字缺失或顺序不对
            }
            lastIndex = currentIndex;
        }
        return true;
    }

    // 解析 @(word:xxx) 部分
    private void parseInput(String input) {
        Pattern pattern = Pattern.compile("@\\(word:([^)]+)\\)");
        Matcher matcher = pattern.matcher(input);
        String[] temp = new String[3];
        int index = 0;
        while (matcher.find() && index < 3) {
            temp[index++] = matcher.group(1);
        }
        if (index >= 3) {
            this.nickname = temp[0];
            this.multiplier = temp[1];
            this.beans = temp[2];
        }
    }

    // Getter 方法
    public String getNickname() {
        return nickname != null ? nickname : "";
    }

    public String getMultiplier() {
        return multiplier != null ? multiplier : "";
    }

    public String getBeans() {
        return beans != null ? beans : "";
    }

    // 检查是否解析成功（三个字段都不为 null）
    public boolean isParsedSuccessfully() {
        return nickname != null && multiplier != null && beans != null;
    }
}