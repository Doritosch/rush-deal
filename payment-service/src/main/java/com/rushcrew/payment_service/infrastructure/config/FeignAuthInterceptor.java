package com.rushcrew.payment_service.infrastructure.config;

import com.rushcrew.payment_service.global.security.model.UserDetailsImpl;
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

        if(authentication != null && authentication.getPrincipal() instanceof UserDetailsImpl userDetails) {
            requestTemplate.header("X-User-Id", String.valueOf(userDetails.userId()));
            requestTemplate.header("X-User-Email", userDetails.email());
            requestTemplate.header("X-User-Role", userDetails.role());
        }
    }
}
