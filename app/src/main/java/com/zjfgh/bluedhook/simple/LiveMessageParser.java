package com.zjfgh.bluedhook.simple;

import static com.zjfgh.bluedhook.simple.ModuleTools.charSequence;

import android.text.SpannableString;

import java.util.*;
import java.util.regex.*;

public class LiveMessageParser {

    private static final String WORD_PATTERN = "@\\(word:([^)]+)\\)";

    public static class ParseResult {
        private final String originalString;
        private final String charSequence;
        private final List<String> keywords;
        private final MessageType messageType;

        public ParseResult(String originalString, List<String> keywords, MessageType messageType) {
            this.originalString = originalString;
            this.keywords = keywords;
            this.messageType = messageType;
            Pattern word = Pattern.compile("@\\(word:([\\s\\S]*?)\\)");
            this.charSequence = charSequence(word, new SpannableString(originalString), "#ffffff").toString();
        }

        public String getOriginalString() {
            return originalString;
        }

        public List<String> getKeywords() {
            return keywords;
        }

        public MessageType getMessageType() {
            return messageType;
        }

        public String getCharSequence() {
            return charSequence;
        }
    }

    public enum MessageType {
        // 优化点：使用更灵活的正则表达式
        CHAMELEON_LIFE(".*(插画师|医生|拳击手|机长|超级影帝).*"),
        A_DESERT_DREAM(".*(烛光|花灯|敦煌恋歌|走进敦煌|九色神鹿|舞动敦煌|飞天传说|隐藏款).*"),
        HOLY_SWORDSMAN(".*(神圣体魄|黄金手套|黄金战靴|黄金头盔|黄金铠甲|圣剑降临).*"),
        PRIMARY_TREASURE(".*初级宝藏.*"),
        ADVANCED_TREASURE(".*高级宝藏.*"),
        GLOWING_TREASURE(".*璀璨宝藏.*"),
        MULTIPLIER_REWARD("恭喜.*触发.*倍.*获得.*豆.*"),
        UNKNOWN("");

        private final String regexPattern;

        MessageType(String regexPattern) {
            this.regexPattern = regexPattern;
        }

        public String getRegexPattern() {
            return regexPattern;
        }
    }

    // 预编译正则，提高性能
    private static final Map<MessageType, Pattern> MESSAGE_PATTERNS = new EnumMap<>(MessageType.class);

    static {
        for (MessageType type : MessageType.values()) {
            if (type != MessageType.UNKNOWN) {
                MESSAGE_PATTERNS.put(type, Pattern.compile(type.getRegexPattern()));
            }
        }
    }

    public ParseResult parse(String message) {
        List<String> keywords = extractKeywords(message);
        Pattern word = Pattern.compile("@\\(word:([\\s\\S]*?)\\)");
        CharSequence charSequence = charSequence(word, new SpannableString(message), "#ffffff");
        MessageType messageType = determineMessageType(charSequence.toString());
        return new ParseResult(message, keywords, messageType);
    }

    private List<String> extractKeywords(String message) {
        List<String> keywords = new ArrayList<>();
        Matcher matcher = Pattern.compile(WORD_PATTERN).matcher(message);

        while (matcher.find()) {
            keywords.add(matcher.group(1));
        }
        return keywords;
    }

    private MessageType determineMessageType(String message) {
        return MESSAGE_PATTERNS.entrySet().stream()
                .filter(entry -> entry.getValue().matcher(message).matches())
                .findFirst()
                .map(Map.Entry::getKey)
                .orElse(MessageType.UNKNOWN);
    }
}
