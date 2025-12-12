package com.rushcrew.order_service.presentation.dto.response;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

import com.rushcrew.order_service.application.command.dto.result.RequestPaymentResult;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class RequestPaymentResponse {
	private UUID orderId;
	private String orderStatus;
	private BigDecimal paymentAmount;
	private Instant paymentCompletedAt;
	private Instant autoConfirmScheduledAt;
	private String message;

	public static RequestPaymentResponse from(RequestPaymentResult result) {
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
