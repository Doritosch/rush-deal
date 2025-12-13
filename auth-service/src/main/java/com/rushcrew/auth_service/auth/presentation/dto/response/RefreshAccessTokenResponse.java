package com.rushcrew.auth_service.auth.presentation.dto.response;

public record RefreshAccessTokenResponse(
    String accessToken
) {
    public static RefreshAccessTokenResponse of(String accessToken) {
        return new RefreshAccessTokenResponse(accessToken);
    }
}
