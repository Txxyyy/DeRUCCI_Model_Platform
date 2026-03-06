package com.derucci.iot.user.service;

import com.derucci.iot.user.entity.User;
import com.derucci.iot.user.entity.UserStatus;
import com.derucci.iot.user.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * UserService测试 - TDD
 */
@DisplayName("UserService测试")
@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;

    @Test
    @DisplayName("根据ID查询用户")
    void should_find_user_by_id() {
        // Given
        User user = new User();
        user.setId(1L);
        user.setUsername("admin");
        user.setEmail("admin@derucci.com");
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        // When
        Optional<User> result = userService.findById(1L);

        // Then
        assertThat(result).isPresent();
        assertThat(result.get().getUsername()).isEqualTo("admin");
    }

    @Test
    @DisplayName("根据用户名查询用户")
    void should_find_user_by_username() {
        // Given
        User user = new User();
        user.setId(1L);
        user.setUsername("admin");
        when(userRepository.findByUsername("admin")).thenReturn(Optional.of(user));

        // When
        Optional<User> result = userService.findByUsername("admin");

        // Then
        assertThat(result).isPresent();
        assertThat(result.get().getId()).isEqualTo(1L);
    }

    @Test
    @DisplayName("创建用户")
    void should_create_user() {
        // Given
        User user = new User();
        user.setUsername("testuser");
        user.setPassword("password123");
        user.setEmail("test@derucci.com");

        User savedUser = new User();
        savedUser.setId(1L);
        savedUser.setUsername("testuser");
        savedUser.setPassword("password123");
        savedUser.setEmail("test@derucci.com");
        savedUser.setStatus(UserStatus.ENABLED);
        savedUser.setCreateTime(LocalDateTime.now());

        when(userRepository.existsByUsername("testuser")).thenReturn(false);
        when(userRepository.save(any(User.class))).thenReturn(savedUser);

        // When
        User result = userService.createUser(user);

        // Then
        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getUsername()).isEqualTo("testuser");
        verify(userRepository).save(any(User.class));
    }

    @Test
    @DisplayName("创建用户-用户名已存在")
    void should_throw_exception_when_username_exists() {
        // Given
        User user = new User();
        user.setUsername("existinguser");
        when(userRepository.existsByUsername("existinguser")).thenReturn(true);

        // When & Then
        try {
            userService.createUser(user);
        } catch (Exception e) {
            assertThat(e.getMessage()).contains("用户名已存在");
        }
    }

    @Test
    @DisplayName("更新用户")
    void should_update_user() {
        // Given
        User existingUser = new User();
        existingUser.setId(1L);
        existingUser.setUsername("admin");
        existingUser.setNickname("管理员");

        User updateData = new User();
        updateData.setNickname("超级管理员");

        when(userRepository.findById(1L)).thenReturn(Optional.of(existingUser));
        when(userRepository.save(any(User.class))).thenReturn(existingUser);

        // When
        User result = userService.updateUser(1L, updateData);

        // Then
        assertThat(result.getNickname()).isEqualTo("超级管理员");
    }

    @Test
    @DisplayName("删除用户")
    void should_delete_user() {
        // Given
        when(userRepository.existsById(1L)).thenReturn(true);
        doNothing().when(userRepository).deleteById(1L);

        // When
        userService.deleteUser(1L);

        // Then
        verify(userRepository).deleteById(1L);
    }

    @Test
    @DisplayName("启用用户")
    void should_enable_user() {
        // Given
        User user = new User();
        user.setId(1L);
        user.setStatus(UserStatus.DISABLED);
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userRepository.save(any(User.class))).thenReturn(user);

        // When
        User result = userService.enableUser(1L);

        // Then
        assertThat(result.getStatus()).isEqualTo(UserStatus.ENABLED);
    }

    @Test
    @DisplayName("禁用用户")
    void should_disable_user() {
        // Given
        User user = new User();
        user.setId(1L);
        user.setStatus(UserStatus.ENABLED);
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userRepository.save(any(User.class))).thenReturn(user);

        // When
        User result = userService.disableUser(1L);

        // Then
        assertThat(result.getStatus()).isEqualTo(UserStatus.DISABLED);
    }
}
