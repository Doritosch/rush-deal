package com.rushcrew.timedeal.domain.vo;

import com.rushcrew.common.exception.BusinessException;
import com.rushcrew.timedeal.domain.exception.TimeDealErrorCode;
import jakarta.persistence.Embeddable;
import java.util.UUID;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Embeddable
@Getter
@EqualsAndHashCode
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class OrderId {

    private UUID orderId;

    private OrderId(UUID orderId) {
        if (orderId == null) {
            throw new BusinessException(TimeDealErrorCode.NOT_FOUND_ORDER);
        }
        this.orderId = orderId;
    }

    public static OrderId of(UUID orderId) {
        return new OrderId(orderId);
    }
}
