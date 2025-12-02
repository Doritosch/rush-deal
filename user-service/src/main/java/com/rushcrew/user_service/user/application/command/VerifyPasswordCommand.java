package com.rushcrew.user_service.user.application.command;

public record VerifyPasswordCommand(
    String email,
    String password
) {}
