package com.rushcrew.auth_service.auth.application.port;

public interface RefreshTokenProvider {
    String generateToken(Long userId);
    Long getUserIdFromToken(String token);
}
