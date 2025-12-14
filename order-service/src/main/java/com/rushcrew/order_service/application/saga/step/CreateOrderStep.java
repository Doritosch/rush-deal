package com.rushcrew.order_service.application.saga.step;

import java.math.BigDecimal;

import org.springframework.stereotype.Component;

import com.rushcrew.order_service.application.command.dto.command.CreateOrderCommand;
import com.rushcrew.order_service.application.command.port.out.OrderCommandPort;
import com.rushcrew.order_service.application.port.out.OutboxPort;
import com.rushcrew.order_service.application.saga.dto.OrderCreationSagaData;
import com.rushcrew.order_service.application.saga.dto.SagaContext;
import com.rushcrew.order_service.domain.model.order.Order;
import com.rushcrew.order_service.domain.model.order.OrderItem;
import com.rushcrew.order_service.domain.model.order.OrderReservation;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class CreateOrderStep {

	private final OrderCommandPort orderCommandPort;
	private final OutboxPort outboxPort;
	private final ObjectMapper objectMapper;

	public void execute(SagaContext context, OrderCreationSagaData data) {
		log.info("[Saga-{}] CreateOrderStep 시작", context.getSagaId());

		CreateOrderCommand command = data.getCommand();

		// 1. OrderItem 생성 (ProductSnapshot 제거)
		var orderItems = command.orderItems().stream()
			.map(itemCommand -> OrderItem.create(
				itemCommand.timeDealStockId(),
				itemCommand.quantity(),
				BigDecimal.ZERO, // unitPrice, 실제 가격 로직 필요
				BigDecimal.ZERO, // discountPrice
				null             // ProductSnapshot 없음
			))
			.toList();

		// 2. Order 생성
		Order order = Order.create(
			command.userId(),
			orderItems,
			command.pointUsed(),
			command.shippingInfo()
		);

		// 3. 재고 예약 정보 추가
		command.orderItems().forEach(itemCommand ->
			order.addReservation(
				OrderReservation.create(
					itemCommand.timeDealStockId(),
					itemCommand.quantity()
				)
			)
		);

		// 4. 저장
		Order savedOrder = orderCommandPort.save(order);

		// 5. Outbox 이벤트
		try {
			outboxPort.createAndSave(
				"ORDER",
				savedOrder.getOrderId(),
				"ORDER_CREATED",
				objectMapper.writeValueAsString(savedOrder.toEventPayload())
			);
		} catch (Exception e) {
			throw new IllegalStateException("ORDER_CREATED Outbox 실패", e);
		}

		// 6. SagaData 반영
		data.bindOrderId(savedOrder.getOrderId());

		log.info("[Saga-{}] CreateOrderStep 완료: orderId={}", context.getSagaId(), savedOrder.getOrderId());
	}
}
