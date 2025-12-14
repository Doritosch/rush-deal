package com.rushcrew.order_service.infrastructure.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import feign.Logger;
import feign.Request;
import feign.Retryer;

@Configuration
public class FeignConfig {

	@Bean
	public Logger.Level feignLoggerLevel() {
		return Logger.Level.BASIC;
	}

	@Bean
	public Request.Options requestOptions() {
		return new Request.Options(
			5000,  // connectTimeoutMillis
			10000  // readTimeoutMillis
		);
	}

	@Bean
	public Retryer retryer() {
		return new Retryer.Default(
			1000,      // period: 초기 재시도 간격
			2000,      // maxPeriod: 최대 재시도 간격
			3          // maxAttempts: 최대 재시도 횟수
		);
	}
}
