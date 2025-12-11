package com.rushcrew.auth_service.auth.application.command;

public record LogoutAllCommand(
    Long userId,
    String accessToken
) {}
