package com.rushcrew.timedeal.domain.vo;

import com.rushcrew.common.exception.BusinessException;
import com.rushcrew.timedeal.domain.entity.TimeDealStock;
import com.rushcrew.timedeal.domain.exception.TimeDealErrorCode;

public enum EventType {
    RESERVE("재고 예약") {
        @Override
        public void applyRestore(
            TimeDealStock stock,
            OrderId orderId,
            Quantity quantity,
            String reason
        ) {
            stock.restoreFromReserved(orderId, quantity, reason);
        }
    },
    SELL("판매 확정") {
        @Override
        public void applyRestore(
            TimeDealStock stock,
            OrderId orderId,
            Quantity quantity,
            String reason
        ) {
            stock.restoreFromSold(orderId, quantity, reason);
        }
    },
    INIT("초기 재고 설정"),
    RESERVE_CANCEL("예약 취소"),
    PAYMENT_CANCEL("결제 취소"),
    ROLLBACK("트랜잭션 롤백"),
    ADMIN_CONTROL("관리자 수동 조정");

    private final String description;

    EventType(String description) {
        this.description = description;
    }

    public void applyRestore(
        TimeDealStock stock,
        OrderId orderId,
        Quantity quantity,
        String reason
    ) {
        throw new BusinessException(TimeDealErrorCode.INVALID_ORDER_STATE);
    }
}
