package com.rushcrew.auth_service.auth.presentation.dto.response;

import com.rushcrew.auth_service.auth.application.result.SignUpResult;

public record SignUpResponse(
    Long userId,
    String email,
    String name,
    String accessToken
) {
    public static SignUpResponse from(SignUpResult result) {
        return new SignUpResponse(
            result.userId(),
            result.email(),
            result.name(),
            result.accessToken()
        );
    }
}
