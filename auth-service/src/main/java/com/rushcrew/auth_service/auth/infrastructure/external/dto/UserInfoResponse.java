package com.rushcrew.auth_service.auth.infrastructure.external.dto;

import com.rushcrew.auth_service.auth.application.result.UserInfoResult;

public record UserInfoResponse(
    Long userId,
    String name,
    String role
) {
    public UserInfoResult toResult() {
        return new UserInfoResult(userId, name, role);
    }
}
