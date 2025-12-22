package com.rushcrew.product.presentation.internal.dto;

import java.util.List;
import java.util.UUID;

public record ProductInfoResponse(
    UUID productId,
    List<UUID> optionIds,
    Long sellerId,
    Long price
) {

    public static ProductInfoResponse of(
        UUID productId,
        List<UUID> optionIds,
        Long sellerId,
        Long price
    ) {
        return new ProductInfoResponse(productId, optionIds, sellerId, price);
    }
}
