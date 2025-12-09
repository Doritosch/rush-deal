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
public class ProductItemIds {

    private UUID productId;
    private UUID optionId;

    private ProductItemIds(UUID productId, UUID optionId) {
        if (productId == null) {
            throw new BusinessException(TimeDealErrorCode.NOT_FOUND_PRODUCT);
        }
        if (optionId == null) {
            throw new BusinessException(TimeDealErrorCode.NOT_FOUND_OPTION);
        }
        this.productId = productId;
        this.optionId = optionId;
    }

    public static ProductItemIds of(UUID productId, UUID optionId) {
        return new ProductItemIds(productId, optionId);
    }
}
