package com.derucci.iot.common.auth;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.Map;

/**
 * 权限拦截器，读取@RequirePermission注解并校验当前用户权限
 */
public class PermissionInterceptor implements HandlerInterceptor {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response,
                             Object handler) throws Exception {
        if (!(handler instanceof HandlerMethod)) {
            return true;
        }

        HandlerMethod method = (HandlerMethod) handler;
        RequirePermission annotation = method.getMethodAnnotation(RequirePermission.class);
        if (annotation == null) {
            return true;
        }

        AuthUser user = AuthContext.getUser();
        if (user == null) {
            sendForbidden(response, "未登录");
            return false;
        }

        // ADMIN拥有所有权限
        if (user.isAdmin()) {
            return true;
        }

        // 校验action权限（如PUBLISH/DELETE/OFFLINE/PUSH）
        String action = annotation.action();
        if (action != null && !action.isEmpty()) {
            if (!user.hasActionPermission(annotation.module(), action)) {
                sendForbidden(response, "权限不足");
                return false;
            }
        } else {
            // 校验access级别（R/RW）
            String permKey = annotation.module() + ":" + annotation.access();
            if (!user.hasPermission(permKey)) {
                sendForbidden(response, "权限不足");
                return false;
            }
        }

        // 校验产品范围（DEVELOPER仅能访问分配的产品）
        if (annotation.checkProductScope() && user.isDeveloper()) {
            Long productId = extractProductId(request);
            if (productId != null && !user.hasProductAccess(productId)) {
                sendForbidden(response, "权限不足");
                return false;
            }
        }

        return true;
    }

    /**
     * 从请求中提取productId（查询参数或路径变量）
     *
     * @param request HTTP请求
     * @return 产品ID，提取不到返回null
     */
    private Long extractProductId(HttpServletRequest request) {
        String productIdParam = request.getParameter("productId");
        if (productIdParam != null) {
            try {
                return Long.parseLong(productIdParam);
            } catch (NumberFormatException ignored) {}
        }
        return null;
    }

    private void sendForbidden(HttpServletResponse response, String message) throws Exception {
        response.setStatus(403);
        response.setContentType("application/json;charset=UTF-8");
        response.getWriter().write(objectMapper.writeValueAsString(
            Map.of("code", 403, "message", message)
        ));
    }
}