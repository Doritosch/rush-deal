package com.rushcrew.timedeal.presentation.dto.response;

import com.rushcrew.timedeal.application.result.UpdateStockCountResult;
import java.util.UUID;

public record UpdateStockCountResponse(
    UUID stockId,
    UUID productId,
    Long currentStock,
    Long changedQuantity
) {

    public static UpdateStockCountResponse from(UpdateStockCountResult result) {
        return new UpdateStockCountResponse(
            result.stockId(),
            result.productId(),
            result.currentStock(),
            result.changedQuantity()
        );

    }
}
