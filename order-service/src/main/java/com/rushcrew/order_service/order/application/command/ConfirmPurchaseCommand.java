package com.rushcrew.order_service.order.application.command;

import java.util.UUID;

public record ConfirmPurchaseCommand(
	UUID orderId,
	Long userId
) { }
