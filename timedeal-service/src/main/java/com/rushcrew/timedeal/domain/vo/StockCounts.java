package com.rushcrew.timedeal.domain.vo;

import com.rushcrew.common.exception.BusinessException;
import com.rushcrew.timedeal.domain.exception.TimeDealErrorCode;
import jakarta.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Embeddable
@Getter
@EqualsAndHashCode
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class StockCounts {

    private Long available; // 주문 가능한 재고 수량
    private Long reserved; // 예약된 재고 수량
    private Long sold; // 판매 완료된 재고 수량

    private StockCounts(Long available, Long reserved, Long sold) {
        validateNotNull(available, reserved, sold);
        validateNonNegative(available, reserved, sold);

        this.available = available;
        this.reserved = reserved;
        this.sold = sold;
    }

    public static StockCounts of(Long available, Long reserved, Long sold) {
        return new StockCounts(available, reserved, sold);
    }

    public static StockCounts init(Long available) {
        return new StockCounts(available, 0L, 0L);
    }

    public StockCounts reserve(Quantity quantity) {
        return StockCounts.of(
            this.available - quantity.getQuantity(),
            this.reserved + quantity.getQuantity(),
            this.getSold()
        );
    }

    public StockCounts confirm(Quantity quantity) {
        return StockCounts.of(
            this.getAvailable(),
            this.reserved - quantity.getQuantity(),
            this.sold + quantity.getQuantity()
        );
    }

    private void validateNotNull(Long available, Long reserved, Long sold) {
        if (available == null || reserved == null || sold == null) {
            throw new BusinessException(TimeDealErrorCode.REQUIRED_STOCK_COUNT);
        }
    }

    private void validateNonNegative(Long available, Long reserved, Long sold) {
        if (available < 0 || reserved < 0 || sold < 0) {
            throw new BusinessException(TimeDealErrorCode.INVALID_STOCK_COUNT);
        }
    }
}
