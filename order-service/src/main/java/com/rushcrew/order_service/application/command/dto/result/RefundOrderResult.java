package com.rushcrew.order_service.application.command.dto.result;

import java.util.UUID;

import lombok.Builder;

@Builder
public record RefundOrderResult(
	UUID orderId,
	String message
) {}
