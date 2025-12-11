package com.rushcrew.api_gateway.config;

import com.rushcrew.api_gateway.exception.GatewayErrorWebExceptionHandler;
import org.springframework.boot.autoconfigure.web.WebProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.reactive.error.ErrorAttributes;
import org.springframework.boot.web.reactive.error.ErrorWebExceptionHandler;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.http.codec.ServerCodecConfigurer;

@Configuration
@EnableConfigurationProperties(WebProperties.class)
public class ExceptionConfig {

    @Bean
    @Order(-2)
    public ErrorWebExceptionHandler gatewayErrorWebExceptionHandler(
        ErrorAttributes errorAttributes,
        WebProperties webProperties,
        ApplicationContext applicationContext,
        ServerCodecConfigurer serverCodecConfigurer
    ) {
        return new GatewayErrorWebExceptionHandler(
            errorAttributes,
            webProperties.getResources(),
            applicationContext,
            serverCodecConfigurer
        );
    }
}
