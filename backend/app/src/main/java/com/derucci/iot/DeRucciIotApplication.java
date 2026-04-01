package com.derucci.iot;

import com.derucci.iot.common.auth.EnableJwtAuth;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication(scanBasePackages = "com.derucci.iot")
@EnableJwtAuth
@EntityScan(basePackages = {
    "com.derucci.iot.user.entity",
    "com.derucci.iot.auth.entity",
    "com.derucci.iot.product.entity",
    "com.derucci.iot.thingmodel.entity",
    "com.derucci.iot.device.entity",
    "com.derucci.iot.ota.entity"
})
@EnableJpaRepositories(basePackages = {
    "com.derucci.iot.user.repository",
    "com.derucci.iot.auth.repository",
    "com.derucci.iot.product.repository",
    "com.derucci.iot.thingmodel.repository",
    "com.derucci.iot.device.repository",
    "com.derucci.iot.ota.repository"
})
public class DeRucciIotApplication {
    public static void main(String[] args) {
        SpringApplication.run(DeRucciIotApplication.class, args);
    }
}
