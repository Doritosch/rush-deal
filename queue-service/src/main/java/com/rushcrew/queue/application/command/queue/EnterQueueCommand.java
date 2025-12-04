package com.rushcrew.queue.application.command.queue;

import java.util.UUID;

public record EnterQueueCommand(
    Long userId,
    UUID productId,
    String role
) {
    public static EnterQueueCommand of(UUID productId, Long userId, String role) {
        return new EnterQueueCommand(userId, productId, role);
    }
}
