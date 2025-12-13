package com.rushcrew.auth_service.auth.application.policy;

public record TokenPolicy(
    long refreshExpirationMillis,
    long accessExpirationMillis
) {}