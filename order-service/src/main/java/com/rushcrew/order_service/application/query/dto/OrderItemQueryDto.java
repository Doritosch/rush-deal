package com.rushcrew.order_service.application.query.dto;

import java.math.BigDecimal;
import java.util.UUID;

import lombok.Builder;

@Builder
public record OrderItemQueryDto(
	UUID orderItemId,
	String productName,
	String optionName,
	Long quantity,
	BigDecimal unitPrice,
	BigDecimal discountPrice,
	BigDecimal subtotal
) {}
