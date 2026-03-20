package com.derucci.iot.common.auth;

/**
 * 认证上下文，基于ThreadLocal存储当前请求的用户信息
 */
public final class AuthContext {

    private static final ThreadLocal<AuthUser> CURRENT_USER = new ThreadLocal<>();

    private AuthContext() {}

    /**
     * 设置当前用户
     *
     * @param user 认证用户信息
     */
    public static void setUser(AuthUser user) {
        CURRENT_USER.set(user);
    }

    /**
     * 获取当前用户
     *
     * @return 当前认证用户，未认证时返回null
     */
    public static AuthUser getUser() {
        return CURRENT_USER.get();
    }

    /**
     * 清除当前用户（请求结束时调用，防止内存泄漏）
     */
    public static void clear() {
        CURRENT_USER.remove();
    }
}
