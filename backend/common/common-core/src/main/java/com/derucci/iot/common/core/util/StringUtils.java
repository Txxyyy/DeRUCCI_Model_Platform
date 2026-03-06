package com.derucci.iot.common.core.util;

/**
 * 字符串工具类
 */
public class StringUtils {

    // ==================== isBlank ====================

    /**
     * 判断字符串是否为空（null、空字符串或仅包含空格）
     */
    public static boolean isBlank(String str) {
        return str == null || str.trim().isEmpty();
    }

    // ==================== isNotBlank ====================

    /**
     * 判断字符串是否非空
     */
    public static boolean isNotBlank(String str) {
        return !isBlank(str);
    }

    // ==================== trim ====================

    /**
     * 去除字符串两端空格，null返回空字符串
     */
    public static String trim(String str) {
        return str == null ? "" : str.trim();
    }

    // ==================== isEmpty ====================

    /**
     * 判断字符串是否为空（null或空字符串）
     */
    public static boolean isEmpty(String str) {
        return str == null || str.isEmpty();
    }

    // ==================== isNotEmpty ====================

    /**
     * 判断字符串是否非空
     */
    public static boolean isNotEmpty(String str) {
        return !isEmpty(str);
    }

    // ==================== defaultIfEmpty ====================

    /**
     * 如果字符串为空，返回默认值
     */
    public static String defaultIfEmpty(String str, String defaultStr) {
        return isEmpty(str) ? defaultStr : str;
    }

    // ==================== equals ====================

    /**
     * 比较两个字符串是否相等
     */
    public static boolean equals(String str1, String str2) {
        if (str1 == null && str2 == null) {
            return true;
        }
        if (str1 == null || str2 == null) {
            return false;
        }
        return str1.equals(str2);
    }

    // ==================== contains ====================

    /**
     * 判断字符串是否包含子串
     */
    public static boolean contains(String str, String searchStr) {
        if (str == null || searchStr == null) {
            return false;
        }
        return str.contains(searchStr);
    }

    // ==================== startsWith ====================

    /**
     * 判断字符串是否以指定前缀开始
     */
    public static boolean startsWith(String str, String prefix) {
        if (str == null || prefix == null) {
            return false;
        }
        return str.startsWith(prefix);
    }

    // ==================== endsWith ====================

    /**
     * 判断字符串是否以指定后缀结束
     */
    public static boolean endsWith(String str, String suffix) {
        if (str == null || suffix == null) {
            return false;
        }
        return str.endsWith(suffix);
    }

    // ==================== capitalize ====================

    /**
     * 首字母大写
     */
    public static String capitalize(String str) {
        if (isEmpty(str)) {
            return str;
        }
        return str.substring(0, 1).toUpperCase() + str.substring(1);
    }

    // ==================== uncapitalize ====================

    /**
     * 首字母小写
     */
    public static String uncapitalize(String str) {
        if (isEmpty(str)) {
            return str;
        }
        return str.substring(0, 1).toLowerCase() + str.substring(1);
    }

    // ==================== toCamelCase ====================

    /**
     * 下划线转驼峰命名
     * hello_world_test -> helloWorldTest
     */
    public static String toCamelCase(String str) {
        if (isEmpty(str)) {
            return str;
        }
        StringBuilder result = new StringBuilder();
        boolean capitalizeNext = false;

        for (int i = 0; i < str.length(); i++) {
            char c = str.charAt(i);
            if (c == '_') {
                capitalizeNext = true;
            } else if (capitalizeNext) {
                result.append(Character.toUpperCase(c));
                capitalizeNext = false;
            } else {
                result.append(Character.toLowerCase(c));
            }
        }
        return result.toString();
    }

    // ==================== toSnakeCase ====================

    /**
     * 驼峰转下划线命名
     * helloWorldTest -> hello_world_test
     */
    public static String toSnakeCase(String str) {
        if (isEmpty(str)) {
            return str;
        }
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < str.length(); i++) {
            char c = str.charAt(i);
            if (Character.isUpperCase(c)) {
                if (i > 0) {
                    result.append('_');
                }
                result.append(Character.toLowerCase(c));
            } else {
                result.append(c);
            }
        }
        return result.toString();
    }
}
