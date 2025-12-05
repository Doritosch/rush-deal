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
public class StockReservationRequest {
	private UUID timeDealStockId;
	private Integer quantity;
	private Long userId;
}
