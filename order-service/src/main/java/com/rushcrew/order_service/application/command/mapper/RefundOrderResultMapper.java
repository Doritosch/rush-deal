package com.rushcrew.order_service.application.command.mapper;

import org.springframework.stereotype.Component;

import com.rushcrew.order_service.application.command.dto.result.RefundOrderResult;
import com.rushcrew.order_service.presentation.dto.response.RefundOrderResponse;

@Component
public class RefundOrderResultMapper {

	public RefundOrderResponse toResponse(RefundOrderResult result) {
		return new RefundOrderResponse(result.orderId(), result.message());
	}
}
