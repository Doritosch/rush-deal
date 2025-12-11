package com.rushcrew.timedeal.infrastructure.kafka.consumer;

import com.rushcrew.timedeal.application.command.ConfirmStockCommand;
import com.rushcrew.timedeal.application.command.ReserveStockCommand;
import com.rushcrew.timedeal.application.service.StockService;
import com.rushcrew.timedeal.domain.vo.OrderId;
import com.rushcrew.timedeal.domain.vo.Quantity;
import com.rushcrew.timedeal.infrastructure.kafka.dto.StockConfirmEvent;
import com.rushcrew.timedeal.infrastructure.kafka.dto.StockReserveEvent;
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
}