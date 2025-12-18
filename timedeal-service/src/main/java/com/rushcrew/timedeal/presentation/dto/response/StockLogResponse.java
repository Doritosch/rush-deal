package com.rushcrew.timedeal.presentation.dto.response;

import com.rushcrew.timedeal.application.result.StockLogResult;
import com.rushcrew.timedeal.domain.vo.EventType;
import java.util.UUID;

public record StockLogResponse(
    UUID stockLogId,
    UUID timeDealStockId,
    UUID orderId,
    EventType eventType,
    Long quantity,
    String description
) {

    public static StockLogResponse from(StockLogResult stockLogResult) {
        return new StockLogResponse(
            stockLogResult.stockLogId(),
            stockLogResult.timeDealStockId(),
            stockLogResult.orderId(),
            stockLogResult.eventType(),
            stockLogResult.quantity(),
            stockLogResult.description()
        );
    }
}
