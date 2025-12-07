package com.rushcrew.order_service.infrastructure.dto.timedeal;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class StockReservationResponse {
	@NonNull private boolean success;
	@NonNull private Integer availableStock;
	@NonNull private String message;
}
