package com.rushcrew.order_service.order.infrastructure.dto.timedeal;

import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StockStatusResponse {
	private UUID timeDealStockId;
	private UUID productId;
	private UUID optionId;
	private Integer availableStock;
	private Integer reservedStock;
	private Integer soldStock;
	private String status; // AVAILABLE, SOLD_OUT
}
