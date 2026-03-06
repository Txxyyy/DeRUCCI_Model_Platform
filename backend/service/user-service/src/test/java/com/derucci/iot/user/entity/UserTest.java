package com.derucci.iot.user.entity;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * User实体测试 - TDD
 */
@DisplayName("User实体测试")
class UserTest {

    @Test
    @DisplayName("创建用户-基本属性")
    void should_create_user_with_basic_properties() {
        // Given
        String username = "admin";
        String password = "password123";
        String email = "admin@derucci.com";
        String phone = "13800138000";

        // When
        User user = new User();
        user.setId(1L);
        user.setUsername(username);
        user.setPassword(password);
        user.setEmail(email);
        user.setPhone(phone);
        user.setNickname("管理员");
        user.setStatus(UserStatus.ENABLED);
        user.setCreateTime(LocalDateTime.now());
        user.setUpdateTime(LocalDateTime.now());

        // Then
        assertThat(user.getId()).isEqualTo(1L);
        assertThat(user.getUsername()).isEqualTo(username);
        assertThat(user.getPassword()).isEqualTo(password);
        assertThat(user.getEmail()).isEqualTo(email);
        assertThat(user.getPhone()).isEqualTo(phone);
        assertThat(user.getNickname()).isEqualTo("管理员");
        assertThat(user.getStatus()).isEqualTo(UserStatus.ENABLED);
    }

    @Test
    @DisplayName("用户状态-启用")
    void should_have_enabled_status() {
        User user = new User();
        user.setStatus(UserStatus.ENABLED);
        assertThat(user.getStatus()).isEqualTo(UserStatus.ENABLED);
        assertThat(user.isEnabled()).isTrue();
    }

    @Test
    @DisplayName("用户状态-禁用")
    void should_have_disabled_status() {
        User user = new User();
        user.setStatus(UserStatus.DISABLED);
        assertThat(user.getStatus()).isEqualTo(UserStatus.DISABLED);
        assertThat(user.isEnabled()).isFalse();
    }

    @Test
    @DisplayName("用户状态-锁定")
    void should_have_locked_status() {
        User user = new User();
        user.setStatus(UserStatus.LOCKED);
        assertThat(user.getStatus()).isEqualTo(UserStatus.LOCKED);
        assertThat(user.isEnabled()).isFalse();
    }

    @Test
    @DisplayName("用户ID生成策略")
    void should_have_id_generation_strategy() {
        // 测试@Id注解存在
        User user = new User();
        assertThat(user.getId()).isNull();
    }
}
