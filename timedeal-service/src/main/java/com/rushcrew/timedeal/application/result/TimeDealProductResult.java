package com.rushcrew.timedeal.application.result;

import com.rushcrew.timedeal.domain.entity.TimeDealProduct;
import com.rushcrew.timedeal.domain.vo.TimeDealProductStatus;
import java.util.UUID;

public record TimeDealProductResult(
    UUID id,
    UUID productId,
    UUID productOptionId,
    TimeDealProductStatus timeDealProductStatus
) {

    public static TimeDealProductResult from(TimeDealProduct timeDealProduct) {
        return new TimeDealProductResult(
            timeDealProduct.getId(),
            timeDealProduct.getItemIds().getProductId(),
            timeDealProduct.getItemIds().getOptionId(),
            timeDealProduct.getStatus()
        );
    }
}
