package com.rushcrew.auth_service.auth.application.command;

public record RefreshCommand(
    String refreshToken
) {}
