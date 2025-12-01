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
public class TimeDealProductResponse {
	private UUID timeDealStockId;
	private UUID productId;           // 상품 ID (주문 생성에 필요)
	private UUID optionId;            // 옵션 ID (nullable)
	private String timeDealId;
	private Integer availableStock;
	private Integer reservedStock;
	private Integer soldStock;
	private String status;            // AVAILABLE, SOLD_OUT, RESERVED, PAUSED
}
