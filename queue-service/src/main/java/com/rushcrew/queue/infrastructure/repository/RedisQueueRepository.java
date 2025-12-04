package com.rushcrew.queue.infrastructure.repository;

import com.rushcrew.queue.domain.entity.QueueToken;
import com.rushcrew.queue.domain.repository.QueueRepository;
import com.rushcrew.queue.domain.vo.TokenId;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Repository;

@Slf4j
@Repository
public class RedisQueueRepository implements QueueRepository {

    private final StringRedisTemplate redisTemplate;

    private static final String WAITING_KEY = "queue:wait:product:%s";
    private static final String ACTIVE_KEY = "queue:active:product:%s";
    private static final String USER_INDEX_KEY = "queue:user:product:%s:%s"; // String (중복방지용)

    public RedisQueueRepository(StringRedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    /**
     * 대기열 등록 (ZSet : Sorted Set)
     */
    @Override
    public boolean register(QueueToken token, LocalDateTime dealEndTime) {
        // TTL 계산 : (이벤트 종료 시간 - 현재 시간)
        long secondsUntilClose = Duration.between(LocalDateTime.now(), dealEndTime).getSeconds();

        if (secondsUntilClose < 0) {
            // 이미 종료된 이벤트면 진입 불가 처리
            return false;
        }

        // redis 사용자 인덱스 키 생성
        String userIndexKey = getUserIndexKey(token.getProductId(), token.getUserId());

        // 중복 방지 : 유저별 대기열 키 생성 (SETNX)
        // KEY: queue:user:product:{productId}:{userId} / VALUE: 토큰 UUID
        Boolean isNewUser = redisTemplate.opsForValue().setIfAbsent(
            userIndexKey,
            token.getId().getValue().toString(),
            Duration.ofMinutes(secondsUntilClose) // TTL 설정: 타임딜 종료 시간에 맞춰 자동 만료
        );

        if (Objects.equals(isNewUser, Boolean.FALSE)) {
            // 이미 대기 중인 유저
            return false;
        }

        // 대기열 추가 : ZSet에 등록
        try {
            double score = System.currentTimeMillis();
            redisTemplate.opsForZSet().add(
                getWaitingKey(token.getProductId()),
                token.getId().getValue().toString(),
                score
            );
            return true;
        } catch (Exception e) {
            // 보상 트랜잭션 : ZSet 저장 실패 시, 중복 방지 키(userIndexKey)도 삭제해줘야 유저가 다시 시도 가능
            log.error("[QUEUE:REDIS:ERROR] 대기열 등록 실패로 인한 롤백 수행: userId={}, tokenId={}", token.getUserId(), token.getId());
            redisTemplate.delete(userIndexKey);
            throw e;
        }
    }

    @Override
    public void activateTokens(UUID productId, List<String> tokens) {

    }

    @Override
    public boolean isActivatedToken(UUID productId, TokenId tokenId) {
        return Boolean.TRUE.equals(redisTemplate.opsForSet()
            .isMember(getActiveKey(productId),
                tokenId.getValue().toString()
            ));
    }

    @Override
    public Long getWaitingRank(UUID productId, TokenId tokenId) {
        // ZRANK key member (ZSet 조회)
        // Redis ZRANK 통하여 대기 순번 조회 성능을 O(log N)으로 최적화
        return redisTemplate.opsForZSet()
            .rank(getWaitingKey(productId),
                tokenId.getValue().toString()
            );
    }

    @Override
    public Double getWaitingScore(UUID productId, TokenId tokenId) {
        // Redis ZSCORE로 진입 시간 조회
        return redisTemplate.opsForZSet()
            .score(getWaitingKey(productId),
                tokenId.getValue().toString()
            );
    }

    /**
     * 본인 확인 (대기열 토큰 소유권 검증)
     */
    public boolean verifyTokenOwner(UUID productId, Long userId, String token) {
        // redis에 저장된 해당 유저 토큰 조회
        String savedToken = redisTemplate.opsForValue().get(
            getUserIndexKey(productId, userId)
        );

        // 저장된 토큰 없거나, 요청 토큰과 다르면 본인 아님
        return savedToken != null && savedToken.equals(token);
    }

    private String getWaitingKey(UUID productId) {
        return String.format(WAITING_KEY, productId);
    }

    private String getActiveKey(UUID productId) {
        return String.format(ACTIVE_KEY, productId);
    }

    private String getUserIndexKey(UUID productId, Long userId) {
        return String.format(USER_INDEX_KEY, productId, userId);
    }
}
