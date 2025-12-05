package com.rushcrew.user_service.user.application.result;

public record UserInfoResult(
    Long userId,
    String name,
    String role
) {}