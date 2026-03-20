package com.derucci.iot.common.auth;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 权限校验注解，标注在Controller方法上，由PermissionInterceptor拦截校验
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface RequirePermission {

    /** 模块名：PRODUCT / CATEGORY_MODEL / PRODUCT_MODEL / DEVICE / OTA / USER */
    String module();

    /** 访问级别：R（只读）/ RW（读写），默认R */
    String access() default "R";

    /** 操作权限点：PUBLISH / DELETE / OFFLINE / PUSH，为空表示仅校验access */
    String action() default "";

    /** 是否校验产品范围（DEVELOPER仅能访问分配的产品） */
    boolean checkProductScope() default false;
}
