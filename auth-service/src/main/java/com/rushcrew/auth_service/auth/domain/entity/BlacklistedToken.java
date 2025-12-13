package com.rushcrew.auth_service.auth.domain.entity;

import com.rushcrew.auth_service.auth.domain.vo.TokenExpiry;
import com.rushcrew.auth_service.auth.domain.vo.TokenId;
import java.time.LocalDateTime;
import java.time.ZoneId;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class BlacklistedToken {

    private final TokenId id;
    private final TokenExpiry expiry;
    private final LocalDateTime blacklistedAt;

    public static BlacklistedToken create(
        TokenId tokenId,
        LocalDateTime expiresAt
    ) {
        return new BlacklistedToken(
            tokenId,
            TokenExpiry.create(expiresAt),
            LocalDateTime.now()
        );
    }

    public long remainingMillis() {
        long expiresAtMillis = expiry
            .getExpiresAt()
            .atZone(ZoneId.systemDefault())
            .toInstant()
            .toEpochMilli();

        long nowMillis = System.currentTimeMillis();

        return Math.max(expiresAtMillis - nowMillis, 0);
    }

    public String getTokenValue() {
        return id.getValue();
    }
}
