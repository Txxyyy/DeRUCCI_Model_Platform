package com.derucci.iot.thingmodel;

import com.derucci.iot.common.auth.EnableJwtAuth;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;

/** 物模型服务启动类 */
@SpringBootApplication
@EnableJwtAuth
@EntityScan(basePackages = {"com.derucci.iot.thingmodel.entity", "com.derucci.iot.common.core.entity"})
public class ThingModelServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(ThingModelServiceApplication.class, args);
    }
}
