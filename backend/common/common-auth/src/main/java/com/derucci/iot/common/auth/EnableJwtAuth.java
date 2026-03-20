package com.derucci.iot.common.auth;

import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

/**
 * 启用JWT认证，在服务主类上添加此注解即可接入认证体系
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Import(JwtAuthAutoConfiguration.class)
public @interface EnableJwtAuth {
}
