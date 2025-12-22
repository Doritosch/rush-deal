package com.rushcrew.timedeal.infrastructure.kafka.publisher;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rushcrew.timedeal.infrastructure.kafka.dto.StockReservationFailedEvent;
import com.rushcrew.timedeal.infrastructure.kafka.dto.StockReservedEvent;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class StockEventProducer {

	private final KafkaTemplate<String, String> kafkaTemplate;
	private final ObjectMapper objectMapper;

	public void publishStockReserved(StockReservedEvent event) {
		try {
			String message = objectMapper.writeValueAsString(event);
			kafkaTemplate.send("stock.reserved", event.sagaId(), message);
			log.info("[Saga-{}] stock.reserved 이벤트 발행 완료", event.sagaId());
		} catch (Exception e) {
			log.error("[Saga-{}] stock.reserved 이벤트 발행 실패", event.sagaId(), e);
			throw new RuntimeException("이벤트 발행 실패", e);
		}
	}

	public void publishStockReservationFailed(StockReservationFailedEvent event) {
		try {
			String message = objectMapper.writeValueAsString(event);
			kafkaTemplate.send("stock.reservation_failed", event.sagaId(), message);
			log.info("[Saga-{}] stock.reservation_failed 이벤트 발행 완료", event.sagaId());
		} catch (Exception e) {
			log.error("[Saga-{}] stock.reservation_failed 이벤트 발행 실패", event.sagaId(), e);
			throw new RuntimeException("이벤트 발행 실패", e);
		}
	}
}
