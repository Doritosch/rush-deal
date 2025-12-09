package com.rushcrew.auth_service.auth.infrastructure.repository;

import com.rushcrew.auth_service.auth.domain.entity.BlacklistedUser;
import com.rushcrew.auth_service.auth.domain.repository.UserBlacklistRepository;
import java.util.concurrent.TimeUnit;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Repository;

@Slf4j
@Repository
@RequiredArgsConstructor
public class RedisUserBlacklistRepository implements UserBlacklistRepository {

    private final StringRedisTemplate redisTemplate;

    private static final String USER_BLACKLIST_PREFIX = "blacklist:user:";
    private static final String REVOKED_MARKER = "all_revoked";

    @Override
    public void blacklistUser(BlacklistedUser user) {
        try {
            String key = USER_BLACKLIST_PREFIX + user.getUserId();
            long ttl = user.remainingMillis();

            if (ttl <= 0) {
                return;
            }

            redisTemplate.opsForValue().set(
                key,
                REVOKED_MARKER,
                ttl,
                TimeUnit.MILLISECONDS
            );

        } catch (Exception e) {
            throw new RuntimeException("사용자 블랙리스트 저장 실패", e);
        }
    }
}
