package com.rushcrew.order_service.application.command.dto.result;

import java.time.Instant;
import java.util.UUID;

import lombok.Builder;

@Builder
public record ConfirmPurchaseResult(
	UUID orderId,
	String orderStatus,
	Instant purchaseConfirmedAt
) {}
