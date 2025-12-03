package com.rushcrew.order_service.order.application.command;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class ConfirmPurchaseResult {
	private UUID orderId;
	private String orderStatus;
	private BigDecimal earnedPoints;	// 적립된 포인트
	private Instant confirmedAt;
	private String message;
}
