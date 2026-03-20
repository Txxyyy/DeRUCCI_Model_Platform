package com.derucci.iot.common.auth;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * JWT认证过滤器，拦截请求验证token有效性
 */
public class JwtAuthFilter extends OncePerRequestFilter {

    private final JwtUtils jwtUtils;
    private final List<String> whitelist;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public JwtAuthFilter(JwtUtils jwtUtils, List<String> whitelist) {
        this.jwtUtils = jwtUtils;
        this.whitelist = whitelist;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        String path = request.getRequestURI();

        // 白名单放行
        if (isWhitelisted(path)) {
            filterChain.doFilter(request, response);
            return;
        }

        String authHeader = request.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            sendError(response, 401, "未提供认证token");
            return;
        }

        String token = authHeader.substring(7);
        try {
            AuthUser user = jwtUtils.parseAuthUser(token);
            AuthContext.setUser(user);
            filterChain.doFilter(request, response);
        } catch (Exception e) {
            sendError(response, 401, "token无效或已过期");
        } finally {
            AuthContext.clear();
        }
    }

    private boolean isWhitelisted(String path) {
        for (String pattern : whitelist) {
            if (pattern.endsWith("/**")) {
                String prefix = pattern.substring(0, pattern.length() - 3);
                if (path.startsWith(prefix)) return true;
            } else if (pattern.equals(path)) {
                return true;
            }
        }
        return false;
    }

    private void sendError(HttpServletResponse response, int status, String message) throws IOException {
        response.setStatus(status);
        response.setContentType("application/json;charset=UTF-8");
        Map<String, Object> body = Map.of("code", status, "message", message, "timestamp", System.currentTimeMillis());
        response.getWriter().write(objectMapper.writeValueAsString(body));
    }
}
