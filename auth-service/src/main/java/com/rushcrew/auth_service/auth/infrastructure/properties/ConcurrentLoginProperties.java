package com.rushcrew.auth_service.auth.infrastructure.properties;

import jakarta.validation.constraints.Positive;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@ConfigurationProperties(prefix = "auth.concurrent")
@Validated
public record ConcurrentLoginProperties(
    @Positive
    int maxSessions
) {}
