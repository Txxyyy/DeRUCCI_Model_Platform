package com.derucci.iot.auth.dto;

import java.util.List;

/**
 * 用户权限更新请求DTO
 */
public class UserPermissionRequest {

    /** 角色：ADMIN / DEVELOPER / FAE */
    private String role;
    /** 自定义权限列表 */
    private List<UserPermissionResponse.PermissionItem> permissions;
    /** 分配的产品ID列表（仅DEVELOPER） */
    private List<Long> assignedProductIds;

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }
    public List<UserPermissionResponse.PermissionItem> getPermissions() { return permissions; }
    public void setPermissions(List<UserPermissionResponse.PermissionItem> permissions) { this.permissions = permissions; }
    public List<Long> getAssignedProductIds() { return assignedProductIds; }
    public void setAssignedProductIds(List<Long> assignedProductIds) { this.assignedProductIds = assignedProductIds; }
}
