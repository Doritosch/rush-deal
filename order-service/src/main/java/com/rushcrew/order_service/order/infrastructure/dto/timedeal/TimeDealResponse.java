package com.rushcrew.order_service.order.infrastructure.dto.timedeal;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class TimeDealResponse {
	private String timeDealId;
	private String title;
	private TimeDealResponseStatus status;
	private BigDecimal discountPrice;
	private BigDecimal discountRate;
	private Integer limitQuantity;
}
