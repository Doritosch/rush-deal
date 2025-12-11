package com.rushcrew.order_service.presentation.dto.response;

import java.time.Instant;
import java.util.UUID;

import com.rushcrew.order_service.application.command.dto.result.ConfirmPurchaseResult;

public record ConfirmPurchaseResponse(
	UUID orderId,
	String orderStatus,
	Instant purchaseConfirmedAt,
	String message
) {

	public static ConfirmPurchaseResponse from(ConfirmPurchaseResult result) {
		return new ConfirmPurchaseResponse(
			result.orderId(),
			result.orderStatus(),
			result.purchaseConfirmedAt(),
			"구매가 확정되었습니다."
		);
	}
}
