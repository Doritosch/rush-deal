package com.rushcrew.queue.infrastructure.repository;

import com.rushcrew.queue.domain.entity.QueueToken;
import com.rushcrew.queue.domain.repository.QueueRepository;
import com.rushcrew.queue.domain.vo.TokenId;
import java.util.List;
import java.util.UUID;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class RedisQueueRepository implements QueueRepository {

    private final StringRedisTemplate redisTemplate;

    private static final String WAITING_KEY = "queue:wait:product:%s";
    private static final String ACTIVE_KEY = "queue:active:product:%s";

    public RedisQueueRepository(StringRedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    /**
     * 대기열 등록 (ZSet : Sorted Set)
     */
    @Override
    public void register(QueueToken token) {
        double score = System.currentTimeMillis();
        redisTemplate.opsForZSet().add(
                getWaitingKey(token.getProductId()),
                token.getId().getValue().toString(),
                score
            );
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
}
