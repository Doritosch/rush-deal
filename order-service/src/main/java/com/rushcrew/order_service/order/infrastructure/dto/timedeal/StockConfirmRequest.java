package com.rushcrew.order_service.order.infrastructure.dto.timedeal;

import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StockConfirmRequest {
	@NonNull private String timeDealStockId;
	@NonNull private Integer quantity;
	@NonNull private String orderId;
}
