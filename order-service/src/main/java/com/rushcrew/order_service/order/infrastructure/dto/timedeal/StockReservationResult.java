package com.rushcrew.order_service.order.infrastructure.dto.timedeal;

import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class StockReservationResult {
	private UUID timeDealStockId;
	private Integer quantity;
}
