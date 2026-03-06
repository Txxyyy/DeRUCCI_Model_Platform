package com.derucci.iot.common.core.util;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * StringUtils字符串工具类测试
 * TDD: 先写测试，测试失败后再实现代码
 */
@DisplayName("StringUtils字符串工具类测试")
class StringUtilsTest {

    // ==================== isBlank 测试 ====================

    @Test
    @DisplayName("isBlank-空字符串返回true")
    void should_return_true_when_string_is_empty() {
        assertThat(StringUtils.isBlank("")).isTrue();
    }

    @Test
    @DisplayName("isBlank-null返回true")
    void should_return_true_when_string_is_null() {
        assertThat(StringUtils.isBlank(null)).isTrue();
    }

    @Test
    @DisplayName("isBlank-空格返回true")
    void should_return_true_when_string_is_whitespace() {
        assertThat(StringUtils.isBlank("   ")).isTrue();
    }

    @Test
    @DisplayName("isBlank-普通字符串返回false")
    void should_return_false_when_string_has_content() {
        assertThat(StringUtils.isBlank("hello")).isFalse();
    }

    // ==================== isNotBlank 测试 ====================

    @Test
    @DisplayName("isNotBlank-普通字符串返回true")
    void should_return_true_when_string_has_content_for_isNotBlank() {
        assertThat(StringUtils.isNotBlank("hello")).isTrue();
    }

    @Test
    @DisplayName("isNotBlank-空字符串返回false")
    void should_return_false_when_string_is_empty_for_isNotBlank() {
        assertThat(StringUtils.isNotBlank("")).isFalse();
    }

    @Test
    @DisplayName("isNotBlank-null返回false")
    void should_return_false_when_string_is_null_for_isNotBlank() {
        assertThat(StringUtils.isNotBlank(null)).isFalse();
    }

    // ==================== trim 测试 ====================

    @Test
    @DisplayName("trim-去除空格")
    void should_trim_whitespace() {
        assertThat(StringUtils.trim("  hello  ")).isEqualTo("hello");
    }

    @Test
    @DisplayName("trim-null返回空字符串")
    void should_return_empty_string_when_trim_null() {
        assertThat(StringUtils.trim(null)).isEqualTo("");
    }

    @Test
    @DisplayName("trim-只有空格返回空字符串")
    void should_return_empty_string_when_trim_only_whitespace() {
        assertThat(StringUtils.trim("   ")).isEqualTo("");
    }

    // ==================== isEmpty 测试 ====================

    @Test
    @DisplayName("isEmpty-空字符串返回true")
    void should_return_true_when_empty_for_isEmpty() {
        assertThat(StringUtils.isEmpty("")).isTrue();
    }

    @Test
    @DisplayName("isEmpty-null返回true")
    void should_return_true_when_null_for_isEmpty() {
        assertThat(StringUtils.isEmpty(null)).isTrue();
    }

    @Test
    @DisplayName("isEmpty-空格字符串返回false")
    void should_return_false_when_whitespace_for_isEmpty() {
        assertThat(StringUtils.isEmpty("   ")).isFalse();
    }

    // ==================== isNotEmpty 测试 ====================

    @Test
    @DisplayName("isNotEmpty-普通字符串返回true")
    void should_return_true_when_string_has_content_for_isNotEmpty() {
        assertThat(StringUtils.isNotEmpty("hello")).isTrue();
    }

    @Test
    @DisplayName("isNotEmpty-空格字符串返回true")
    void should_return_true_when_whitespace_for_isNotEmpty() {
        assertThat(StringUtils.isNotEmpty("   ")).isTrue();
    }

    // ==================== defaultIfEmpty 测试 ====================

    @Test
    @DisplayName("defaultIfEmpty-空字符串返回默认值")
    void should_return_default_when_empty() {
        assertThat(StringUtils.defaultIfEmpty("", "default")).isEqualTo("default");
    }

    @Test
    @DisplayName("defaultIfEmpty-null返回默认值")
    void should_return_default_when_null() {
        assertThat(StringUtils.defaultIfEmpty(null, "default")).isEqualTo("default");
    }

    @Test
    @DisplayName("defaultIfEmpty-有内容返回原值")
    void should_return_original_when_has_content() {
        assertThat(StringUtils.defaultIfEmpty("hello", "default")).isEqualTo("hello");
    }

    // ==================== equals 测试 ====================

    @Test
    @DisplayName("equals-相同字符串返回true")
    void should_return_true_when_equals() {
        assertThat(StringUtils.equals("hello", "hello")).isTrue();
    }

    @Test
    @DisplayName("equals-不同字符串返回false")
    void should_return_false_when_not_equals() {
        assertThat(StringUtils.equals("hello", "world")).isFalse();
    }

    @Test
    @DisplayName("equals-null比较")
    void should_return_false_when_compare_null() {
        assertThat(StringUtils.equals("hello", null)).isFalse();
        assertThat(StringUtils.equals(null, "hello")).isFalse();
        assertThat(StringUtils.equals(null, null)).isTrue();
    }

    // ==================== contains 测试 ====================

    @Test
    @DisplayName("contains-包含子串返回true")
    void should_return_true_when_contains() {
        assertThat(StringUtils.contains("hello world", "world")).isTrue();
    }

    @Test
    @DisplayName("contains-不包含子串返回false")
    void should_return_false_when_not_contains() {
        assertThat(StringUtils.contains("hello", "xyz")).isFalse();
    }

    // ==================== startsWith 测试 ====================

    @Test
    @DisplayName("startsWith-以指定前缀开始返回true")
    void should_return_true_when_starts_with() {
        assertThat(StringUtils.startsWith("hello", "hel")).isTrue();
    }

    @Test
    @DisplayName("startsWith-不以指定前缀开始返回false")
    void should_return_false_when_not_starts_with() {
        assertThat(StringUtils.startsWith("hello", "wor")).isFalse();
    }

    // ==================== endsWith 测试 ====================

    @Test
    @DisplayName("endsWith-以指定后缀结束返回true")
    void should_return_true_when_ends_with() {
        assertThat(StringUtils.endsWith("hello", "llo")).isTrue();
    }

    @Test
    @DisplayName("endsWith-不以指定后缀结束返回false")
    void should_return_false_when_not_ends_with() {
        assertThat(StringUtils.endsWith("hello", "wor")).isFalse();
    }

    // ==================== capitalize 测试 ====================

    @Test
    @DisplayName("capitalize-首字母大写")
    void should_capitalize() {
        assertThat(StringUtils.capitalize("hello")).isEqualTo("Hello");
    }

    @Test
    @DisplayName("capitalize-空字符串")
    void should_return_empty_when_capitalize_empty() {
        assertThat(StringUtils.capitalize("")).isEqualTo("");
    }

    @Test
    @DisplayName("capitalize-null")
    void should_return_null_when_capitalize_null() {
        assertThat(StringUtils.capitalize(null)).isNull();
    }

    // ==================== uncapitalize 测试 ====================

    @Test
    @DisplayName("uncapitalize-首字母小写")
    void should_uncapitalize() {
        assertThat(StringUtils.uncapitalize("Hello")).isEqualTo("hello");
    }

    // ==================== camelCase 测试 ====================

    @Test
    @DisplayName("toCamelCase-下划线转驼峰")
    void should_convert_to_camel_case() {
        assertThat(StringUtils.toCamelCase("hello_world_test")).isEqualTo("helloWorldTest");
    }

    @Test
    @DisplayName("toCamelCase-空字符串")
    void should_return_empty_for_camel_case_empty() {
        assertThat(StringUtils.toCamelCase("")).isEqualTo("");
    }

    // ==================== snakeCase 测试 ====================

    @Test
    @DisplayName("toSnakeCase-驼峰转下划线")
    void should_convert_to_snake_case() {
        assertThat(StringUtils.toSnakeCase("helloWorldTest")).isEqualTo("hello_world_test");
    }

    @Test
    @DisplayName("toSnakeCase-空字符串")
    void should_return_empty_for_snake_case_empty() {
        assertThat(StringUtils.toSnakeCase("")).isEqualTo("");
    }
}
