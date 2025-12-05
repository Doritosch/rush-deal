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
public class StockRestoreRequest {
	@NonNull private String timeDealStockId;
	@NonNull private Integer quantity;
	@NonNull private String orderId;
	@NonNull private String reason;
}
