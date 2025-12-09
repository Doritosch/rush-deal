package com.rushcrew.payment_service.infrastructure.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@ConfigurationProperties("portone.secret")
public record PortOneSecretProperties(String api, String webhook, String storeId, String channelKey) {
}
