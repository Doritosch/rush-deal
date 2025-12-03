package com.rushcrew.queue.infrastructure.repository;

import com.rushcrew.queue.domain.entity.QueueToken;
import com.rushcrew.queue.domain.repository.QueueTokenRepository;
import java.util.List;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class RedisQueueRepository implements QueueTokenRepository {

    private final RedisTemplate<String, QueueToken> redisTemplate;

    public RedisQueueRepository(RedisTemplate<String, QueueToken> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    @Override
    public void register(QueueToken token) {

    }

    @Override
    public void activateTokens(Long productId, List<String> tokens) {

    }

    @Override
    public boolean isActivatedToken(Long productId, String token) {
        return false;
    }

    @Override
    public Long getRank(Long productId, String token) {
        return 0L;
    }
}
