package com.rushcrew.payment_service.domain.vo;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum PaymentType {

    NORMAL("일반결제"), BILLING("자동결제"), BRANDPAY("브랜드페이");

    private final String description;

}
