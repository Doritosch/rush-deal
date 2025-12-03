package com.rushcrew.user_service.user.presentation.dto.response;

import com.rushcrew.user_service.user.application.result.VerifyPasswordResult;

public record VerifyPasswordResponse(
    Long userId,
    String email,
    String name,
    String role
) {
    public static VerifyPasswordResponse fromResult(VerifyPasswordResult result) {
        return new VerifyPasswordResponse(
            result.userId(),
            result.email(),
            result.name(),
            result.role()
        );
    }
}
