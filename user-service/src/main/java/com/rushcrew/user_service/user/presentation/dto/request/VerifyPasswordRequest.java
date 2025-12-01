package com.rushcrew.user_service.user.presentation.dto.request;

import com.rushcrew.user_service.user.application.command.VerifyPasswordCommand;

public record VerifyPasswordRequest(
    String email,
    String password
) {
    public VerifyPasswordCommand toCommand() {
        return new VerifyPasswordCommand(email, password);
    }
}
