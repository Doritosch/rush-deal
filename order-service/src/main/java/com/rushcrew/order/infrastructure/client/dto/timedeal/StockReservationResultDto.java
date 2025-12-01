package com.rushcrew.order.infrastructure.client.dto.timedeal;

import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class StockReservationResultDto {
	private UUID timeDealStockId;
	private Integer quantity;
}
