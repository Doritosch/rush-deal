package com.rushcrew.timedeal.application.result;

import java.math.BigDecimal;
import java.util.UUID;

import com.rushcrew.timedeal.domain.vo.TimeDealStatus;

import lombok.Getter;

@Getter
public class TimeDealForOrderResult {

	private final UUID timeDealId;
	private final String title;
	private final TimeDealStatus status;
	private final BigDecimal discountPrice;
	private final Long limitQuantity;

	public TimeDealForOrderResult(
		UUID timeDealId,
		String title,
		TimeDealStatus status,
		BigDecimal discountPrice,
		Long limitQuantity
	) {
		this.timeDealId = timeDealId;
		this.title = title;
		this.status = status;
		this.discountPrice = discountPrice;
		this.limitQuantity = limitQuantity;
	}
}
