package com.rushcrew.api_gateway.config;

import jakarta.validation.constraints.NotEmpty;
import java.util.List;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@ConfigurationProperties(prefix = "gateway")
@Validated
public record GatewayProperties(
    @NotEmpty
    List<String> publicPaths
) {}
