package com.rushcrew.auth_service.auth.presentation.dto.response;

import com.rushcrew.auth_service.auth.application.result.LoginResult;

public record LoginResponse(
    Long userId,
    String email,
    String name,
    String accessToken,
    String refreshToken
) {
    public static LoginResponse fromResult(LoginResult result) {
        return new LoginResponse(
            result.userId(),
            result.email(),
            result.name(),
            result.accessToken(),
            result.refreshToken()
        );
    }
}
