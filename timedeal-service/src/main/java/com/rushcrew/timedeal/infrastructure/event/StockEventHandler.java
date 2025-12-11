package com.rushcrew.timedeal.infrastructure.event;

import com.rushcrew.timedeal.application.event.StockReservedEvent;
import com.rushcrew.timedeal.domain.port.StockCache;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Slf4j
@Component
@RequiredArgsConstructor
public class StockEventHandler {

    private final StockCache stockCache;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleStockReserved(StockReservedEvent event) {
        try {
            stockCache.reserve(event.stockId(), event.quantity());
        } catch (Exception e) {
            log.error(
                "StockReservedEvent 처리 중 Redis 반영 실패. stock={}, quantity={}",
                event.stockId(), event.quantity(), e
            );
        }

    }
}
