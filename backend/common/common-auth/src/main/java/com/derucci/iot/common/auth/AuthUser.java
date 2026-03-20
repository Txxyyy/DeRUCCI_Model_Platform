package com.derucci.iot.common.auth;

import java.util.List;

/**
 * 认证用户信息，从JWT token解析后存入ThreadLocal
 */
public class AuthUser {

    /** 用户ID */
    private Long id;
    /** 用户名 */
    private String username;
    /** 角色（ADMIN/DEVELOPER/FAE） */
    private String role;
    /** 权限列表（如 PRODUCT:RW, DEVICE:R, OTA:PUSH） */
    private List<String> permissions;
    /** 分配的产品ID列表（仅DEVELOPER角色使用） */
    private List<Long> assignedProductIds;

    public AuthUser() {}

    public AuthUser(Long id, String username, String role, List<String> permissions) {
        this.id = id;
        this.username = username;
        this.role = role;
        this.permissions = permissions;
    }

    /**
     * 判断是否为管理员
     *
     * @return true表示管理员
     */
    public boolean isAdmin() {
        return "ADMIN".equals(role);
    }

    /**
     * 判断是否为开发者角色
     *
     * @return true表示DEVELOPER
     */
    public boolean isDeveloper() {
        return "DEVELOPER".equals(role);
    }

    /**
     * 判断是否为FAE角色
     *
     * @return true表示FAE
     */
    public boolean isFAE() {
        return "FAE".equals(role);
    }

    /**
     * 检查是否拥有指定权限
     *
     * @param permission 权限码，如 "PRODUCT:RW"
     * @return true表示拥有该权限
     */
    public boolean hasPermission(String permission) {
        if (isAdmin()) return true;
        if (permissions == null) return false;
        // RW权限包含R
        if (permission.endsWith(":R")) {
            String module = permission.split(":")[0];
            return permissions.contains(module + ":R") || permissions.contains(module + ":RW");
        }
        return permissions.contains(permission);
    }

    /**
     * DEVELOPER是否有该产品的访问权限
     *
     * @param productId 产品ID
     * @return true表示有权限（ADMIN/FAE不限制）
     */
    public boolean hasProductAccess(Long productId) {
        if (!"DEVELOPER".equals(role)) return true;
        return assignedProductIds != null && assignedProductIds.contains(productId);
    }

    /**
     * 是否拥有特定操作权限（如 OTA:PUSH）
     *
     * @param module 模块名
     * @param action 操作名
     * @return true表示拥有该操作权限
     */
    public boolean hasActionPermission(String module, String action) {
        if (isAdmin()) return true;
        if (permissions == null) return false;
        return permissions.contains(module + ":" + action);
    }

    // Getters & Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }
    public List<String> getPermissions() { return permissions; }
    public void setPermissions(List<String> permissions) { this.permissions = permissions; }
    public List<Long> getAssignedProductIds() { return assignedProductIds; }
    public void setAssignedProductIds(List<Long> assignedProductIds) { this.assignedProductIds = assignedProductIds; }
}
