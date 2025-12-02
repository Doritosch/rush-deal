package com.rushcrew.order.infrastructure.client.dto.timedeal;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TimeDealResponse {
	private String timeDealId;
	private String title;
	private TimeDealStatus status;
	private BigDecimal discountPrice;
	private BigDecimal discountRate;
	private Integer limitQuantity;
}
