package com.rushcrew.user_service.user.presentation.dto.response;

import com.rushcrew.user_service.user.application.result.UserAllResult;

public record UserAllResponse(
    Long userId,
    String email,
    String name,
    String role
) {
    public static UserAllResponse fromResult(UserAllResult result) {
        return new UserAllResponse(
            result.userId(),
            result.email(),
            result.name(),
            result.role()
        );
    }
}
