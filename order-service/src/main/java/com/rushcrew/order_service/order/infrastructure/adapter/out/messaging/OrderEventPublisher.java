package com.rushcrew.order_service.order.infrastructure.adapter.out.messaging;

import java.time.Instant;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import com.rushcrew.order_service.order.application.port.out.OrderEventPort;
import com.rushcrew.order_service.order.infrastructure.adapter.out.messaging.event.StockDepletedEvent;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class OrderEventPublisher implements OrderEventPort {

	private final KafkaTemplate<String, Object> kafkaTemplate;

	@Override
	public void publishStockDepletedEvent(String timeDealId, Long userId, Integer availableStock, Instant timestamp) {
		StockDepletedEvent event = new StockDepletedEvent(timeDealId, userId, availableStock, timestamp);
		kafkaTemplate.send("stock.depleted.event", event);
	}
}
