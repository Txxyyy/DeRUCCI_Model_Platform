package com.derucci.iot.ota;

import com.derucci.iot.common.auth.EnableJwtAuth;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;

/** OTA服务启动类，负责固件管理和升级任务调度 */
@SpringBootApplication
@EnableJwtAuth
@EntityScan(basePackages = {"com.derucci.iot.ota.entity", "com.derucci.iot.common.core.entity"})
public class OtaServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(OtaServiceApplication.class, args);
    }
}
