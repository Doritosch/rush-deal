package com.rushcrew.queue.application.command;

import com.rushcrew.queue.domain.enums.QueuePolicyStatus;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.Builder;

@Builder
public record UpdatePolicyCommand(
    UUID productId,
    String timeDealName,
    QueuePolicyStatus status,
    LocalDateTime startTime,
    LocalDateTime endTime,
    Integer limitSize,
    Integer queueGap,
    Integer ttl
) {

}
