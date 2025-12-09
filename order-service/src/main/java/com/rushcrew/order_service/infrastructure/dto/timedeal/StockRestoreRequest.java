package com.rushcrew.order_service.infrastructure.dto.timedeal;

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
	@NonNull private String sagaId;
	@NonNull private String reason;
}
