package com.rushcrew.user_service.user.application.result;

public record UserCreateResult(
    Long userId,
    String email,
    String name,
    String role
) {}
