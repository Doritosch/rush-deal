package com.rushcrew.timedeal.infrastructure.kafka.consumer;

import com.rushcrew.timedeal.application.command.ConfirmStockCommand;
import com.rushcrew.timedeal.application.command.ReserveStockCommand;
import com.rushcrew.timedeal.application.command.RestoreStockCommand;
import com.rushcrew.timedeal.application.service.StockService;
import com.rushcrew.timedeal.domain.vo.OrderId;
import com.rushcrew.timedeal.domain.vo.Quantity;
import com.rushcrew.timedeal.infrastructure.kafka.dto.StockConfirmEvent;
import com.rushcrew.timedeal.infrastructure.kafka.dto.StockReserveEvent;
import com.rushcrew.timedeal.infrastructure.kafka.dto.StockRestoreEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class StockEventConsumer {

    private final StockService stockService;

    // 주문 생성 -> 재고 예약
    @KafkaListener(topics = "order.created")
    public void reserve(StockReserveEvent event) {
        ReserveStockCommand command = new ReserveStockCommand(
            OrderId.of(event.orderId()),
            event.stockId(),
            Quantity.positive(event.quantity()),
            event.userId()
        );
        stockService.reserveStock(command);
    }

    // 결제 -> 재고 확정
    @KafkaListener(topics = "payment.completed")
    public void confirm(StockConfirmEvent event) {
        ConfirmStockCommand command = new ConfirmStockCommand(
            OrderId.of(event.orderId()),
            event.stockId(),
            Quantity.positive(event.quantity())
        );
        stockService.confirmStock(command);
    }

    // 주문 취소 -> 재고 복구
    @KafkaListener(topics = "order.cancelled")
    public void restore(StockRestoreEvent event) {
        RestoreStockCommand command = new RestoreStockCommand(
            event.stockId(),
            Quantity.positive(event.quantity()),
            OrderId.of(event.orderId()),
            event.reason()
        );
        stockService.restoreStock(command);
    }

}
