package com.rushcrew.order_service.presentation.dto.response;

import java.time.Instant;
import java.util.UUID;

public record ConfirmPurchaseResponse(
	UUID orderId,
	String orderStatus,
	Instant purchaseConfirmedAt,
	String message
) {}
