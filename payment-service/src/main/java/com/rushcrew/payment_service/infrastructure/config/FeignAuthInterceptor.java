package com.rushcrew.payment_service.infrastructure.config;

import feign.RequestInterceptor;
import feign.RequestTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
public class FeignAuthInterceptor implements RequestInterceptor {
    @Override
    public void apply(RequestTemplate requestTemplate) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if(authentication != null && authentication.getCredentials() != null) {
            String token = authentication.getCredentials().toString();
            requestTemplate.header("Authorization", "Bearer " + token);
        }
    }
}
