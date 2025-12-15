package com.rushcrew.timedeal.application.result;

import java.util.UUID;

public record ConfirmStockResult(
    UUID orderId
) {

    public static ConfirmStockResult of(UUID orderId) {
        return new ConfirmStockResult(orderId);
    }
}
