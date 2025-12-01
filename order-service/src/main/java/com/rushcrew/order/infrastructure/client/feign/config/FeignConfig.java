package com.rushcrew.order.infrastructure.client.feign.config;

import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableFeignClients(basePackages = "com.rushcrew.order.infrastructure.client.feign")
public class FeignConfig {
}
