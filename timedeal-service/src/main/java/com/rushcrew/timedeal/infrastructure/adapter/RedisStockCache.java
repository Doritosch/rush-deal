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
}
