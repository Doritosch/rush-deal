package com.rushcrew.auth_service.auth.presentation.dto.response;

import com.rushcrew.auth_service.auth.application.result.LoginResult;

public record LoginResponse(
    String accessToken
) {
    public static LoginResponse fromResult(LoginResult result) {
        return new LoginResponse(result.accessToken());
    }
}
