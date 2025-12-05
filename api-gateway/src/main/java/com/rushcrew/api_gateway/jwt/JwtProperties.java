package com.rushcrew.api_gateway.jwt;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@ConfigurationProperties(prefix = "jwt")
@Validated
public record JwtProperties(
    @NotNull
    AccessToken access
) {
    public record AccessToken(
        @NotBlank
        String secret,
        @Positive
        Long expiration
    ) {}
}
