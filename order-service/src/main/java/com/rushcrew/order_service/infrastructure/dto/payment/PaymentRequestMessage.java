package com.rushcrew.order_service.infrastructure.dto.payment;

import java.util.UUID;

public record PaymentRequestMessage(
        UUID orderId,
        Long userId,
        Long finalAmount
) {
}
