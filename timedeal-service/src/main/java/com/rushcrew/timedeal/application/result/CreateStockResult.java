package com.rushcrew.timedeal.application.result;

import com.rushcrew.timedeal.domain.entity.TimeDealStock;
import com.rushcrew.timedeal.domain.vo.TimeDealStockStatus;
import java.time.LocalDateTime;
import java.util.UUID;

public record CreateStockResult(
    UUID stockId,
    UUID productId,
    Long availableStock,
    Long reservedStock,
    Long soldStock,
    Long totalStock,
    TimeDealStockStatus status,
    LocalDateTime createdAt
) {

    public static CreateStockResult of(TimeDealStock stock, Long totalStock) {
        return new CreateStockResult(
            stock.getId(),
            stock.getTimeDealProduct().getId(),
            stock.getStockCounts().getAvailable(),
            stock.getStockCounts().getReserved(),
            stock.getStockCounts().getSold(),
            totalStock,
            stock.getStatus(),
            stock.getCreatedAt()
        );
    }
}
