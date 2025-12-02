package com.rushcrew.order.infrastructure.messaging;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import com.rushcrew.order.infrastructure.messaging.event.StockDepletedEvent;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class OrderEventPublisher {

	private final KafkaTemplate<String, Object> kafkaTemplate;

	/**
	 * 재고 소진 이벤트 발행
	 * - 대기열 서비스가 수신하여 대기열 종료 처리
	 */
	public void publishStockDepletedEvent(StockDepletedEvent event) {
		kafkaTemplate.send("stock.depleted", event);
		log.info("재고 소진 이벤트 발행: timeDeal={}, user={}, availableStock={}",
			event.getTimeDealId(), event.getUserId(), event.getAvailableStock());
	}

}
