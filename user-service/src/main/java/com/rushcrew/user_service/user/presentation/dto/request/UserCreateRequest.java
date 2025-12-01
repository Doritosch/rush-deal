package com.rushcrew.user_service.user.presentation.dto.request;

import com.rushcrew.user_service.user.application.command.UserCreateCommand;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UserCreateRequest(
    @NotBlank(message = "이메일은 필수입니다")
    @Email(message = "올바른 이메일 형식이 아닙니다")
    String email,

    @NotBlank(message = "비밀번호는 필수입니다")
    @Size(min = 8, message = "비밀번호는 최소 8자 이상이어야 합니다")
    String password,

    @NotBlank(message = "이름은 필수입니다") String name,

    @NotBlank(message = "역할은 필수입니다") String role
) {
    public UserCreateCommand toCommand() {
        return new UserCreateCommand(email, password, name, role);
    }
}
