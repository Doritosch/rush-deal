package com.rushcrew.order_service.order.application.command;

import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class RequestPaymentCommand {
	private UUID orderId;
	private Long userId;
	private String paymentMethod; // CARD, BILLING
}
