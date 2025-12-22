package com.rushcrew.timedeal.application.result;

import java.math.BigDecimal;
import java.util.UUID;

import com.rushcrew.timedeal.domain.vo.TimeDealStatus;

/**
 * Native Query 전용 Projection
 */
public interface TimeDealForOrderView {

	UUID getTimeDealId();

	String getTitle();

	TimeDealStatus getStatus();

	BigDecimal getDiscountPrice();

	Long getLimitQuantity();
}
