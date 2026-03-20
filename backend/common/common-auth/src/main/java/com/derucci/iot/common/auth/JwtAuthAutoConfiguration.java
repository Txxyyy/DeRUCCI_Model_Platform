package com.derucci.iot.common.auth;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

/**
 * JWT认证自动配置，读取jwt.*配置项并注册Filter和权限拦截器
 */
@Configuration
@EnableConfigurationProperties(JwtAuthAutoConfiguration.JwtProperties.class)
public class JwtAuthAutoConfiguration implements WebMvcConfigurer {

    @Bean
    public JwtUtils jwtUtils(JwtProperties props) {
        return new JwtUtils(props.getSecret(), props.getAccessExpiration(), props.getRefreshExpiration());
    }

    @Bean
    public FilterRegistrationBean<JwtAuthFilter> jwtAuthFilterRegistration(JwtUtils jwtUtils, JwtProperties props) {
        FilterRegistrationBean<JwtAuthFilter> registration = new FilterRegistrationBean<>();
        registration.setFilter(new JwtAuthFilter(jwtUtils, props.getWhitelist()));
        registration.addUrlPatterns("/api/*");
        registration.setOrder(1);
        return registration;
    }

    @Bean
    public PermissionInterceptor permissionInterceptor() {
        return new PermissionInterceptor();
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(permissionInterceptor())
                .addPathPatterns("/api/**");
    }

    @ConfigurationProperties(prefix = "jwt")
    public static class JwtProperties {
        /** 签名密钥（至少32字符） */
        private String secret = "DeRUCCI-IoT-Platform-JWT-Secret-Key-2026";
        /** accessToken有效期，默认2小时 */
        private long accessExpiration = 7200000;
        /** refreshToken有效期，默认7天 */
        private long refreshExpiration = 604800000;
        /** 白名单路径 */
        private List<String> whitelist = List.of("/api/auth/**");

        public String getSecret() { return secret; }
        public void setSecret(String secret) { this.secret = secret; }
        public long getAccessExpiration() { return accessExpiration; }
        public void setAccessExpiration(long accessExpiration) { this.accessExpiration = accessExpiration; }
        public long getRefreshExpiration() { return refreshExpiration; }
        public void setRefreshExpiration(long refreshExpiration) { this.refreshExpiration = refreshExpiration; }
        public List<String> getWhitelist() { return whitelist; }
        public void setWhitelist(List<String> whitelist) { this.whitelist = whitelist; }
    }
}
