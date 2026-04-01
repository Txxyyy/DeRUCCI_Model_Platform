package com.derucci.iot.auth.service;

import com.derucci.iot.auth.dto.LoginResponse;
import com.derucci.iot.auth.dto.UserPermissionRequest;
import com.derucci.iot.auth.dto.UserPermissionResponse;
import com.derucci.iot.auth.entity.UserPermission;
import com.derucci.iot.auth.entity.UserProduct;
import com.derucci.iot.auth.repository.UserPermissionRepository;
import com.derucci.iot.auth.repository.UserProductRepository;
import com.derucci.iot.common.auth.AuthUser;
import com.derucci.iot.common.auth.JwtUtils;
import com.derucci.iot.common.core.exception.BusinessException;
import com.derucci.iot.user.entity.User;
import com.derucci.iot.user.repository.UserRepository;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final UserPermissionRepository permissionRepository;
    private final UserProductRepository userProductRepository;
    private final JwtUtils jwtUtils;
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public AuthService(UserRepository userRepository,
                       UserPermissionRepository permissionRepository,
                       UserProductRepository userProductRepository,
                       JwtUtils jwtUtils) {
        this.userRepository = userRepository;
        this.permissionRepository = permissionRepository;
        this.userProductRepository = userProductRepository;
        this.jwtUtils = jwtUtils;
    }

    public LoginResponse login(String username, String password) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> BusinessException.parameterError("用户名或密码错误"));

        if (!user.isEnabled()) {
            throw BusinessException.parameterError("账号已被禁用");
        }

        boolean matched = false;
        if (user.getPassword().startsWith("$2")) {
            matched = passwordEncoder.matches(password, user.getPassword());
        } else {
            matched = user.getPassword().equals(password);
            if (matched) {
                user.setPassword(passwordEncoder.encode(password));
            }
        }
        if (!matched) {
            throw BusinessException.parameterError("用户名或密码错误");
        }
        user.setLastLoginTime(LocalDateTime.now());
        userRepository.save(user);

        List<String> permissions = buildPermissions(user);
        List<Long> productIds = buildAssignedProductIds(user);

        AuthUser authUser = new AuthUser(user.getId(), user.getUsername(),
                user.getRole() != null ? user.getRole() : "DEVELOPER", permissions);
        authUser.setAssignedProductIds(productIds);

        String accessToken = jwtUtils.generateAccessToken(authUser);
        String refreshToken = jwtUtils.generateRefreshToken(user.getId());

        return buildLoginResponse(accessToken, refreshToken, user, permissions, productIds);
    }

    public LoginResponse refresh(String refreshToken) {
        Long userId = jwtUtils.parseRefreshTokenUserId(refreshToken);
        User user = userRepository.findById(userId)
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

    public LoginResponse.UserInfo getMe(Long userId) {
        User user = userRepository.findById(userId)
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

    public UserPermissionResponse getUserPermissions(Long userId) {
        User user = userRepository.findById(userId)
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

    @Transactional
    public void updateUserPermissions(Long userId, UserPermissionRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> BusinessException.notFound("用户不存在"));

        if (request.getRole() != null) {
            user.setRole(request.getRole());
            userRepository.save(user);
        }

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

    private List<String> buildPermissions(User user) {
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

    private List<Long> buildAssignedProductIds(User user) {
        String role = user.getRole() != null ? user.getRole() : "DEVELOPER";
        if (!"DEVELOPER".equals(role)) {
            return List.of();
        }
        return userProductRepository.findProductIdsByUserId(user.getId());
    }

    private LoginResponse buildLoginResponse(String accessToken, String refreshToken,
                                              User user, List<String> permissions,
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
