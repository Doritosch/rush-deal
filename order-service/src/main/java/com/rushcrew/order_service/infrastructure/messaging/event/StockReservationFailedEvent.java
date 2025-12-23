package com.rushcrew.order_service.infrastructure.messaging.event;

import java.time.Instant;

public record StockReservationFailedEvent(
	String sagaId,
	String productId,
	String reason,
	Instant occurredAt
) {

	public static StockReservationFailedEvent of(
		String sagaId,
		String productId,
		String reason
	) {
		return new StockReservationFailedEvent(
			sagaId,
			productId,
			reason,
			Instant.now()
		);
	}
}
