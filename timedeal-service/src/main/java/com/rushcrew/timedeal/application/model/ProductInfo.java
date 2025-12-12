package com.rushcrew.timedeal.application.model;

import com.rushcrew.timedeal.domain.vo.Price;
import java.util.List;
import java.util.UUID;

public record ProductInfo(
    List<UUID> optionIds,
    Long sellerId,
    Price price
) {

    public static ProductInfo of(List<UUID> optionIds, Long sellerId, Long price) {
        return new ProductInfo(
            optionIds,
            sellerId,
            Price.of(price)
        );
    }
}
