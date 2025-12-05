package com.rushcrew.auth_service.auth.application.command;

public record LoginCommand(
    String email,
    String password
) {}
