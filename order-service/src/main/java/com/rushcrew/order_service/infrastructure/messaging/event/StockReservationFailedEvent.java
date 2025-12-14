package com.rushcrew.order_service.infrastructure.messaging.event;

import java.time.Instant;

public record StockReservationFailedEvent(
	String sagaId,
	String timeDealId,
	String reason,
	Instant occurredAt
) {

	public static StockReservationFailedEvent of(
		String sagaId,
		String timeDealId,
		String reason
	) {
		return new StockReservationFailedEvent(
			sagaId,
			timeDealId,
			reason,
			Instant.now()
		);
	}
}
