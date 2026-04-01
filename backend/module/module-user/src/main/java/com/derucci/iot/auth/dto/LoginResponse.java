package com.derucci.iot.auth.dto;

/**
 * 登录响应DTO
 */
public class LoginResponse {
    private String accessToken;
    private String refreshToken;
    private long expiresIn;
    private UserInfo user;

    public static class UserInfo {
        private Long id;
        private String username;
        private String nickname;
        private String role;
        private java.util.List<String> permissions;
        private String avatar;
        /** 分配的产品ID列表（仅DEVELOPER角色） */
        private java.util.List<Long> assignedProductIds;

        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }
        public String getUsername() { return username; }
        public void setUsername(String username) { this.username = username; }
        public String getNickname() { return nickname; }
        public void setNickname(String nickname) { this.nickname = nickname; }
        public String getRole() { return role; }
        public void setRole(String role) { this.role = role; }
        public java.util.List<String> getPermissions() { return permissions; }
        public void setPermissions(java.util.List<String> permissions) { this.permissions = permissions; }
        public String getAvatar() { return avatar; }
        public void setAvatar(String avatar) { this.avatar = avatar; }
        public java.util.List<Long> getAssignedProductIds() { return assignedProductIds; }
        public void setAssignedProductIds(java.util.List<Long> assignedProductIds) { this.assignedProductIds = assignedProductIds; }
    }

    public String getAccessToken() { return accessToken; }
    public void setAccessToken(String accessToken) { this.accessToken = accessToken; }
    public String getRefreshToken() { return refreshToken; }
    public void setRefreshToken(String refreshToken) { this.refreshToken = refreshToken; }
    public long getExpiresIn() { return expiresIn; }
    public void setExpiresIn(long expiresIn) { this.expiresIn = expiresIn; }
    public UserInfo getUser() { return user; }
    public void setUser(UserInfo user) { this.user = user; }
}
