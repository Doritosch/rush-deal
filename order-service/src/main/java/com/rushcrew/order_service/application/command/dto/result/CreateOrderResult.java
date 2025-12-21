package com.rushcrew.order_service.application.command.dto.result;

import java.util.UUID;

// Saga 접수 응답
public record CreateOrderResult(
	UUID sagaId,
	String status   // PROCESSING
) {
	public static CreateOrderResult accepted(UUID sagaId) {
		return new CreateOrderResult(sagaId, "PROCESSING");
	}
}
