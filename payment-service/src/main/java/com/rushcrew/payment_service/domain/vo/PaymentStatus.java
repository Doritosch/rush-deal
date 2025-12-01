package com.rushcrew.payment_service.domain.vo;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum PaymentStatus {

    READY("결제요청"), IN_PROGRESS("결제승인"), DONE("결제확정"), EXPIRED("결제만료"),
    CANCELLED("결제취소"), ABORTED("결제실");

    private final String description;
}
