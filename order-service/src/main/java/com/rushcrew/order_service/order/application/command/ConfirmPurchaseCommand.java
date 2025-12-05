package com.rushcrew.order_service.order.application.command;

import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class ConfirmPurchaseCommand {
	private UUID orderId;
	private Long userId;
}
