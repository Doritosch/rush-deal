package com.rushcrew.auth_service.auth.infrastructure.external.dto;

public record UserCreateResponse(
    Long userId,
    String email,
    String name,
    String role
) {
}
