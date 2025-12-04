package com.rushcrew.queue.infrastructure.repository;

import com.rushcrew.queue.domain.entity.QueueToken;
import com.rushcrew.queue.domain.repository.QueueRepository;
import com.rushcrew.queue.domain.vo.TokenId;
import java.time.Duration;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Repository;

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
    public boolean register(QueueToken token) {
        // 중복 방지 : 유저별 대기열 키 생성 (SETNX)
        // Key: queue:user:product:{productId}:{userId} -> Value: TokenUUID
        Boolean isNewUser = redisTemplate.opsForValue().setIfAbsent(
            getUserIndexKey(token.getProductId(), token.getUserId()),
            token.getId().getValue().toString()
        );

        if (Objects.equals(isNewUser, Boolean.FALSE)) {
            // 이미 대기 중인 유저
            return false;
        }

        // 대기열 추가 : ZSet에 등록
        double score = System.currentTimeMillis();
        redisTemplate.opsForZSet().add(
                getWaitingKey(token.getProductId()),
                token.getId().getValue().toString(),
                score
            );
        return true;
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
