package com.rushcrew.order_service.application.command.dto.command;

import java.util.UUID;

import lombok.Builder;

@Builder
public record RequestPaymentCommand(
	UUID orderId,
	Long userId
) {}

