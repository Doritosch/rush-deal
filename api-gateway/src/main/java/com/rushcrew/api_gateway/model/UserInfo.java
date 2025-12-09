package com.rushcrew.api_gateway.model;

public record UserInfo(
    String userId,
    String email,
    String role
) {}