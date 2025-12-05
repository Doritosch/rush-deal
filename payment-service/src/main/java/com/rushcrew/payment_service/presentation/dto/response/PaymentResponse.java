package com.rushcrew.payment_service.presentation.dto.response;

import com.rushcrew.payment_service.application.result.PaymentResult;
import com.rushcrew.payment_service.domain.vo.PaymentStatus;

import java.math.BigDecimal;
import java.util.UUID;

public record PaymentResponse(
        UUID paymentId,
        UUID orderId,
        BigDecimal totalAmount,
        PaymentStatus status
) {
    public static PaymentResponse from(PaymentResult result) {
        return new PaymentResponse(
                result.paymentId(),
                result.orderId(),
                result.totalAmount(),
                result.status()
        );
    }
}
