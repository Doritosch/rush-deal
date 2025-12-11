package com.rushcrew.order_service.application.mapper;

import java.util.UUID;

import org.springframework.stereotype.Component;

import com.rushcrew.order_service.application.command.dto.command.ConfirmPurchaseCommand;

@Component
public class ConfirmPurchaseCommandMapper {

	public ConfirmPurchaseCommand toCommand(UUID orderId, Long userId) {
		return ConfirmPurchaseCommand.builder()
			.orderId(orderId)
			.userId(userId)
			.build();
	}
}
