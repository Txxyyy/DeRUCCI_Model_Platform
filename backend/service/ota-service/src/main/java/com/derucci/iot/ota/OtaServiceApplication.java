package com.derucci.iot.ota;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;

@SpringBootApplication
@EntityScan(basePackages = {"com.derucci.iot.ota.entity", "com.derucci.iot.common.core.entity"})
public class OtaServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(OtaServiceApplication.class, args);
    }
}
