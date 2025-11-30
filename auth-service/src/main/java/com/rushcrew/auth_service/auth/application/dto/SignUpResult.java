package com.rushcrew.auth_service.auth.application.dto;

import java.util.Date;

public record SignUpResult(
    Long userId,
    String email,
    String name,
    String accessToken,
    Date createdAt
) {}
