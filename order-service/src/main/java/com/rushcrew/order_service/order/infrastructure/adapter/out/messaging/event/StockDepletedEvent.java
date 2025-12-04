package com.rushcrew.order_service.order.infrastructure.adapter.out.messaging.event;

import java.time.Instant;

public record StockDepletedEvent(
	String timeDealId,        // 어떤 타임딜이
	Long userId,              // 누가 주문 시도했는데
	Integer availableStock,   // 남은 재고가 얼마인지
	Instant timestamp
) {}
