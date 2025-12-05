package com.rushcrew.auth_service.auth.domain.entity;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.rushcrew.auth_service.auth.domain.vo.TokenExpiry;
import com.rushcrew.auth_service.auth.domain.vo.TokenId;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class RefreshToken {

    private final TokenId id;
    private final Long userId;
    private final LocalDateTime issuedAt;
    private final TokenExpiry expiry;

    public static RefreshToken create(
        String tokenValue,
        Long userId,
        Long expiryMillis
    ) {
        LocalDateTime now = LocalDateTime.now();
        return new RefreshToken(
            TokenId.of(tokenValue),
            userId,
            now,
            TokenExpiry.fromMilliseconds(expiryMillis)
        );
    }


    @JsonCreator
    public static RefreshToken fromJson(
        @JsonProperty("id") TokenId id,
        @JsonProperty("userId") Long userId,
        @JsonProperty("issuedAt") LocalDateTime issuedAt,
        @JsonProperty("expiry") TokenExpiry expiry
    ) {
        return new RefreshToken(id, userId, issuedAt, expiry);
    }

    public boolean isExpired() {
        return expiry.isExpired();
    }

    public String getTokenValue() {
        return id.getValue();
    }
}
