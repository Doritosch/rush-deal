package com.rushcrew.user_service.user.presentation.dto.response;

import com.rushcrew.user_service.user.application.result.UserCreateResult;

public record UserCreateResponse(
    Long userId,
    String email,
    String name,
    String role
) {
    public static UserCreateResponse fromResult(UserCreateResult result) {
        return new UserCreateResponse(
            result.userId(),
            result.email(),
            result.name(),
            result.role()
        );
    }
}
