package com.derucci.iot.common.core.util;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * ValidationUtils校验工具类测试
 * TDD: 先写测试，测试失败后再实现代码
 */
@DisplayName("ValidationUtils校验工具类测试")
class ValidationUtilsTest {

    // ==================== isEmail 测试 ====================

    @Test
    @DisplayName("isEmail-正确邮箱格式")
    void should_return_true_for_valid_email() {
        assertThat(ValidationUtils.isEmail("test@example.com")).isTrue();
        assertThat(ValidationUtils.isEmail("user.name@domain.co.uk")).isTrue();
    }

    @Test
    @DisplayName("isEmail-错误邮箱格式")
    void should_return_false_for_invalid_email() {
        assertThat(ValidationUtils.isEmail("notanemail")).isFalse();
        assertThat(ValidationUtils.isEmail("@example.com")).isFalse();
        assertThat(ValidationUtils.isEmail("test@")).isFalse();
        assertThat(ValidationUtils.isEmail("test@.com")).isFalse();
        assertThat(ValidationUtils.isEmail("")).isFalse();
        assertThat(ValidationUtils.isEmail(null)).isFalse();
    }

    // ==================== isPhone 测试 ====================

    @Test
    @DisplayName("isPhone-中国手机号")
    void should_return_true_for_valid_china_phone() {
        assertThat(ValidationUtils.isPhone("13812345678")).isTrue();
        assertThat(ValidationUtils.isPhone("15912345678")).isTrue();
        assertThat(ValidationUtils.isPhone("18612345678")).isTrue();
    }

    @Test
    @DisplayName("isPhone-错误手机号格式")
    void should_return_false_for_invalid_phone() {
        assertThat(ValidationUtils.isPhone("12345678901")).isFalse();
        assertThat(ValidationUtils.isPhone("1381234567")).isFalse();
        assertThat(ValidationUtils.isPhone("")).isFalse();
        assertThat(ValidationUtils.isPhone(null)).isFalse();
    }

    // ==================== isUrl 测试 ====================

    @Test
    @DisplayName("isUrl-正确URL格式")
    void should_return_true_for_valid_url() {
        assertThat(ValidationUtils.isUrl("http://example.com")).isTrue();
        assertThat(ValidationUtils.isUrl("https://example.com")).isTrue();
        assertThat(ValidationUtils.isUrl("https://example.com/path")).isTrue();
        assertThat(ValidationUtils.isUrl("http://example.com:8080")).isTrue();
    }

    @Test
    @DisplayName("isUrl-错误URL格式")
    void should_return_false_for_invalid_url() {
        assertThat(ValidationUtils.isUrl("notaurl")).isFalse();
        assertThat(ValidationUtils.isUrl("ftp://example.com")).isFalse();
        assertThat(ValidationUtils.isUrl("")).isFalse();
        assertThat(ValidationUtils.isUrl(null)).isFalse();
    }

    // ==================== isNumeric 测试 ====================

    @Test
    @DisplayName("isNumeric-纯数字")
    void should_return_true_for_numeric() {
        assertThat(ValidationUtils.isNumeric("12345")).isTrue();
        assertThat(ValidationUtils.isNumeric("0")).isTrue();
    }

    @Test
    @DisplayName("isNumeric-非纯数字")
    void should_return_false_for_non_numeric() {
        assertThat(ValidationUtils.isNumeric("123a")).isFalse();
        assertThat(ValidationUtils.isNumeric("12.34")).isFalse();
        assertThat(ValidationUtils.isNumeric("")).isFalse();
        assertThat(ValidationUtils.isNumeric(null)).isFalse();
    }

    // ==================== isAlpha 测试 ====================

    @Test
    @DisplayName("isAlpha-纯字母")
    void should_return_true_for_alpha() {
        assertThat(ValidationUtils.isAlpha("abc")).isTrue();
        assertThat(ValidationUtils.isAlpha("ABC")).isTrue();
        assertThat(ValidationUtils.isAlpha("abcDEF")).isTrue();
    }

    @Test
    @DisplayName("isAlpha-非纯字母")
    void should_return_false_for_non_alpha() {
        assertThat(ValidationUtils.isAlpha("abc123")).isFalse();
        assertThat(ValidationUtils.isAlpha("abc def")).isFalse();
        assertThat(ValidationUtils.isAlpha("")).isFalse();
        assertThat(ValidationUtils.isAlpha(null)).isFalse();
    }

    // ==================== isAlphanumeric 测试 ====================

    @Test
    @DisplayName("isAlphanumeric-字母数字组合")
    void should_return_true_for_alphanumeric() {
        assertThat(ValidationUtils.isAlphanumeric("abc123")).isTrue();
        assertThat(ValidationUtils.isAlphanumeric("ABC123")).isTrue();
    }

    @Test
    @DisplayName("isAlphanumeric-包含特殊字符")
    void should_return_false_for_non_alphanumeric() {
        assertThat(ValidationUtils.isAlphanumeric("abc_123")).isFalse();
        assertThat(ValidationUtils.isAlphanumeric("abc def")).isFalse();
    }

    // ==================== isPositiveNumber 测试 ====================

    @Test
    @DisplayName("isPositiveNumber-正数")
    void should_return_true_for_positive_number() {
        assertThat(ValidationUtils.isPositiveNumber("123")).isTrue();
        assertThat(ValidationUtils.isPositiveNumber("0.01")).isTrue();
    }

    @Test
    @DisplayName("isPositiveNumber-非正数")
    void should_return_false_for_non_positive_number() {
        assertThat(ValidationUtils.isPositiveNumber("-1")).isFalse();
        assertThat(ValidationUtils.isPositiveNumber("0")).isFalse();
        assertThat(ValidationUtils.isPositiveNumber("abc")).isFalse();
    }

    // ==================== isInteger 测试 ====================

    @Test
    @DisplayName("isInteger-整数")
    void should_return_true_for_integer() {
        assertThat(ValidationUtils.isInteger("123")).isTrue();
        assertThat(ValidationUtils.isInteger("-456")).isTrue();
    }

    @Test
    @DisplayName("isInteger-非整数")
    void should_return_false_for_non_integer() {
        assertThat(ValidationUtils.isInteger("12.34")).isFalse();
        assertThat(ValidationUtils.isInteger("abc")).isFalse();
    }

    // ==================== isIdCard 测试 ====================

    @Test
    @DisplayName("isIdCard-中国身份证号")
    void should_return_true_for_valid_idcard() {
        assertThat(ValidationUtils.isIdCard("110101199001011234")).isTrue();
    }

    @Test
    @DisplayName("isIdCard-错误身份证号")
    void should_return_false_for_invalid_idcard() {
        assertThat(ValidationUtils.isIdCard("123")).isFalse();
        assertThat(ValidationUtils.isIdCard("")).isFalse();
        assertThat(ValidationUtils.isIdCard(null)).isFalse();
    }

    // ==================== length 测试 ====================

    @Test
    @DisplayName("isLength-正确长度")
    void should_return_true_for_valid_length() {
        assertThat(ValidationUtils.isLength("abc", 1, 5)).isTrue();
        assertThat(ValidationUtils.isLength("ab", 2, 2)).isTrue();
    }

    @Test
    @DisplayName("isLength-错误长度")
    void should_return_false_for_invalid_length() {
        assertThat(ValidationUtils.isLength("abcde", 1, 3)).isFalse();
        assertThat(ValidationUtils.isLength("a", 2, 5)).isFalse();
    }

    // ==================== matches 测试 ====================

    @Test
    @DisplayName("matches-正则匹配")
    void should_return_true_for_matching_pattern() {
        assertThat(ValidationUtils.matches("abc123", "^[a-z0-9]+$")).isTrue();
    }

    @Test
    @DisplayName("matches-正则不匹配")
    void should_return_false_for_non_matching_pattern() {
        assertThat(ValidationUtils.matches("abc!@#", "^[a-z0-9]+$")).isFalse();
    }
}
