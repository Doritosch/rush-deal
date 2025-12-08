package com.rushcrew.api_gateway.blacklist;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class BlacklistService {

    private final ReactiveRedisTemplate<String, String> reactiveRedisTemplate;

    private static final String TOKEN_BLACKLIST_PREFIX = "blacklist:token:";
    private static final String USER_BLACKLIST_PREFIX = "blacklist:user:";

    /**
     * Access Token이 블랙리스트에 등록되어 있는지 확인
     */
    public Mono<Boolean> isTokenBlacklisted(String token) {
        String key = TOKEN_BLACKLIST_PREFIX + token;
        return reactiveRedisTemplate.hasKey(key);
    }

    /**
     * User ID가 블랙리스트에 등록되어 있는지 확인 (전체 로그아웃)
     */
    public Mono<Boolean> isUserBlacklisted(Long userId) {
        String key = USER_BLACKLIST_PREFIX + userId;
        return reactiveRedisTemplate.hasKey(key);
    }
}
