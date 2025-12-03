package com.rushcrew.queue.application.port.in;

import com.rushcrew.queue.application.command.CreatePolicyCommand;
import com.rushcrew.queue.application.dto.QueuePolicyQueryResponse;
import java.util.UUID;

/**
 * application 계층 usecase 정의
 */
public interface QueuePolicyPort {
    QueuePolicyQueryResponse createQueuePolicy(CreatePolicyCommand command, Long userId);

    QueuePolicyQueryResponse getQueuePolicyInfo(UUID policyId);

}
