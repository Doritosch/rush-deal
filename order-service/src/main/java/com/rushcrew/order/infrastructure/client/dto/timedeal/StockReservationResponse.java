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
public class StockReservationResponse {
	private boolean success;
	private Integer availableStock;
	private String message;
	private StockReservationResult result;

	public static StockReservationResponse success(UUID timeDealStockId, Integer quantity) {
		return StockReservationResponse.builder()
			.success(true)
			.result(new StockReservationResult(timeDealStockId, quantity))
			.build();
	}

	public static StockReservationResponse failure(Integer availableStock, String message) {
		return StockReservationResponse.builder()
			.success(false)
			.availableStock(availableStock)
			.message(message)
			.build();
	}
}
