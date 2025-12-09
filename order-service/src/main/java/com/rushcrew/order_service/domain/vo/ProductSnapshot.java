package com.rushcrew.order_service.domain.vo;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.UUID;

import lombok.Builder;

@Builder
public record ProductSnapshot(
	UUID timeDealStockId,
	UUID productId,
	String productName,
	String productDescription,
	UUID optionId,
	String optionName,
	UUID sellerId,
	String sellerName,
	BigDecimal originalPrice,
	UUID timeDealId,
	String timeDealTitle,
	Integer discountRate,
	String category
) implements Serializable {}
