package com.rushcrew.order_service.presentation.dto.response;

import java.util.UUID;

public record RefundOrderResponse(
	UUID orderId,
	String message
) {}
