package com.rushcrew.timedeal.application.result;

import java.util.UUID;

public record UpdateStockCountResult(
    UUID stockId,
    UUID productId,
    Long previousStock,
    Long currentStock,
    Long changedQuantity
) {

    public static UpdateStockCountResult of(
        UUID stockId,
        UUID productId,
        Long previousStock,
        Long currentStock,
        Long changedQuantity
    ) {
        return new UpdateStockCountResult(
            stockId,
            productId,
            previousStock,
            currentStock,
            changedQuantity
        );
    }
}
