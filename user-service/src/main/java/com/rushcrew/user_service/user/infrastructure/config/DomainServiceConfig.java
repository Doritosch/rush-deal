package com.rushcrew.user_service.user.infrastructure.config;

import com.rushcrew.user_service.user.domain.repository.UserRepository;
import com.rushcrew.user_service.user.domain.service.UserReader;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DomainServiceConfig {

    @Bean
    public UserReader userReader(UserRepository userRepository) {
        return new UserReader(userRepository);
    }
}