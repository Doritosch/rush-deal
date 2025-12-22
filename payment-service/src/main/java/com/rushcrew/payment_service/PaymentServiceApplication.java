package com.rushcrew.payment_service;

import com.rushcrew.payment_service.infrastructure.config.PortOneSecretProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@EnableFeignClients
@ComponentScan(basePackages = {
		"com.rushcrew.payment_service",
		"com.rushcrew.common"
})
@EntityScan(basePackages = {
		"com.rushcrew.payment_service",
		"com.rushcrew.common"
})
@EnableConfigurationProperties(PortOneSecretProperties.class)
public class PaymentServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(PaymentServiceApplication.class, args);
	}

}
