package com.rushcrew.payment_service.presentation.dto.request;

import java.util.UUID;

public record CompletePaymentRequest(
        String portOnePaymentId
) {
}
