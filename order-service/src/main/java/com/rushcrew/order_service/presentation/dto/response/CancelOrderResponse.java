package com.rushcrew.order_service.presentation.dto.response;

import java.time.Instant;
import java.util.UUID;

public record CancelOrderResponse(
	UUID orderId,
	String orderStatus,
	Instant cancelledAt,
	String message
) {}
