package com.rushcrew.timedeal.application.result;

public record ReserveStockResult(
    Boolean success,
    Integer availableStock,
    String message
) {

    public static ReserveStockResult of(Long available, String message) {
        return new ReserveStockResult(true, available.intValue(), message);
    }
}
