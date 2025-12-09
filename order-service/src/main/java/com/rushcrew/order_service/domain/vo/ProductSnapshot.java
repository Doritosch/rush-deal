package com.rushcrew.order_service.domain.vo;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.UUID;

import lombok.Builder;

@Builder
public record ProductSnapshot(
	String timeDealStockId,
	String productId,
	String productName,
	String productDescription,
	String optionId,
	String optionName,
	String sellerId,
	String sellerName,
	BigDecimal originalPrice,
	String timeDealId,
	String timeDealTitle,
	Integer discountRate,
	String category
) implements Serializable {}
