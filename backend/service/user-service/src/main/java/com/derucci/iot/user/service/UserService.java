package com.derucci.iot.user.service;

import com.derucci.iot.common.core.exception.BusinessException;
import com.derucci.iot.user.entity.User;
import com.derucci.iot.user.entity.UserStatus;
import com.derucci.iot.user.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * 用户Service
 */
@Service
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /**
     * 根据ID查询用户
     */
    public Optional<User> findById(Long id) {
        return userRepository.findById(id);
    }

    /**
     * 根据用户名查询用户
     */
    public Optional<User> findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    /**
     * 根据邮箱查询用户
     */
    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    /**
     * 查询所有用户
     */
    public List<User> findAll() {
        return userRepository.findAll();
    }

    /**
     * 创建用户
     */
    @Transactional
    public User createUser(User user) {
        // 检查用户名是否存在
        if (userRepository.existsByUsername(user.getUsername())) {
            throw BusinessException.parameterError("用户名已存在");
        }
        // 检查邮箱是否存在
        if (user.getEmail() != null && userRepository.existsByEmail(user.getEmail())) {
            throw BusinessException.parameterError("邮箱已被使用");
        }
        // 设置默认状态
        if (user.getStatus() == null) {
            user.setStatus(UserStatus.ENABLED);
        }
        return userRepository.save(user);
    }

    /**
     * 更新用户
     */
    @Transactional
    public User updateUser(Long id, User updateData) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> BusinessException.notFound("用户不存在"));

        // 更新昵称
        if (updateData.getNickname() != null) {
            user.setNickname(updateData.getNickname());
        }
        // 更新邮箱
        if (updateData.getEmail() != null) {
            user.setEmail(updateData.getEmail());
        }
        // 更新手机号
        if (updateData.getPhone() != null) {
            user.setPhone(updateData.getPhone());
        }
        // 更新头像
        if (updateData.getAvatar() != null) {
            user.setAvatar(updateData.getAvatar());
        }

        return userRepository.save(user);
    }

    /**
     * 删除用户
     */
    @Transactional
    public void deleteUser(Long id) {
        if (!userRepository.existsById(id)) {
            throw BusinessException.notFound("用户不存在");
        }
        userRepository.deleteById(id);
    }

    /**
     * 启用用户
     */
    @Transactional
    public User enableUser(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> BusinessException.notFound("用户不存在"));
        user.setStatus(UserStatus.ENABLED);
        return userRepository.save(user);
    }

    /**
     * 禁用用户
     */
    @Transactional
    public User disableUser(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> BusinessException.notFound("用户不存在"));
        user.setStatus(UserStatus.DISABLED);
        return userRepository.save(user);
    }

    /**
     * 修改密码
     */
    @Transactional
    public void changePassword(Long id, String oldPassword, String newPassword) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> BusinessException.notFound("用户不存在"));

        // 验证旧密码
        if (!user.getPassword().equals(oldPassword)) {
            throw BusinessException.parameterError("原密码错误");
        }

        user.setPassword(newPassword);
        userRepository.save(user);
    }
}
