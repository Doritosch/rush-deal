package com.rushcrew.auth_service.auth.application.result;

public record TokenPairResult(
    String accessToken,
    String refreshToken
) {}
