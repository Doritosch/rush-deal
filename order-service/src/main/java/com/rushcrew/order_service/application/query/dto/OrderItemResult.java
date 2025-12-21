package com.rushcrew.order_service.application.query.dto;

import java.math.BigDecimal;
import java.util.UUID;

import lombok.Builder;

@Builder
public record OrderItemResult(
	UUID orderItemId,
	UUID timeDealStockId,
	UUID timeDealId,
	UUID productId,
	String productName,
	String optionName,
	Long quantity,
	BigDecimal unitPrice,
	BigDecimal discountPrice,
	BigDecimal subtotal
) {
}
