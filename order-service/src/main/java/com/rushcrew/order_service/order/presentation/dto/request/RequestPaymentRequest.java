package com.rushcrew.order_service.order.presentation.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RequestPaymentRequest {
	@NotBlank(message = "결제 수단은 필수입니다")
	private String paymentMethod; // CARD, BILLING
}
