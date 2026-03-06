package com.derucci.iot.common.core.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 校验工具类
 */
public class ValidationUtils {

    // 手机号正则（中国大陆）
    private static final Pattern CHINA_PHONE_PATTERN = Pattern.compile("^1[3-9]\\d{9}$");

    // 邮箱正则
    private static final Pattern EMAIL_PATTERN = Pattern.compile(
        "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$"
    );

    // URL正则
    private static final Pattern URL_PATTERN = Pattern.compile(
        "^(https?|http)://[a-zA-Z0-9.-]+(:\\d+)?(/.*)?$"
    );

    // 身份证号正则（18位）
    private static final Pattern ID_CARD_PATTERN = Pattern.compile(
        "^[1-9]\\d{5}(18|19|20)\\d{2}((0[1-9])|(1[0-2]))(([0-2][1-9])|10|20|30|31)\\d{3}[0-9Xx]$"
    );

    // ==================== isEmail ====================

    /**
     * 校验邮箱格式
     */
    public static boolean isEmail(String str) {
        if (str == null || str.isEmpty()) {
            return false;
        }
        return EMAIL_PATTERN.matcher(str).matches();
    }

    // ==================== isPhone ====================

    /**
     * 校验中国手机号格式
     */
    public static boolean isPhone(String str) {
        if (str == null || str.isEmpty()) {
            return false;
        }
        return CHINA_PHONE_PATTERN.matcher(str).matches();
    }

    // ==================== isUrl ====================

    /**
     * 校验URL格式
     */
    public static boolean isUrl(String str) {
        if (str == null || str.isEmpty()) {
            return false;
        }
        return URL_PATTERN.matcher(str).matches();
    }

    // ==================== isNumeric ====================

    /**
     * 判断是否为纯数字
     */
    public static boolean isNumeric(String str) {
        if (str == null || str.isEmpty()) {
            return false;
        }
        return str.matches("\\d+");
    }

    // ==================== isAlpha ====================

    /**
     * 判断是否为纯字母
     */
    public static boolean isAlpha(String str) {
        if (str == null || str.isEmpty()) {
            return false;
        }
        return str.matches("[a-zA-Z]+");
    }

    // ==================== isAlphanumeric ====================

    /**
     * 判断是否为字母数字组合
     */
    public static boolean isAlphanumeric(String str) {
        if (str == null || str.isEmpty()) {
            return false;
        }
        return str.matches("[a-zA-Z0-9]+");
    }

    // ==================== isPositiveNumber ====================

    /**
     * 判断是否为正数
     */
    public static boolean isPositiveNumber(String str) {
        if (str == null || str.isEmpty()) {
            return false;
        }
        try {
            double num = Double.parseDouble(str);
            return num > 0;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    // ==================== isInteger ====================

    /**
     * 判断是否为整数
     */
    public static boolean isInteger(String str) {
        if (str == null || str.isEmpty()) {
            return false;
        }
        return str.matches("-?\\d+");
    }

    // ==================== isIdCard ====================

    /**
     * 校验中国身份证号格式
     */
    public static boolean isIdCard(String str) {
        if (str == null || str.isEmpty()) {
            return false;
        }
        return ID_CARD_PATTERN.matcher(str).matches();
    }

    // ==================== isLength ====================

    /**
     * 校验字符串长度范围
     */
    public static boolean isLength(String str, int min, int max) {
        if (str == null) {
            return false;
        }
        int length = str.length();
        return length >= min && length <= max;
    }

    // ==================== matches ====================

    /**
     * 正则表达式匹配
     */
    public static boolean matches(String str, String pattern) {
        if (str == null || pattern == null) {
            return false;
        }
        return str.matches(pattern);
    }

    /**
     * 正则表达式匹配（自定义Pattern）
     */
    public static boolean matches(String str, Pattern pattern) {
        if (str == null || pattern == null) {
            return false;
        }
        Matcher matcher = pattern.matcher(str);
        return matcher.matches();
    }
}
