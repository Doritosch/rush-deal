package com.rushcrew.auth_service.auth.infrastructure.repository;

import com.rushcrew.auth_service.auth.domain.entity.BlacklistedToken;
import com.rushcrew.auth_service.auth.domain.repository.AccessTokenBlacklistRepository;
import java.util.concurrent.TimeUnit;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Repository;

@Slf4j
@Repository
@RequiredArgsConstructor
public class RedisAccessTokenBlacklistRepository implements AccessTokenBlacklistRepository {

    private final StringRedisTemplate redisTemplate;

    private static final String BLACKLIST_PREFIX = "blacklist:token:";
    private static final String BLACKLIST_MARKER = "revoked";

    @Override
    public void addToBlacklist(BlacklistedToken token) {
        try {
            String tokenKey = BLACKLIST_PREFIX + token.getTokenValue();

            long ttlMillis = token.remainingMillis();

            if (ttlMillis <= 0) {
                return;
            }

            redisTemplate
                .opsForValue()
                .setIfAbsent(
                    tokenKey,
                    BLACKLIST_MARKER,
                    ttlMillis,
                    TimeUnit.MILLISECONDS
                );
        } catch (Exception e) {
            throw new RuntimeException("블랙리스트 저장 실패했습니다.", e);
        }
    }
}
