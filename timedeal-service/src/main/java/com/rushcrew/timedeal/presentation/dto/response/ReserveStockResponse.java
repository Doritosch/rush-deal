package com.rushcrew.timedeal.presentation.dto.response;

import com.rushcrew.timedeal.application.result.ReserveStockResult;

public record ReserveStockResponse(
    boolean success,
    Integer availableStock,
    String message
) {

    public static ReserveStockResponse from(ReserveStockResult result) {
        return new ReserveStockResponse(
            result.success(),
            result.availableStock(),
            result.message()
        );
    }
}
