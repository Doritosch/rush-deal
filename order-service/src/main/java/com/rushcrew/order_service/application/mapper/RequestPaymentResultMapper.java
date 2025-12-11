package com.rushcrew.order_service.application.mapper;

import org.springframework.stereotype.Component;

import com.rushcrew.order_service.application.command.dto.result.RequestPaymentResult;
import com.rushcrew.order_service.presentation.dto.response.RequestPaymentResponse;

@Component
public class RequestPaymentResultMapper {

	public RequestPaymentResponse toResponse(RequestPaymentResult result) {
		return RequestPaymentResponse.builder()
			.orderId(result.orderId())
			.orderStatus(result.orderStatus())
			.paymentAmount(result.paymentAmount())
			.paymentCompletedAt(result.paymentCompletedAt())
			.autoConfirmScheduledAt(result.autoConfirmScheduledAt())
			.message("결제가 완료되었습니다. 7일 후 자동으로 구매 확정됩니다.")
			.build();
	}
}
