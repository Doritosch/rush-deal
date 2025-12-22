package com.rushcrew.payment_service.application.command;

import java.util.UUID;

public record RefundCommand(
        UUID paymentId,
        Long amount,
        String cancelReason
) {
}
