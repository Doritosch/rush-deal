package com.rushcrew.auth_service.auth.application.result;

public record LoginResult(
    Long userId,
    String email,
    String name,
    String accessToken,
    String refreshToken
) {}
