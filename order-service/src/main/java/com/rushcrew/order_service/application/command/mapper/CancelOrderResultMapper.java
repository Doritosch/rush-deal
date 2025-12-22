package com.rushcrew.order_service.application.command.mapper;

import org.springframework.stereotype.Component;

import com.rushcrew.order_service.application.command.dto.result.CancelOrderResult;
import com.rushcrew.order_service.presentation.dto.response.CancelOrderResponse;

@Component
public class CancelOrderResultMapper {

	public CancelOrderResponse toResponse(CancelOrderResult result) {
		return new CancelOrderResponse(
			result.orderId(),
			result.orderStatus(),
			result.cancelledAt(),
			"주문이 취소되었습니다."
		);
	}
}
