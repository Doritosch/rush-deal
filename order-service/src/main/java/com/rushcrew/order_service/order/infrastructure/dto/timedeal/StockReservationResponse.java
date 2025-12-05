package com.rushcrew.order_service.order.infrastructure.dto.timedeal;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class StockReservationResponse {
	private boolean success;
	private Integer availableStock;
	private String message;
}
