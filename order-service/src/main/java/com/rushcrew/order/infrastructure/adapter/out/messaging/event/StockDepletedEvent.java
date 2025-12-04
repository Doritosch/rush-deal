package com.rushcrew.order.infrastructure.adapter.out.messaging.event;

import java.time.Instant;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StockDepletedEvent {
	private String timeDealId;           // 어떤 타임딜이
	private Long userId;                 // 누가 주문 시도했는데
	private Integer availableStock;      // 남은 재고가 얼마인지
	private Instant timestamp;
}
