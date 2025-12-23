package com.rushcrew.payment_service.infrastructure.client.dto;

import java.util.UUID;

public record OrderResponse(
    UUID orderId,
    Long totalAmount,
    String orderStatus
) {
}
