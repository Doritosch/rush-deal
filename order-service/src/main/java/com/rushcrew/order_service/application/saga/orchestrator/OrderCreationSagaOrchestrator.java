package com.rushcrew.order_service.application.saga.orchestrator;

import java.time.Instant;

import org.springframework.stereotype.Component;

import com.rushcrew.order_service.application.command.dto.command.CreateOrderCommand;
import com.rushcrew.order_service.application.command.dto.result.CreateOrderResult;
import com.rushcrew.order_service.application.saga.dto.OrderCreationSagaData;
import com.rushcrew.order_service.application.saga.dto.SagaContext;
import com.rushcrew.order_service.domain.model.saga.SagaInstance;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class OrderCreationSagaOrchestrator {
	@Transactional
	public CreateOrderResult execute(CreateOrderCommand command) {
		// 1. Saga 인스턴스 생성
		SagaInstance sagaInstance = SagaInstance.create("ORDER_CREATION", command.userId());
		// 2. Saga Context 생성
		SagaContext context = SagaContext.builder()
			.sagaId(sagaInstance.getSagaId())
			.userId(command.userId())
			.build();
		// 3. Saga Data 생성
		OrderCreationSagaData sagaData = OrderCreationSagaData.builder()
			.command(command)
			.build();
		// 검증 시작
		try {
			// Step 1: 재고 검증
			// Step 2: 재고 예약
			// Step 3: 포인트 차감
			// Step 4: 주문 생성

			// Saga 완료
			sagaInstance.complete();
			log.info("[Saga-{}] 완료", context.getSagaId());

			// 결과 리턴
			return CreateOrderResult.builder()
				.build();

		} catch (Exception e) {
			// Saga 실패 처리
			sagaInstance.fail(e.getMessage());
			log.error("[Saga-{}] 실패: {}", context.getSagaId(), e.getMessage(), e);
			throw e;
		}

	}
}
