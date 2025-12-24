package com.rushcrew.queue.presentation.mapper;

import com.rushcrew.queue.application.dto.QueueRedisResponse;
import com.rushcrew.queue.domain.enums.QueueStatus;
import com.rushcrew.queue.presentation.dto.response.QueueResponse;
import org.springframework.stereotype.Component;

@Component
public class QueuePresentationMapper {

    public QueueResponse toQueueResponse(QueueRedisResponse redisResponse) {
        String message = redisResponse.status().equals(QueueStatus.WAITING) ?
            "대기 중입니다." : "입장 가능합니다.";
        return QueueResponse.builder()
            .token(redisResponse.token())
            .productId(redisResponse.productId())
            .rank(redisResponse.rank())
            .status(redisResponse.status().getDescription())
            .enteredAt(redisResponse.enteredAt())
            .message(message)
            .build();
    }
}
