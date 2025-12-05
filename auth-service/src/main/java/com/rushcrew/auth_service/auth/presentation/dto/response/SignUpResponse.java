package com.rushcrew.auth_service.auth.presentation.dto.response;

import com.rushcrew.auth_service.auth.application.result.SignUpResult;

public record SignUpResponse(
    Long userId,
    String email,
    String name,
    String accessToken,
    String refreshToken
) {
    public static SignUpResponse fromResult(SignUpResult result) {
        return new SignUpResponse(
            result.userId(),
            result.email(),
            result.name(),
            result.accessToken(),
            result.refreshToken()
        );
    }
}
