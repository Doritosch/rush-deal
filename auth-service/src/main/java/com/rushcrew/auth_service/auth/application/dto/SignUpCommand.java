package com.rushcrew.auth_service.auth.application.dto;

public record SignUpCommand(
    String email,
    String password,
    String name,
    String role
) {}
