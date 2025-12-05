package com.rushcrew.auth_service.auth.infrastructure.external.dto;

import com.rushcrew.auth_service.auth.application.result.UserCreateResult;

public record UserCreateResponse(
    Long userId,
    String email,
    String name,
    String role
) {
    public UserCreateResult toResult() {
        return new UserCreateResult(userId, email, name, role);
    }
}
