package com.rushcrew.payment_service.infrastructure.config;

import io.portone.sdk.server.payment.PaymentClient;
import io.portone.sdk.server.webhook.WebhookVerifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class PortOneConfig {

    @Bean
    public PaymentClient portonePaymentClient(PortOneSecretProperties secret) {
        return new PaymentClient(
                secret.api(),
                "https://api.portone.io",
                secret.storeId());
    }

    @Bean
    public WebhookVerifier portoneWebhookVerifier(PortOneSecretProperties secret) {
        return new WebhookVerifier(
                secret.webhook()
        );
    }
}
