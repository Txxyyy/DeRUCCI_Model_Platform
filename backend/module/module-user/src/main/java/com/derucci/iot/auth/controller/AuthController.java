package com.derucci.iot.auth.controller;

import com.derucci.iot.auth.dto.LoginRequest;
import com.derucci.iot.auth.dto.LoginResponse;
import com.derucci.iot.auth.dto.RefreshRequest;
import com.derucci.iot.auth.dto.UserPermissionRequest;
import com.derucci.iot.auth.dto.UserPermissionResponse;
import com.derucci.iot.auth.service.AuthService;
import com.derucci.iot.common.auth.AuthContext;
import com.derucci.iot.common.auth.RequirePermission;
import com.derucci.iot.common.core.result.Result;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

/**
 * 认证Controller，提供登录、刷新token、获取当前用户信息接口
 */
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    /**
     * 用户登录
     *
     * @param request 登录请求（用户名+密码）
     * @return 登录响应（token+用户信息）
     */
    @PostMapping("/login")
    public Result<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        LoginResponse response = authService.login(request.getUsername(), request.getPassword());
        return Result.success(response);
    }

    /**
     * 刷新token
     *
     * @param request 刷新请求（refreshToken）
     * @return 新的token响应
     */
    @PostMapping("/refresh")
    public Result<LoginResponse> refresh(@Valid @RequestBody RefreshRequest request) {
        LoginResponse response = authService.refresh(request.getRefreshToken());
        return Result.success(response);
    }

    /**
     * 获取当前登录用户信息
     *
     * @return 用户信息
     */
    @GetMapping("/me")
    public Result<LoginResponse.UserInfo> getMe() {
        var user = AuthContext.getUser();
        if (user == null) {
            return Result.unauthorized("未登录");
        }
        LoginResponse.UserInfo info = authService.getMe(user.getId());
        return Result.success(info);
    }

    /**
     * 获取指定用户的权限配置（仅ADMIN可调用）
     *
     * @param userId 目标用户ID
     * @return 用户权限信息（角色+权限列表+分配的产品）
     */
    @GetMapping("/users/{userId}/permissions")
    @RequirePermission(module = "USER", access = "RW")
    public Result<UserPermissionResponse> getUserPermissions(@PathVariable Long userId) {
        UserPermissionResponse response = authService.getUserPermissions(userId);
        return Result.success(response);
    }

    /**
     * 更新指定用户的权限配置（仅ADMIN可调用）
     *
     * @param userId 目标用户ID
     * @param request 权限更新请求（角色+权限列表+产品分配）
     * @return 空结果
     */
    @PutMapping("/users/{userId}/permissions")
    @RequirePermission(module = "USER", access = "RW")
    public Result<Void> updateUserPermissions(@PathVariable Long userId,
                                               @RequestBody UserPermissionRequest request) {
        authService.updateUserPermissions(userId, request);
        return Result.success();
    }
}
