package com.rushcrew.order_service.application.command.mapper;

import java.util.UUID;

import org.springframework.stereotype.Component;

import com.rushcrew.order_service.application.command.dto.command.RequestPaymentCommand;

@Component
public class RequestPaymentCommandMapper {

	public RequestPaymentCommand toCommand(
		UUID orderId,
		Long userId
	) {
		return RequestPaymentCommand.builder()
			.orderId(orderId)
			.userId(userId)
			.build();
	}
}
