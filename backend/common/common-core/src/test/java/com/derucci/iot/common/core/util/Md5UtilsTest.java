package com.derucci.iot.common.core.util;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Md5Utils加密工具类测试
 * TDD: 先写测试，测试失败后再实现代码
 */
@DisplayName("Md5Utils加密工具类测试")
class Md5UtilsTest {

    @Test
    @DisplayName("encrypt-MD5加密")
    void should_encrypt_to_md5() {
        String result = Md5Utils.encrypt("hello");
        assertThat(result).isEqualTo("5d41402abc4b2a76b9719d911017c592");
    }

    @Test
    @DisplayName("encrypt-不同字符串产生不同结果")
    void should_different_input_different_output() {
        String result1 = Md5Utils.encrypt("hello");
        String result2 = Md5Utils.encrypt("world");
        assertThat(result1).isNotEqualTo(result2);
    }

    @Test
    @DisplayName("encrypt-null输入")
    void should_handle_null_input() {
        String result = Md5Utils.encrypt(null);
        assertThat(result).isNotNull();
    }

    @Test
    @DisplayName("encrypt-空字符串")
    void should_handle_empty_string() {
        String result = Md5Utils.encrypt("");
        assertThat(result).isEqualTo(Md5Utils.encrypt(""));
    }

    @Test
    @DisplayName("encrypt-多次调用结果一致")
    void should_produce_consistent_results() {
        String result1 = Md5Utils.encrypt("test");
        String result2 = Md5Utils.encrypt("test");
        assertThat(result1).isEqualTo(result2);
    }

    @Test
    @DisplayName("encrypt-中文字符")
    void should_encrypt_chinese() {
        String result = Md5Utils.encrypt("测试");
        assertThat(result).isNotNull();
        assertThat(result.length()).isEqualTo(32);
    }

    @Test
    @DisplayName("encrypt-长字符串")
    void should_encrypt_long_string() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 1000; i++) {
            sb.append("a");
        }
        String result = Md5Utils.encrypt(sb.toString());
        assertThat(result).isNotNull();
        assertThat(result.length()).isEqualTo(32);
    }

    @Test
    @DisplayName("encrypt-特殊字符")
    void should_encrypt_special_chars() {
        String result = Md5Utils.encrypt("!@#$%^&*()");
        assertThat(result).isNotNull();
        assertThat(result.length()).isEqualTo(32);
    }

    @Test
    @DisplayName("verify-验证密码")
    void should_verify_password() {
        String password = "123456";
        String md5 = Md5Utils.encrypt(password);
        assertThat(Md5Utils.verify(password, md5)).isTrue();
    }

    @Test
    @DisplayName("verify-错误密码")
    void should_return_false_for_wrong_password() {
        String password = "123456";
        String wrongPassword = "123457";
        String md5 = Md5Utils.encrypt(password);
        assertThat(Md5Utils.verify(wrongPassword, md5)).isFalse();
    }
}
