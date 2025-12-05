package com.rushcrew.order_service.order.presentation.dto.response;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

import com.rushcrew.order_service.order.application.command.RequestPaymentResult;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class RequestPaymentResponse {
	private UUID orderId;
	private String orderStatus;
	private BigDecimal finalAmount;
	private BigDecimal pointUsed;
	private String paymentStatus;
	private String sagaId;
	private String message;
	private Instant requestedAt;

	public static RequestPaymentResponse from(RequestPaymentResult result) {
		String message = "PENDING".equals(result.getPaymentStatus())
			? "포인트 차감 처리 중입니다..."
			: "결제 처리 중입니다...";

		return RequestPaymentResponse.builder()
			.orderId(result.getOrderId())
			.orderStatus(result.getOrderStatus())
			.finalAmount(result.getFinalAmount())
			.pointUsed(result.getPointUsed())
			.paymentStatus(result.getPaymentStatus())
			.sagaId(result.getSagaId())
			.message(message)
			.requestedAt(result.getRequestedAt())
			.build();
	}
}
