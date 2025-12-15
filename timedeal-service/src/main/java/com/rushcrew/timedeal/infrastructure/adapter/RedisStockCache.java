package com.rushcrew.timedeal.infrastructure.adapter;

import com.rushcrew.timedeal.domain.port.StockCache;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class RedisStockCache implements StockCache {

    private final StringRedisTemplate redisTemplate;

    @Override
    public void register(UUID stockId, Long available) {
        String key = "tds:" + stockId;
        redisTemplate.opsForValue().set(key, String.valueOf(available));
    }

    /**
     * quantity가 양수면 증가, 음수면 감소
     */
    @Override
    public void changeCount(UUID stockId, Long quantity) {
        redisTemplate.opsForValue().increment("tds:" + stockId, quantity);
    }

    @Override
    public void evict(UUID stockId) {
        redisTemplate.delete("tds:" + stockId);
    }

    @Override
    public void reserve(UUID stockId, Long quantity) {
        String key = "tds:" + stockId;
        redisTemplate.opsForValue().decrement(key, quantity);
    }

    @Override
    public void restore(UUID stockId, Long quantity) {
        String key = "tds:" + stockId;
        redisTemplate.opsForValue().increment(key, quantity);
    }
}
