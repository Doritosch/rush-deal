package com.rushcrew.auth_service.auth.presentation.repository;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rushcrew.auth_service.auth.domain.entity.RefreshToken;
import com.rushcrew.auth_service.auth.domain.repository.RefreshTokenRepository;
import com.rushcrew.auth_service.auth.infrastructure.properties.JwtProperties;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Repository;

@Slf4j
@Repository
@RequiredArgsConstructor
public class RedisRefreshTokenRepository implements RefreshTokenRepository {

    private final StringRedisTemplate redisTemplate;
    private final ObjectMapper objectMapper;
    private final JwtProperties jwtProperties;

    private static final String TOKEN_PREFIX = "refresh_token:";
    private static final String USER_TOKENS_PREFIX = "user_tokens:";

    @Override
    public void save(RefreshToken token) {
        try {
            String json = objectMapper.writeValueAsString(token);

            String tokenKey = TOKEN_PREFIX + token.getTokenValue();
            String userTokensKey = USER_TOKENS_PREFIX + token.getUserId();

            // JwtProperties에서 만료 시간 가져오기
            long expirationMs = jwtProperties.refresh().expiration();

            // Token 저장 (만료 시간 적용)
            redisTemplate
                .opsForValue()
                .set(tokenKey, json, expirationMs, TimeUnit.MILLISECONDS);

            // User의 Token 리스트에 추가
            redisTemplate
                .opsForList()
                .rightPush(userTokensKey, token.getTokenValue());

            // User Token 리스트에도 만료 시간 적용
            redisTemplate.expire(
                userTokensKey,
                expirationMs,
                TimeUnit.MILLISECONDS
            );
        } catch (Exception e) {
            throw new RuntimeException("토큰 저장 실패했습니다.", e);
        }
    }

    @Override
    public Optional<RefreshToken> findByToken(String tokenValue) {
        try {
            String json = redisTemplate
                .opsForValue()
                .get(TOKEN_PREFIX + tokenValue);

            if (json == null) {
                return Optional.empty();
            }

            RefreshToken token = objectMapper.readValue(
                json,
                RefreshToken.class
            );
            return Optional.of(token);
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    @Override
    public List<String> findAllTokensByUserId(Long userId) {
        String key = USER_TOKENS_PREFIX + userId.toString();

        List<String> tokens = redisTemplate.opsForList().range(key, 0, -1);

        if (tokens == null) {
            return List.of();
        }

        return tokens;
    }

    @Override
    public void deleteByToken(String tokenValue) {
        Optional<RefreshToken> tokenOpt = findByToken(tokenValue);

        if (tokenOpt.isEmpty()) {
            log.debug("Token not found for deletion: token={}", tokenValue);
            return;
        }

        RefreshToken token = tokenOpt.get();

        // Token 삭제
        redisTemplate.delete(TOKEN_PREFIX + tokenValue);

        // User Token 리스트에서 제거
        redisTemplate
            .opsForList()
            .remove(USER_TOKENS_PREFIX + token.getUserId(), 0, tokenValue);

        log.debug(
            "RefreshToken deleted: userId={}, token={}",
            token.getUserId(),
            tokenValue
        );
    }

    @Override
    public void deleteAllByUserId(Long userId) {
        String userTokenKey = USER_TOKENS_PREFIX + userId;

        List<String> tokens = findAllTokensByUserId(userId);

        if (!tokens.isEmpty()) {
            // 모든 Token 삭제
            List<String> tokenKeys = tokens
                .stream()
                .map(token -> TOKEN_PREFIX + token)
                .toList();

            redisTemplate.delete(tokenKeys);
        }

        // User Token 리스트 삭제
        redisTemplate.delete(userTokenKey);
    }
}
