package com.rushcrew.order_service.infrastructure.dto.payment;

import java.math.BigDecimal;
import java.util.UUID;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class PaymentRequest {
	private UUID orderId;
	private Long userId;
	private BigDecimal amount;
}
