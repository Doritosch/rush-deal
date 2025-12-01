package com.rushcrew.auth_service.auth.infrastructure.external.dto;

import com.rushcrew.auth_service.auth.application.result.VerifyPasswordResult;

public record VerifyPasswordResponse(
    Long userId,
    String email,
    String name,
    String role
) {

    public VerifyPasswordResult toResult() {
        return new VerifyPasswordResult(userId, email, name, role);
    }
}
