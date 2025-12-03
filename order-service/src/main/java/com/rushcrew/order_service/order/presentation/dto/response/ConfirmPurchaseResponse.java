package com.rushcrew.order_service.order.presentation.dto.response;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

import com.rushcrew.order_service.order.application.command.ConfirmPurchaseResult;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class ConfirmPurchaseResponse {
	private UUID orderId;
	private String orderStatus;
	private BigDecimal earnedPoints;
	private Instant confirmedAt;
	private String message;

	public static ConfirmPurchaseResponse from(ConfirmPurchaseResult result) {
		return ConfirmPurchaseResponse.builder()
			.orderId(result.getOrderId())
			.orderStatus(result.getOrderStatus())
			.earnedPoints(result.getEarnedPoints())
			.confirmedAt(result.getConfirmedAt())
			.message(result.getMessage())
			.build();
	}
}
