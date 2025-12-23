package com.rushcrew.timedeal.application.service;

import com.rushcrew.timedeal.application.command.ReserveStockCommand;
import com.rushcrew.timedeal.application.result.ReserveStockResult;
import com.rushcrew.timedeal.domain.entity.TimeDealStock;
import lombok.RequiredArgsConstructor;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class RetryStockService {

    private final StockPolicy stockPolicy;

    @Retryable(
        retryFor = {ObjectOptimisticLockingFailureException.class},
        maxAttempts = 5,
        backoff = @Backoff(delay = 5, maxDelay = 20, multiplier = 2)
    )
    @Transactional
    public ReserveStockResult reserveWithRetry(ReserveStockCommand command) {
        TimeDealStock stock = stockPolicy.getStockOrThrow(command.stockId());
        stock.reserve(command.quantity(), command.orderId());

        return ReserveStockResult.of(
            stock.getStockCounts().getAvailable(),
            "재고가 예약되었습니다."
        );
    }
}
