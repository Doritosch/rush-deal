package com.rushcrew.auth_service.auth.domain.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.rushcrew.auth_service.auth.domain.vo.TokenExpiry;
import com.rushcrew.auth_service.auth.domain.vo.UserId;
import java.time.LocalDateTime;
import java.time.ZoneId;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@JsonIgnoreProperties(ignoreUnknown = true)
public class BlacklistedUser {

    private final UserId id;
    private final TokenExpiry expiry;
    private final LocalDateTime blacklistedAt;

    public static BlacklistedUser create(UserId userId, LocalDateTime expiresAt) {
        return new BlacklistedUser(
            userId,
            TokenExpiry.create(expiresAt),
            LocalDateTime.now()
        );
    }

    public Long getUserId() {
        return id.getValue();
    }

    public long remainingMillis() {
        long expiresAtMillis = expiry.getExpiresAt()
            .atZone(ZoneId.systemDefault())
            .toInstant()
            .toEpochMilli();

        long nowMillis = System.currentTimeMillis();

        return Math.max(expiresAtMillis - nowMillis, 0);
    }
}
