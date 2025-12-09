package com.rushcrew.payment_service.presentation.dto.request;

import java.util.UUID;

public record CompletePaymentRequest(
        UUID paymentId,
        String portOnePaymentId
) {
}
