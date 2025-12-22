package com.rushcrew.timedeal.presentation.dto.response;

import java.math.BigDecimal;
import java.util.UUID;

import com.rushcrew.timedeal.application.result.TimeDealForOrderResult;
import com.rushcrew.timedeal.domain.vo.TimeDealStatus;

public record TimeDealForOrderResponse(
	UUID timeDealId,
	String title,
	TimeDealStatus status,
	BigDecimal discountPrice,
	Long limitQuantity
) {
	public static TimeDealForOrderResponse from(TimeDealForOrderResult result) {
		return new TimeDealForOrderResponse(
			result.getTimeDealId(),
			result.getTitle(),
			result.getStatus(),
			result.getDiscountPrice(),
			result.getLimitQuantity()
		);
	}
}
