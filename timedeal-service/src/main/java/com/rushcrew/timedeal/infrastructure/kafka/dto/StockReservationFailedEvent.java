package com.rushcrew.timedeal.infrastructure.kafka.dto;

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
