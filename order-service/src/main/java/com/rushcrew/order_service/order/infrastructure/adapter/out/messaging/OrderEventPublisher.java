package com.rushcrew.order_service.order.infrastructure.adapter.out.messaging;

import java.time.Instant;
import java.util.UUID;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import com.rushcrew.order_service.order.application.port.out.OrderEventPort;
import com.rushcrew.order_service.order.infrastructure.adapter.out.messaging.event.StockDepletedEvent;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class OrderEventPublisher implements OrderEventPort {

	private final KafkaTemplate<String, Object> kafkaTemplate;

	@Override
	public void publishStockDepletedEvent(
		@NonNull UUID timeDealId,
		@NonNull Long userId,
		@NonNull Integer availableStock,
		@NonNull Instant timestamp
	) {
		StockDepletedEvent event = new StockDepletedEvent(timeDealId.toString(), userId, availableStock, timestamp);
		kafkaTemplate.send("stock.depleted.event", event);
	}
}
