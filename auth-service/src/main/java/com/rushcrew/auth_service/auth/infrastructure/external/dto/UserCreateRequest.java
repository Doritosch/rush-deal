package com.rushcrew.auth_service.auth.infrastructure.external.dto;

import com.rushcrew.auth_service.auth.application.command.SignUpCommand;

public record UserCreateRequest(
    String email,
    String password,
    String name,
    String role
) {
    public static UserCreateRequest fromCommand(SignUpCommand command) {
        return new UserCreateRequest(
            command.email(),
            command.password(),
            command.name(),
            command.role()
        );
    }
}
