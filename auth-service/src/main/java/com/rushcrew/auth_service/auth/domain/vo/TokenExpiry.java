package com.rushcrew.auth_service.auth.domain.vo;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class TokenExpiry {

    private final LocalDateTime expiresAt;

    public static TokenExpiry fromMilliseconds(long millis) {
        if (millis <= 0) {
            throw new IllegalArgumentException("만료 일수는 양수여야 합니다");
        }

        return new TokenExpiry(LocalDateTime.now().plus(millis, ChronoUnit.MILLIS));
    }

    @JsonCreator
    public static TokenExpiry fromJson(
        @JsonProperty("expiresAt") LocalDateTime expiresAt
    ) {
        return new TokenExpiry(expiresAt);
    }

    public boolean isExpired() {
        return LocalDateTime.now().isAfter(expiresAt);
    }
}
