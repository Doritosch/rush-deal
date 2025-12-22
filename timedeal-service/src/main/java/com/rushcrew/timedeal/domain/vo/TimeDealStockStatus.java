package com.rushcrew.timedeal.domain.vo;

public enum TimeDealStockStatus {
    AVAILABLE("구매가능"),
    RESERVED("예약됨"),
    SOLD("결제완료"),
    PAUSED("중지됨");

    private final String description;

    TimeDealStockStatus(String description) {
        this.description = description;
    }
}
