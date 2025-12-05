package com.rushcrew.order_service.order.application.command;

import java.util.UUID;

public record RequestPaymentCommand (
	UUID orderId,
	Long userId,
	String paymentMethod	// CARD, BILLING
) {}
