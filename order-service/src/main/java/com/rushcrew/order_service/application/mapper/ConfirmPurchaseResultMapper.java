package com.rushcrew.order_service.application.mapper;

import org.springframework.stereotype.Component;

import com.rushcrew.order_service.application.command.dto.result.ConfirmPurchaseResult;
import com.rushcrew.order_service.presentation.dto.response.ConfirmPurchaseResponse;

@Component
public class ConfirmPurchaseResultMapper {

	public ConfirmPurchaseResponse toResponse(ConfirmPurchaseResult result) {
		return new ConfirmPurchaseResponse(
			result.orderId(),
			result.orderStatus(),
			result.purchaseConfirmedAt(),
			"구매가 확정되었습니다."
		);
	}
}
