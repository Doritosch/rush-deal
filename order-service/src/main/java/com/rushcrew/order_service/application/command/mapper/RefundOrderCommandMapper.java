package com.rushcrew.order_service.application.command.mapper;

import java.util.UUID;

import org.springframework.stereotype.Component;

import com.rushcrew.order_service.application.command.dto.command.RefundOrderCommand;

@Component
public class RefundOrderCommandMapper {

	public RefundOrderCommand toCommand(UUID orderId, Long userId, String reason) {
		return RefundOrderCommand.builder()
			.orderId(orderId)
			.userId(userId)
			.reason(reason)
			.build();
	}
}
