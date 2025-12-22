package com.rushcrew.auth_service.auth.application.result;

public record UserInfoResult(
    Long id,
    String name,
    String role
) {}
