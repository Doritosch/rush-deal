package com.rushcrew.queue.application.command;

import com.rushcrew.queue.domain.enums.QueueStatus;
import com.rushcrew.queue.domain.vo.TokenId;
import java.util.UUID;

public record EnterQueueCommand(
    TokenId id, // Redis 토큰 발급
    Long userId,
    UUID productId,
    QueueStatus status,
    Long requestTime, // 대기열 진입 요청 시간 (Score용)
    Long activeTime  // 활성화 시간 (TTL 계산용)
) {

}
