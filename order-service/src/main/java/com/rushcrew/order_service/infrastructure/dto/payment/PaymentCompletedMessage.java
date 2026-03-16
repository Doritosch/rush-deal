package com.rushcrew.order_service.infrastructure.dto.payment;

import java.time.LocalDateTime;
import java.util.UUID;

public record PaymentCompletedMessage(
        UUID paymentId,
        UUID orderId,
        Long totalAmount,
        String currency,
        LocalDateTime completedAt,
        String status
) {
}
