package com.rushcrew.queue.domain.dto;

import com.rushcrew.queue.domain.enums.QueuePolicyStatus;
import java.util.UUID;

public record SearchPolicyCondition(
    UUID productId,
    QueuePolicyStatus status
) {

}
