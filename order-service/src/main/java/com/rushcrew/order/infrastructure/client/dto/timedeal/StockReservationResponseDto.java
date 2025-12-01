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
public class StockReservationResponseDto {
	private boolean success;
	private Integer availableStock;
	private String message;
	private StockReservationResultDto result;

	public static StockReservationResponseDto success(UUID timeDealStockId, Integer quantity) {
		return StockReservationResponseDto.builder()
			.success(true)
			.result(new StockReservationResultDto(timeDealStockId, quantity))
			.build();
	}

	public static StockReservationResponseDto failure(Integer availableStock, String message) {
		return StockReservationResponseDto.builder()
			.success(false)
			.availableStock(availableStock)
			.message(message)
			.build();
	}
}
