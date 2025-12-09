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
public class LimitQuantity {

    private Long quantity;

    private LimitQuantity(Long quantity) {
        if (isUnlimited(quantity)) return;

        if (quantity <= 0) {
            throw new BusinessException(TimeDealErrorCode.INVALID_LIMIT_QUANTITY);
        }
        this.quantity = quantity;
    }

    public static LimitQuantity of(Long quantity) {
        return new LimitQuantity(quantity);
    }

    private boolean isUnlimited(Long quantity) {
        return quantity == null;
    }
}
