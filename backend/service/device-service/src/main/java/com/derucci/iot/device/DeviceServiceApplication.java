package com.derucci.iot.device;

import com.derucci.iot.common.auth.EnableJwtAuth;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;

/** 设备服务启动类，提供设备注册、状态管理等核心能力 */
@SpringBootApplication
@EnableJwtAuth
@EntityScan(basePackages = {"com.derucci.iot.device.entity", "com.derucci.iot.common.core.entity"})
public class DeviceServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(DeviceServiceApplication.class, args);
    }
}
