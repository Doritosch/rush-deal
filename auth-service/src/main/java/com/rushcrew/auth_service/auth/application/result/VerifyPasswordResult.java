package com.rushcrew.auth_service.auth.application.result;

public record VerifyPasswordResult(
    Long userId,
    String email,
    String name,
    String role
) {}
