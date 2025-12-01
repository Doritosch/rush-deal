package com.rushcrew.user_service.user.application.command;

public record UserCreateCommand(
    String email,
    String password,
    String name,
    String role
) {}
