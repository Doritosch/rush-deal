package com.rushcrew.timedeal.infrastructure.external.dto.request;

import java.util.UUID;

public record ProductItemIdsRequest(
    UUID productId
) {

    public static ProductItemIdsRequest of(UUID productId) {
        return new ProductItemIdsRequest(productId);
    }
}
