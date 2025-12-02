package com.rushcrew.queue.application.port.in;

import com.rushcrew.queue.application.command.CreatePolicyCommand;
import com.rushcrew.queue.presentation.dto.response.CreatePolicyResponse;

public interface QueuePolicyPort {
    CreatePolicyResponse createQueuePolicy(CreatePolicyCommand command, Long userId);

}
