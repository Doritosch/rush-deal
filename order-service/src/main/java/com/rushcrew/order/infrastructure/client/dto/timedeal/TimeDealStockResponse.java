package com.rushcrew.order.infrastructure.client.dto.timedeal;

import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TimeDealStockResponse {
	private UUID timeDealStockId;
	private String timeDealId;
	private String productId;
	private String optionId;
	private Integer availableStock;     // 주문 가능한 재고
	private Integer reservedStock;      // 예약된 재고
	private Integer soldStock;          // 판매 완료된 재고
	private String status;
}
