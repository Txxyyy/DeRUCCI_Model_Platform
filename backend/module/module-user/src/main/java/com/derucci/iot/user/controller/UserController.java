package com.derucci.iot.user.controller;

import com.derucci.iot.common.core.result.Result;
import com.derucci.iot.common.auth.RequirePermission;
import com.derucci.iot.user.entity.User;
import com.derucci.iot.user.service.UserService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 用户Controller — 仅ADMIN可访问
 */
@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    /**
     * 根据ID查询用户
     *
     * @param id 用户主键ID
     * @return 用户详情，不存在时返回404
     */
    @GetMapping("/{id}")
    @RequirePermission(module = "USER", access = "R")
    public Result<User> getUser(@PathVariable Long id) {
        return userService.findById(id)
                .map(Result::success)
                .orElse(Result.notFound("用户不存在"));
    }

    /**
     * 根据用户名查询用户
     *
     * @param username 用户名
     * @return 用户详情，不存在时返回404
     */
    @GetMapping("/username/{username}")
    @RequirePermission(module = "USER", access = "R")
    public Result<User> getUserByUsername(@PathVariable String username) {
        return userService.findByUsername(username)
                .map(Result::success)
                .orElse(Result.notFound("用户不存在"));
    }

    /**
     * 查询所有用户
     *
     * @return 用户列表
     */
    @GetMapping
    @RequirePermission(module = "USER", access = "RW")
    public Result<List<User>> getAllUsers() {
        return Result.success(userService.findAll());
    }

    /**
     * 创建用户
     *
     * @param user 用户信息
     * @return 创建成功的用户
     */
    @PostMapping
    @RequirePermission(module = "USER", access = "RW")
    public Result<User> createUser(@RequestBody User user) {
        User created = userService.createUser(user);
        return Result.success(created);
    }

    /**
     * 更新用户
     *
     * @param id 用户ID
     * @param user 更新的用户信息
     * @return 更新后的用户
     */
    @PutMapping("/{id}")
    @RequirePermission(module = "USER", access = "RW")
    public Result<User> updateUser(@PathVariable Long id, @RequestBody User user) {
        User updated = userService.updateUser(id, user);
        return Result.success(updated);
    }

    /**
     * 删除用户
     *
     * @param id 用户ID
     */
    @DeleteMapping("/{id}")
    @RequirePermission(module = "USER", access = "RW")
    public Result<Void> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return Result.success();
    }

    /**
     * 启用用户
     *
     * @param id 用户ID
     * @return 启用后的用户
     */
    @PutMapping("/{id}/enable")
    @RequirePermission(module = "USER", access = "RW")
    public Result<User> enableUser(@PathVariable Long id) {
        User user = userService.enableUser(id);
        return Result.success(user);
    }

    /**
     * 禁用用户
     *
     * @param id 用户ID
     * @return 禁用后的用户
     */
    @PutMapping("/{id}/disable")
    @RequirePermission(module = "USER", access = "RW")
    public Result<User> disableUser(@PathVariable Long id) {
        User user = userService.disableUser(id);
        return Result.success(user);
    }
}