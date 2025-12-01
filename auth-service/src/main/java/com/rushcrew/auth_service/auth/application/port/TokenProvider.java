package com.rushcrew.auth_service.auth.application.port;

public interface TokenProvider {

    String generateToken(Long userId, String email, String role);
}
