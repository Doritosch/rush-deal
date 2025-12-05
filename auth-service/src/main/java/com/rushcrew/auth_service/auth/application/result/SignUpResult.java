package com.rushcrew.auth_service.auth.application.result;

public record SignUpResult(
    Long userId,
    String email,
    String name,
    String accessToken,
    String refreshToken
) {}
