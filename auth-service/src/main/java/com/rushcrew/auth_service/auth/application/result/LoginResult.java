package com.rushcrew.auth_service.auth.application.result;

public record LoginResult(
    String accessToken,
    String refreshToken
) {}
