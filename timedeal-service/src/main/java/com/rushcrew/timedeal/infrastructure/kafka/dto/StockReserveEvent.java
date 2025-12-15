package com.rushcrew.timedeal.infrastructure.kafka.dto;

import java.util.List;

public record StockReserveEvent(
	String sagaId,
	Long userId,
	String timeDealId,
	String productId,
	List<OrderItem> orderItems
) {
	public record OrderItem(
		String timeDealStockId,
		Long quantity  // int가 아니라 Integer (JSON 파싱을 위해)
	) {}
}
