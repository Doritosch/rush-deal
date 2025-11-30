package com.rushcrew.auth_service.auth.application.command;

public record SignUpCommand(
    String email,
    String password,
    String name,
    String role
) {}
