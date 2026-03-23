package com.rushcrew.payment_service.infrastructure.event;

import java.time.LocalDateTime;
import java.util.UUID;

public record PaymentResultMessage(
        UUID paymentId,
        UUID orderId,
        Long totalAmount,
        String currency,
        LocalDateTime completedAt,
        String status,
        PaymentMessageStatus messageStatus
) {
}
