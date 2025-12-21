package com.rushcrew.order_service.application.command.mapper;

import java.util.UUID;

import org.springframework.stereotype.Component;

import com.rushcrew.order_service.application.command.dto.command.CancelOrderCommand;

@Component
public class CancelOrderCommandMapper {
	public CancelOrderCommand toCommand(UUID orderId, Long userId) {
		return CancelOrderCommand.builder()
			.orderId(orderId)
			.userId(userId)
			.build();
	}
}
