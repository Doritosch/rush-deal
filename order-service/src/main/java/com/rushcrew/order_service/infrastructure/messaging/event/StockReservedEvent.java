package com.rushcrew.order_service.infrastructure.messaging.event;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

public record StockReservedEvent(
	String sagaId,
	String timeDealId,
	List<ReservedStockItem> reservedItems,
	Instant occurredAt
) {

	public static StockReservedEvent of(
		String sagaId,
		String timeDealId,
		List<ReservedStockItem> reservedItems
	) {
		return new StockReservedEvent(
			sagaId,
			timeDealId,
			reservedItems,
			Instant.now()
		);
	}

	public record ReservedStockItem(
		String timeDealStockId,
		Long quantity,
		BigDecimal discountedPrice
	) {}
}
