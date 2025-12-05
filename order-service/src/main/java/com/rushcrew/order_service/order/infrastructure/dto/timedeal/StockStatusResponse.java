package com.rushcrew.order_service.order.infrastructure.dto.timedeal;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StockStatusResponse {
	@NonNull private String timeDealStockId;
	@NonNull private String productId;
	@NonNull private String optionId;
	@NonNull private Integer availableStock;
	@NonNull private Integer reservedStock;
	@NonNull private Integer soldStock;
	@NonNull private String status; // AVAILABLE, SOLD_OUT
}
