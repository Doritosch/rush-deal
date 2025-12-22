package com.rushcrew.payment_service.presentation.dto.request;

import jakarta.validation.constraints.NotBlank;

public record CancelPaymentRequest(
        @NotBlank(message = "취소 사유는 필수입니다.")
        String cancelReason
) {
}
