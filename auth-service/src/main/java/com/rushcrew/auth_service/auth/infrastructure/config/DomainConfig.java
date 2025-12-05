package com.rushcrew.auth_service.auth.infrastructure.config;

import com.rushcrew.auth_service.auth.domain.policy.ConcurrentLoginPolicy;
import com.rushcrew.auth_service.auth.infrastructure.properties.ConcurrentLoginProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(ConcurrentLoginProperties.class)
public class DomainConfig {

    @Bean
    public ConcurrentLoginPolicy concurrentLoginPolicy(
        ConcurrentLoginProperties properties
    ) {
        return new ConcurrentLoginPolicy(properties.maxSessions());
    }
}
