package com.derucci.iot.user;

import com.derucci.iot.common.auth.EnableJwtAuth;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;

@SpringBootApplication
@EntityScan(basePackages = {"com.derucci.iot.user.entity", "com.derucci.iot.common.core.entity"})
@EnableJwtAuth
public class UserServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(UserServiceApplication.class, args);
    }
}
