package com.rushcrew.queue.application.command;

import com.rushcrew.queue.domain.enums.QueuePolicyStatus;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.Builder;

@Builder
public record CreatePolicyCommand(
    UUID productId,
    String dealName,
    QueuePolicyStatus status,
    LocalDateTime startTime,
    LocalDateTime endTime,
    Integer limitSize,
    Integer queueGap,
    Integer ttl
) {

}
