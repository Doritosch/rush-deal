package com.rushcrew.order_service.infrastructure.dto.timedeal;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class TimeDealStockResponse {

	@NonNull private String timeDealStockId;
	@NonNull private String productId;
	@NonNull private Long availableStock;
	@NonNull private Long reservedStock;
	@NonNull private Long soldStock;
	@NonNull private String status;
}
