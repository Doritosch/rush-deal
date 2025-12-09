package com.rushcrew.auth_service.auth.application.command;

public record LogoutCommand(
    String accessToken,
    String refreshToken
) {}