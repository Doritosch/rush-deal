package com.rushcrew.payment_service.domain.vo;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum PaymentStatus {

    PENDING("결제요청"), PAID("결제완료"), CANCELLED("결제취소");

    private final String description;

    public boolean isPending() {
        if (!description.equals("결제요청")) {
            return false;
        }
        return true;
    }

    public boolean isPaid() {
        if (!description.equals("결제완료")) {
            return false;
        }
        return true;
    }
}
