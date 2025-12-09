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
public class Quantity {

    private Long quantity;

    private Quantity(Long quantity) {
        if (quantity <= 0) {
            throw new BusinessException(TimeDealErrorCode.INVALID_QUANTITY_RANGE);
        }
        this.quantity = quantity;
    }

    public static Quantity of(Long quantity) {
        return new Quantity(quantity);
    }
}
