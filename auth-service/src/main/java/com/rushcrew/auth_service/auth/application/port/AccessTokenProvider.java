package com.rushcrew.auth_service.auth.application.port;

import java.time.LocalDateTime;

public interface AccessTokenProvider {
    String generateToken(Long userId, String email, String role);
    LocalDateTime getExpiryDate(String token);
}
