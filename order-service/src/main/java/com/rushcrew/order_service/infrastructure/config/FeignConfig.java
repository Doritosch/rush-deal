package com.rushcrew.order_service.infrastructure.config;

import java.util.concurrent.TimeUnit;

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
			5, TimeUnit.SECONDS,   // connect timeout
			10, TimeUnit.SECONDS,  // read timeout
			true                  // follow redirects
		);
	}

	@Bean
	public Retryer retryer() {
		return new Retryer.Default(
			1000,   // period (ms)
			2000,   // maxPeriod (ms)
			3       // maxAttempts
		);
	}
}
