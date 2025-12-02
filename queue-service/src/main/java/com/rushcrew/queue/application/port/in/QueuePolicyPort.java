package com.rushcrew.queue.application.port.in;

import com.rushcrew.queue.application.command.CreatePolicyCommand;
import com.rushcrew.queue.application.dto.QueuePolicyResponse;

/**
 * application 계층 usecase 정의
 */
public interface QueuePolicyPort {
    QueuePolicyResponse createQueuePolicy(CreatePolicyCommand command, Long userId);

}
