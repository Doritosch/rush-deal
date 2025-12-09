package com.rushcrew.timedeal;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = {
    "com.rushcrew.timedeal",
    "com.rushcrew.common"
})
@EntityScan(basePackages = {
    "com.rushcrew.timedeal",
    "com.rushcrew.common"
})
@EnableFeignClients
public class TimeDealApplication {

    public static void main(String[] args) {
        SpringApplication.run(TimeDealApplication.class, args);
    }
}