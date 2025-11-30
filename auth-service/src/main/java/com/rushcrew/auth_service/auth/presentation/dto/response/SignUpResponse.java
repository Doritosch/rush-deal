package com.rushcrew.auth_service.auth.presentation.dto.response;

import com.rushcrew.auth_service.auth.application.result.SignUpResult;
import java.util.Date;

public record SignUpResponse(
    Long userId,
    String email,
    String name,
    String accessToken,
    Date createdAt
) {
    public static SignUpResponse from(SignUpResult result) {
        return new SignUpResponse(
            result.userId(),
            result.email(),
            result.name(),
            result.accessToken(),
            result.createdAt()
        );
    }
}
