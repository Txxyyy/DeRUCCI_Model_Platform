package com.derucci.iot.user.controller;

import com.derucci.iot.common.core.result.Result;
import com.derucci.iot.user.entity.User;
import com.derucci.iot.user.service.UserService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 用户Controller
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
     */
    @GetMapping("/{id}")
    public Result<User> getUser(@PathVariable Long id) {
        return userService.findById(id)
                .map(Result::success)
                .orElse(Result.notFound("用户不存在"));
    }

    /**
     * 根据用户名查询用户
     */
    @GetMapping("/username/{username}")
    public Result<User> getUserByUsername(@PathVariable String username) {
        return userService.findByUsername(username)
                .map(Result::success)
                .orElse(Result.notFound("用户不存在"));
    }

    /**
     * 查询所有用户
     */
    @GetMapping
    public Result<List<User>> getAllUsers() {
        return Result.success(userService.findAll());
    }

    /**
     * 创建用户
     */
    @PostMapping
    public Result<User> createUser(@RequestBody User user) {
        User created = userService.createUser(user);
        return Result.success(created);
    }

    /**
     * 更新用户
     */
    @PutMapping("/{id}")
    public Result<User> updateUser(@PathVariable Long id, @RequestBody User user) {
        User updated = userService.updateUser(id, user);
        return Result.success(updated);
    }

    /**
     * 删除用户
     */
    @DeleteMapping("/{id}")
    public Result<Void> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return Result.success();
    }

    /**
     * 启用用户
     */
    @PutMapping("/{id}/enable")
    public Result<User> enableUser(@PathVariable Long id) {
        User user = userService.enableUser(id);
        return Result.success(user);
    }

    /**
     * 禁用用户
     */
    @PutMapping("/{id}/disable")
    public Result<User> disableUser(@PathVariable Long id) {
        User user = userService.disableUser(id);
        return Result.success(user);
    }
}
