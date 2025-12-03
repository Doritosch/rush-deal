package com.rushcrew.queue.application.command;

import com.rushcrew.queue.domain.enums.QueuePolicyStatus;
import java.util.UUID;

public record SearchPolicyCommand(
    UUID productId,
    QueuePolicyStatus status
) {
    public static SearchPolicyCommand of(UUID productId, QueuePolicyStatus status) {
        if (status == null) {
            return new SearchPolicyCommand(productId, null);
        }
        if (productId == null) {
            return new SearchPolicyCommand(null, status);
        }
        return new SearchPolicyCommand(productId, status);
    }
}
