package com.rushcrew.user_service.user.presentation.dto.response;

import com.rushcrew.user_service.user.application.result.UserInfoResult;

public record UserInfoResponse(
    Long userId,
    String name,
    String role
) {
    public static UserInfoResponse fromResult(UserInfoResult result) {
        return new UserInfoResponse(
            result.userId(),
            result.name(),
            result.role()
        );
    }
}
