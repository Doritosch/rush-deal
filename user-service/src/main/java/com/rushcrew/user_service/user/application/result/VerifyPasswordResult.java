package com.rushcrew.user_service.user.application.result;

public record VerifyPasswordResult(
    Long userId,
    String email,
    String name,
    String role
) {}
