package com.rushcrew.order_service.infrastructure.adapter.out.messaging;

import java.time.Instant;
import java.util.UUID;

import org.springframework.stereotype.Component;

import com.rushcrew.order_service.application.port.out.OrderEventPort;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class OrderEventPublisher implements OrderEventPort {
	@Override
	public void publishStockDepletedEvent(@NonNull UUID timeDealId, @NonNull Long userId,
		@NonNull Integer availableStock, @NonNull Instant timestamp) {
		// Outbox를 통해 이벤트 발행
		// → OrderCreationService에서 Outbox에 이벤트 저장
		// → Scheduler가 Polling하여 발행
	}
}
