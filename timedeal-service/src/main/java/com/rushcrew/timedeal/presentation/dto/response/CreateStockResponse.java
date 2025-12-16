package com.rushcrew.timedeal.presentation.dto.response;

import com.rushcrew.timedeal.application.result.CreateStockResult;
import com.rushcrew.timedeal.domain.vo.TimeDealStockStatus;
import java.time.LocalDateTime;
import java.util.UUID;

public record CreateStockResponse(
    UUID stockId,
    UUID productId,
    Long availableStock,
    Long reservedStock,
    Long soldStock,
    Long totalStock,
    TimeDealStockStatus status,
    LocalDateTime createdAt
) {

    public static CreateStockResponse from(CreateStockResult result) {
        return new CreateStockResponse(
            result.stockId(),
            result.productId(),
            result.availableStock(),
            result.reservedStock(),
            result.soldStock(),
            result.totalStock(),
            result.status(),
            result.createdAt()
        );
    }
}
