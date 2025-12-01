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
public class ProductResponseDto {
	private UUID timeDealStockId;
	private String productId;
	private String timeDealId;
	private Integer availableStock;
	private Integer reservedStock;
	private Integer soldStock;
}
