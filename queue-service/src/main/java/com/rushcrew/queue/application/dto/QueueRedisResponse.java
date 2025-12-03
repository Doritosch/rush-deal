package com.rushcrew.queue.application.dto;


import com.rushcrew.queue.domain.enums.QueueStatus;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.Builder;

@Builder
public record QueueRedisResponse(
    UUID token,
    UUID productId,
    Long rank,
    QueueStatus status,
    LocalDateTime enteredAt // 대기열 진입 요청 시간
) {

}
