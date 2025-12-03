package com.rushcrew.queue.infrastructure.repository;

import com.rushcrew.queue.domain.entity.QueueToken;
import com.rushcrew.queue.domain.repository.QueueRepository;
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
    public void activateTokens(Long productId, List<String> tokens) {

    }

    @Override
    public boolean isActivatedToken(Long productId, QueueToken token) {
        return Boolean.TRUE.equals(redisTemplate.opsForSet()
            .isMember(getActiveKey(token.getProductId()),
                token.getId().getValue().toString()
            ));
    }

    @Override
    public Long getWaitingRank(UUID productId, QueueToken token) {
        // ZRANK key member
        return redisTemplate.opsForZSet()
            .rank(getWaitingKey(token.getProductId()),
                token.getId().getValue().toString()
            );
    }

    private String getWaitingKey(UUID productId) {
        return String.format(WAITING_KEY, productId);
    }

    private String getActiveKey(UUID productId) {
        return String.format(ACTIVE_KEY, productId);
    }
}
