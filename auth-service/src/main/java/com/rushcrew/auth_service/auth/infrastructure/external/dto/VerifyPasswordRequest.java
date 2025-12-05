package com.rushcrew.auth_service.auth.infrastructure.external.dto;

import com.rushcrew.auth_service.auth.application.command.LoginCommand;

public record VerifyPasswordRequest(
    String email,
    String password
) {

    public static VerifyPasswordRequest fromCommand(LoginCommand command) {
        return new VerifyPasswordRequest(
            command.email(),
            command.password()
        );
    }
}
