package com.rushcrew.timedeal.domain.vo;

public enum EventType {
    INIT("초기 재고 설정"),
    RESERVE("재고 예약"),
    RESERVE_CANCEL("예약 취소"), // 재고 복구
    PAYMENT_CANCEL("결제 취소"), // 재고 복구
    SELL("판매 확정"), // 재고 차감
    ROLLBACK("트랜잭션 롤백"),
    ADMIN_CONTROL("관리자 수동 조정"); // 임의로 재고 증가/감소

    private final String description;

    EventType(String description) {
        this.description = description;
    }
}
