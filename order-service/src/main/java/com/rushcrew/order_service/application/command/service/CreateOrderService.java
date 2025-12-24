package com.rushcrew.order_service.application.command.service;

import org.springframework.stereotype.Service;

import com.rushcrew.order_service.application.command.dto.command.CreateOrderCommand;
import com.rushcrew.order_service.application.command.dto.result.CreateOrderResult;
import com.rushcrew.order_service.application.command.usecase.CreateOrderUseCase;
import com.rushcrew.order_service.application.saga.orchestrator.OrderCreationSagaOrchestrator;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class CreateOrderService implements CreateOrderUseCase {

	private final OrderCreationSagaOrchestrator sagaOrchestrator;

	@Override
	public CreateOrderResult createOrder(CreateOrderCommand command) {
		log.info("주문 생성 Saga 시작: userId={}", command.userId());
		// Saga 시작만 수행
		var sagaId = sagaOrchestrator.execute(command);
		// 즉시 응답 (비동기 처리)
		return CreateOrderResult.accepted(sagaId);
	}
}
