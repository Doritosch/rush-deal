package com.rushcrew.payment_service.infrastructure.event;

import com.rushcrew.payment_service.domain.vo.PaymentStatus;

import java.time.LocalDateTime;
import java.util.UUID;

public record PaymentRequestMessage(
        UUID paymentId,
        UUID orderId,
        Long totalAmount,
        String currency,
        LocalDateTime completedAt,
        String status,
        PaymentMessageStatus messageStatus
) {
}
