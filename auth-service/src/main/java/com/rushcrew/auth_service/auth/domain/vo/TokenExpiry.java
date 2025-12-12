package com.rushcrew.auth_service.auth.domain.vo;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
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

    public static TokenExpiry create(LocalDateTime expiresAt) {
        if (expiresAt == null) {
            throw new IllegalArgumentException("만료 시간은 null일 수 없습니다.");
        }

        if (expiresAt.isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException("만료 시간은 현재 시간 이후여야 합니다.");
        }

        return new TokenExpiry(expiresAt.truncatedTo(ChronoUnit.SECONDS));
    }

    public static TokenExpiry fromMilliseconds(long millis) {
        if (millis <= 0) {
            throw new IllegalArgumentException("만료 시간은 양수여야 합니다");
        }

        LocalDateTime expiresAt = LocalDateTime.now().plus(
            millis,
            ChronoUnit.MILLIS
        );
        return create(expiresAt);
    }

    @JsonCreator
    public static TokenExpiry fromJson(
        @JsonProperty("expiresAt") LocalDateTime expiresAt
    ) {
        if (expiresAt == null) {
            throw new IllegalArgumentException("만료 시간은 null일 수 없습니다.");
        }
        return new TokenExpiry(expiresAt.truncatedTo(ChronoUnit.SECONDS));
    }

    @JsonIgnore
    public boolean isExpired() {
        return LocalDateTime.now().isAfter(expiresAt);
    }
}
