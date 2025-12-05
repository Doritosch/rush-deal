package com.rushcrew.order_service.order.application.port.dto;

import java.math.BigDecimal;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class TimeDealInfo {
	private String timeDealId;
	private String title;
	private TimeDealStatus status;
	private BigDecimal discountPrice;
	private BigDecimal discountRate;
	private Integer limitQuantity;
}
