package com.derucci.iot.thingmodel;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;

@SpringBootApplication
@EntityScan(basePackages = {"com.derucci.iot.thingmodel.entity", "com.derucci.iot.common.core.entity"})
public class ThingModelServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(ThingModelServiceApplication.class, args);
    }
}
