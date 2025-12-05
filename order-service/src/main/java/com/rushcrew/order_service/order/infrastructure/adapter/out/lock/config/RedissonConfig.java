package com.rushcrew.order_service.order.infrastructure.adapter.out.lock.config;

import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;

@Configuration
public class RedissonConfig {
	@Bean
	public RedissonClient redissonClient() throws Exception {
		Config config = Config.fromYAML(new ClassPathResource("redisson.yml").getInputStream());
		return Redisson.create(config);
	}
}

