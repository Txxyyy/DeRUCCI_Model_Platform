package com.derucci.iot.auth;

import com.derucci.iot.common.auth.EnableJwtAuth;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * 认证服务启动类，共享user-db数据库
 */
@SpringBootApplication
@EnableJwtAuth
public class AuthServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(AuthServiceApplication.class, args);
    }
}
