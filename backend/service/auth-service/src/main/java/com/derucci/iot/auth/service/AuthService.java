package com.derucci.iot.auth.service;

import com.derucci.iot.auth.dto.LoginResponse;
import com.derucci.iot.auth.dto.UserPermissionRequest;
import com.derucci.iot.auth.dto.UserPermissionResponse;
import com.derucci.iot.auth.entity.AuthUserEntity;
import com.derucci.iot.auth.entity.UserPermission;
import com.derucci.iot.auth.entity.UserProduct;
import com.derucci.iot.auth.repository.AuthUserRepository;
import com.derucci.iot.auth.repository.UserPermissionRepository;
import com.derucci.iot.auth.repository.UserProductRepository;
import com.derucci.iot.common.auth.AuthUser;
import com.derucci.iot.common.auth.JwtUtils;
import com.derucci.iot.common.core.exception.BusinessException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 认证服务，处理登录、token刷新等认证逻辑
 */
@Service
public class AuthService {

    private final AuthUserRepository userRepository;
    private final UserPermissionRepository permissionRepository;
    private final UserProductRepository userProductRepository;
    private final JwtUtils jwtUtils;
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public AuthService(AuthUserRepository userRepository,
                       UserPermissionRepository permissionRepository,
                       UserProductRepository userProductRepository,
                       JwtUtils jwtUtils) {
        this.userRepository = userRepository;
        this.permissionRepository = permissionRepository;
        this.userProductRepository = userProductRepository;
        this.jwtUtils = jwtUtils;
    }

    /**
     * 用户登录，验证凭据并签发token
     *
     * @param username 用户名
     * @param password 明文密码
     * @return 登录响应（含token和用户信息）
     */
    public LoginResponse login(String username, String password) {
        AuthUserEntity user = userRepository.findByUsername(username)
                .orElseThrow(() -> BusinessException.parameterError("用户名或密码错误"));

        if (!"ENABLED".equals(user.getStatus())) {
            throw BusinessException.parameterError("账号已被禁用");
        }

        // 兼容明文密码和BCrypt密码
        boolean matched = false;
        if (user.getPassword().startsWith("$2")) {
            matched = passwordEncoder.matches(password, user.getPassword());
        } else {
            matched = user.getPassword().equals(password);
            // 顺便升级为BCrypt
            if (matched) {
                user.setPassword(passwordEncoder.encode(password));
            }
        }
        if (!matched) {
            throw BusinessException.parameterError("用户名或密码错误");
        }
        // 更新最后登录时间
        user.setLastLoginTime(LocalDateTime.now());
        userRepository.save(user);

        // 构建权限列表
        List<String> permissions = buildPermissions(user);
        List<Long> productIds = buildAssignedProductIds(user);

        AuthUser authUser = new AuthUser(user.getId(), user.getUsername(),
                user.getRole() != null ? user.getRole() : "DEVELOPER", permissions);
        authUser.setAssignedProductIds(productIds);

        String accessToken = jwtUtils.generateAccessToken(authUser);
        String refreshToken = jwtUtils.generateRefreshToken(user.getId());

        return buildLoginResponse(accessToken, refreshToken, user, permissions, productIds);
    }

    /**
     * 刷新token
     *
     * @param refreshToken 刷新token
     * @return 新的登录响应
     */
    public LoginResponse refresh(String refreshToken) {
        Long userId = jwtUtils.parseRefreshTokenUserId(refreshToken);
        AuthUserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> BusinessException.parameterError("用户不存在"));

        List<String> permissions = buildPermissions(user);
        List<Long> productIds = buildAssignedProductIds(user);
        AuthUser authUser = new AuthUser(user.getId(), user.getUsername(),
                user.getRole() != null ? user.getRole() : "DEVELOPER", permissions);
        authUser.setAssignedProductIds(productIds);

        String newAccessToken = jwtUtils.generateAccessToken(authUser);
        String newRefreshToken = jwtUtils.generateRefreshToken(user.getId());

        return buildLoginResponse(newAccessToken, newRefreshToken, user, permissions, productIds);
    }

    /**
     * 获取当前用户信息
     *
     * @param userId 用户ID
     * @return 用户信息响应
     */
    public LoginResponse.UserInfo getMe(Long userId) {
        AuthUserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> BusinessException.notFound("用户不存在"));
        List<String> permissions = buildPermissions(user);
        List<Long> productIds = buildAssignedProductIds(user);

        LoginResponse.UserInfo info = new LoginResponse.UserInfo();
        info.setId(user.getId());
        info.setUsername(user.getUsername());
        info.setNickname(user.getNickname());
        info.setRole(user.getRole() != null ? user.getRole() : "DEVELOPER");
        info.setPermissions(permissions);
        info.setAvatar(user.getAvatar());
        info.setAssignedProductIds(productIds);
        return info;
    }

    /**
     * 获取指定用户的权限配置
     *
     * @param userId 用户ID
     * @return 权限响应（角色+权限列表+分配的产品）
     */
    public UserPermissionResponse getUserPermissions(Long userId) {
        AuthUserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> BusinessException.notFound("用户不存在"));

        UserPermissionResponse response = new UserPermissionResponse();
        response.setRole(user.getRole() != null ? user.getRole() : "DEVELOPER");

        List<UserPermission> perms = permissionRepository.findByUserId(userId);
        response.setPermissions(perms.stream()
                .map(p -> new UserPermissionResponse.PermissionItem(p.getModule(), p.getAccess(), p.getAction()))
                .collect(Collectors.toList()));

        response.setAssignedProductIds(userProductRepository.findProductIdsByUserId(userId));
        return response;
    }

    /**
     * 更新指定用户的权限配置（先删后插）
     *
     * @param userId 用户ID
     * @param request 权限更新请求
     */
    @Transactional
    public void updateUserPermissions(Long userId, UserPermissionRequest request) {
        AuthUserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> BusinessException.notFound("用户不存在"));

        // 更新角色
        if (request.getRole() != null) {
            user.setRole(request.getRole());
            userRepository.save(user);
        }

        // 先删除旧权限，再批量插入新权限
        permissionRepository.deleteByUserId(userId);
        if (request.getPermissions() != null) {
            for (UserPermissionResponse.PermissionItem item : request.getPermissions()) {
                UserPermission perm = new UserPermission();
                perm.setUserId(userId);
                perm.setModule(item.getModule());
                perm.setAccess(item.getAccess());
                perm.setAction(item.getAction());
                permissionRepository.save(perm);
            }
        }

        // 先删除旧产品分配，再批量插入
        userProductRepository.deleteByUserId(userId);
        if (request.getAssignedProductIds() != null) {
            for (Long productId : request.getAssignedProductIds()) {
                UserProduct up = new UserProduct();
                up.setUserId(userId);
                up.setProductId(productId);
                userProductRepository.save(up);
            }
        }
    }

    /**
     * 按角色生成默认权限矩阵，再合并t_user_permission自定义权限
     *
     * @param user 用户实体
     * @return 权限字符串列表
     */
    private List<String> buildPermissions(AuthUserEntity user) {
        String role = user.getRole() != null ? user.getRole() : "DEVELOPER";
        List<String> permissions = new ArrayList<>();

        switch (role) {
            case "ADMIN":
                permissions.addAll(List.of(
                    "PRODUCT:RW", "PRODUCT:PUBLISH",
                    "CATEGORY_MODEL:RW",
                    "PRODUCT_MODEL:RW",
                    "DEVICE:RW", "DEVICE:DELETE", "DEVICE:OFFLINE",
                    "OTA:RW", "OTA:PUSH",
                    "USER:RW"
                ));
                break;
            case "DEVELOPER":
                permissions.addAll(List.of(
                    "PRODUCT:RW",
                    "CATEGORY_MODEL:R",
                    "PRODUCT_MODEL:RW",
                    "DEVICE:RW",
                    "OTA:RW", "OTA:PUSH"
                ));
                break;
            case "FAE":
                permissions.addAll(List.of(
                    "PRODUCT:R",
                    "CATEGORY_MODEL:R",
                    "PRODUCT_MODEL:R",
                    "DEVICE:R", "DEVICE:OFFLINE",
                    "OTA:R", "OTA:PUSH"
                ));
                break;
            default:
                break;
        }

        // 合并自定义权限
        List<UserPermission> customPerms = permissionRepository.findByUserId(user.getId());
        for (UserPermission p : customPerms) {
            String perm;
            if (p.getAction() != null && !p.getAction().isEmpty()) {
                perm = p.getModule() + ":" + p.getAction();
            } else {
                perm = p.getModule() + ":" + p.getAccess();
            }
            if (!permissions.contains(perm)) {
                permissions.add(perm);
            }
        }

        return permissions;
    }

    /**
     * 查询DEVELOPER角色分配的产品ID列表
     *
     * @param user 用户实体
     * @return 产品ID列表（非DEVELOPER返回空列表）
     */
    private List<Long> buildAssignedProductIds(AuthUserEntity user) {
        String role = user.getRole() != null ? user.getRole() : "DEVELOPER";
        if (!"DEVELOPER".equals(role)) {
            return List.of();
        }
        return userProductRepository.findProductIdsByUserId(user.getId());
    }

    private LoginResponse buildLoginResponse(String accessToken, String refreshToken,
                                              AuthUserEntity user, List<String> permissions,
                                              List<Long> productIds) {
        LoginResponse resp = new LoginResponse();
        resp.setAccessToken(accessToken);
        resp.setRefreshToken(refreshToken);
        resp.setExpiresIn(jwtUtils.getAccessExpiration() / 1000);

        LoginResponse.UserInfo info = new LoginResponse.UserInfo();
        info.setId(user.getId());
        info.setUsername(user.getUsername());
        info.setNickname(user.getNickname());
        info.setRole(user.getRole() != null ? user.getRole() : "DEVELOPER");
        info.setPermissions(permissions);
        info.setAvatar(user.getAvatar());
        info.setAssignedProductIds(productIds);
        resp.setUser(info);
        return resp;
    }
}
