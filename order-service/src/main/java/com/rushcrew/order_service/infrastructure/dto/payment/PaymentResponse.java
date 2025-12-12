package com.rushcrew.order_service.infrastructure.dto.payment;

import java.util.UUID;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class PaymentResponse {
	private UUID paymentId;
	private UUID orderId;
	private boolean success;
	private String message;
}
