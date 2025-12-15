package com.rushcrew.order_service.application.saga.step;

import java.util.UUID;

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
import com.rushcrew.order_service.infrastructure.messaging.event.StockReservedEvent;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class CreateOrderStep {

	private final OrderCommandPort orderCommandPort;
	private final OutboxPort outboxPort;
	private final ObjectMapper objectMapper;

	@Transactional
	public void execute(SagaContext context, OrderCreationSagaData data, StockReservedEvent event) {
		log.info("[Saga-{}] CreateOrderStep 시작", context.getSagaId());

		CreateOrderCommand command = data.getCommand();

		// 1. OrderItem 생성 stock.reserved 수신한 StockReservedEvent 사용
		var orderItems = event.reservedItems().stream()
			.map(reservedItem -> OrderItem.create(
				UUID.fromString(reservedItem.timeDealStockId()),
				reservedItem.quantity(),
				reservedItem.discountedPrice()
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
		event.reservedItems().forEach(reservedItem ->
			order.addReservation(
				OrderReservation.create(
					UUID.fromString(reservedItem.timeDealStockId()),
					reservedItem.quantity()
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
