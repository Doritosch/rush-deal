package com.rushcrew.payment_service.infrastructure.event;

import java.util.UUID;

public record PaymentRequestMessage(
        UUID orderId,
        Long userId,
        Long finalAmount
) {
}
