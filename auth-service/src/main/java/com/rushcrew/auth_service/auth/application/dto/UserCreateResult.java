package com.rushcrew.auth_service.auth.application.dto;

public record UserCreateResult(
    Long userId,
    String email,
    String name,
    String role
) {}
