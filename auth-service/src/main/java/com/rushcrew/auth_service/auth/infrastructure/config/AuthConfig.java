package com.rushcrew.auth_service.auth.infrastructure.config;

import com.rushcrew.auth_service.auth.application.policy.TokenPolicy;
import com.rushcrew.auth_service.auth.infrastructure.properties.JwtProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class AuthConfig {

    private final JwtProperties jwtProperties;

    @Bean
    public TokenPolicy tokenPolicy() {
        return new TokenPolicy(
            jwtProperties.refresh().expiration(),
            jwtProperties.access().expiration()
        );
    }
}