package com.rushcrew.order_service.order.application.port.dto;

import java.math.BigDecimal;
import java.util.UUID;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class TimeDealInfo {
	private UUID timeDealId;
	private String title;
	private TimeDealStatus status;
	private BigDecimal discountPrice;
	private BigDecimal discountRate;
	private Integer limitQuantity;
}
