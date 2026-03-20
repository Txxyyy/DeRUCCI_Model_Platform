package com.derucci.iot.common.auth;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.*;

/**
 * JWT工具类，负责token的生成、解析和验证
 */
public class JwtUtils {

    private final SecretKey key;
    /** accessToken有效期（毫秒），默认2小时 */
    private final long accessExpiration;
    /** refreshToken有效期（毫秒），默认7天 */
    private final long refreshExpiration;

    public JwtUtils(String secret, long accessExpiration, long refreshExpiration) {
        this.key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        this.accessExpiration = accessExpiration;
        this.refreshExpiration = refreshExpiration;
    }

    /**
     * 生成accessToken
     *
     * @param user 用户信息
     * @return JWT token字符串
     */
    @SuppressWarnings("unchecked")
    public String generateAccessToken(AuthUser user) {
        Date now = new Date();
        var builder = Jwts.builder()
                .subject(String.valueOf(user.getId()))
                .claim("username", user.getUsername())
                .claim("role", user.getRole())
                .claim("permissions", user.getPermissions() != null ? user.getPermissions() : List.of());
        // DEVELOPER角色写入分配的产品ID列表
        if ("DEVELOPER".equals(user.getRole()) && user.getAssignedProductIds() != null
                && !user.getAssignedProductIds().isEmpty()) {
            builder.claim("productIds", user.getAssignedProductIds().stream()
                    .map(String::valueOf).collect(java.util.stream.Collectors.joining(",")));
        }
        return builder
                .issuedAt(now)
                .expiration(new Date(now.getTime() + accessExpiration))
                .signWith(key)
                .compact();
    }

    /**
     * 生成refreshToken
     *
     * @param userId 用户ID
     * @return refresh token字符串
     */
    public String generateRefreshToken(Long userId) {
        Date now = new Date();
        return Jwts.builder()
                .subject(String.valueOf(userId))
                .claim("type", "refresh")
                .issuedAt(now)
                .expiration(new Date(now.getTime() + refreshExpiration))
                .signWith(key)
                .compact();
    }

    /**
     * 解析token获取Claims
     *
     * @param token JWT token字符串
     * @return Claims对象
     */
    public Claims parseToken(String token) {
        return Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    /**
     * 从token解析出AuthUser
     *
     * @param token JWT token字符串
     * @return 认证用户信息
     */
    @SuppressWarnings("unchecked")
    public AuthUser parseAuthUser(String token) {
        Claims claims = parseToken(token);
        AuthUser user = new AuthUser();
        user.setId(Long.parseLong(claims.getSubject()));
        user.setUsername(claims.get("username", String.class));
        user.setRole(claims.get("role", String.class));
        Object perms = claims.get("permissions");
        if (perms instanceof List) {
            user.setPermissions((List<String>) perms);
        } else {
            user.setPermissions(List.of());
        }
        // 解析DEVELOPER的产品ID列表
        String productIdsStr = claims.get("productIds", String.class);
        if (productIdsStr != null && !productIdsStr.isEmpty()) {
            user.setAssignedProductIds(
                Arrays.stream(productIdsStr.split(","))
                    .map(Long::parseLong)
                    .collect(java.util.stream.Collectors.toList())
            );
        }
        return user;
    }

    /**
     * 从refreshToken解析用户ID
     *
     * @param refreshToken refresh token字符串
     * @return 用户ID
     */
    public Long parseRefreshTokenUserId(String refreshToken) {
        Claims claims = parseToken(refreshToken);
        if (!"refresh".equals(claims.get("type", String.class))) {
            throw new IllegalArgumentException("不是有效的refreshToken");
        }
        return Long.parseLong(claims.getSubject());
    }

    public long getAccessExpiration() {
        return accessExpiration;
    }
}
