package com.rushcrew.queue.application.port.in;

import com.rushcrew.queue.application.command.EnterQueueCommand;
import com.rushcrew.queue.application.dto.QueueRedisResponse;

public interface QueuePort {
    // 대기열 진입 (토큰 발급)
    QueueRedisResponse enterQueue(EnterQueueCommand command);

}
