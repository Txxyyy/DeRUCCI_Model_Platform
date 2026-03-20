package com.derucci.iot.auth.dto;

import java.util.List;

/**
 * 用户权限查询响应DTO
 */
public class UserPermissionResponse {

    /** 用户角色 */
    private String role;
    /** 权限列表 */
    private List<PermissionItem> permissions;
    /** 分配的产品ID列表（仅DEVELOPER） */
    private List<Long> assignedProductIds;

    public static class PermissionItem {
        private String module;
        private String access;
        private String action;

        public PermissionItem() {}
        public PermissionItem(String module, String access, String action) {
            this.module = module;
            this.access = access;
            this.action = action;
        }

        public String getModule() { return module; }
        public void setModule(String module) { this.module = module; }
        public String getAccess() { return access; }
        public void setAccess(String access) { this.access = access; }
        public String getAction() { return action; }
        public void setAction(String action) { this.action = action; }
    }

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }
    public List<PermissionItem> getPermissions() { return permissions; }
    public void setPermissions(List<PermissionItem> permissions) { this.permissions = permissions; }
    public List<Long> getAssignedProductIds() { return assignedProductIds; }
    public void setAssignedProductIds(List<Long> assignedProductIds) { this.assignedProductIds = assignedProductIds; }
}
