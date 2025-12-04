package com.rushcrew.queue.application.port.in;

import com.rushcrew.queue.application.command.queue.EnterQueueCommand;
import com.rushcrew.queue.application.dto.QueueRedisResponse;
import java.util.UUID;

public interface QueuePort {
    /**
     * 대기열 진입 (토큰 발급)
     */
    QueueRedisResponse enterQueue(EnterQueueCommand command);

    /**
     * 대기 상태 조회 (Polling)
     */
    QueueRedisResponse getQueueRank(UUID productId, String token, Long userId, String role);
}
