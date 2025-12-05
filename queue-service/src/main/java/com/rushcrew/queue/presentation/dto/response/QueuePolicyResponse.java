package com.rushcrew.queue.presentation.dto.response;

import com.rushcrew.queue.domain.enums.QueuePolicyStatus;
import java.time.LocalDateTime;
import java.util.UUID;

public record QueuePolicyResponse(
    UUID policyId,
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
