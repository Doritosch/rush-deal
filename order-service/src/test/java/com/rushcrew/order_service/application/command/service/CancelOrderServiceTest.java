package com.rushcrew.order_service.application.command.service;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rushcrew.common.exception.BusinessException;
import com.rushcrew.order_service.application.command.dto.command.CancelOrderCommand;
import com.rushcrew.order_service.application.command.dto.result.CancelOrderResult;
import com.rushcrew.order_service.application.command.port.out.OrderCommandPort;
import com.rushcrew.order_service.application.port.out.OutboxPort;
import com.rushcrew.order_service.application.port.out.StockEventPort;
import com.rushcrew.order_service.domain.enums.OrderStatus;
import com.rushcrew.order_service.domain.enums.ReservationStatus;
import com.rushcrew.order_service.domain.model.order.Order;
import com.rushcrew.order_service.domain.model.order.OrderItem;
import com.rushcrew.order_service.domain.model.order.OrderReservation;
import com.rushcrew.order_service.domain.vo.ProductSnapshot;
import com.rushcrew.order_service.domain.vo.ShippingInfo;
import com.rushcrew.order_service.global.error.OrderErrorCode;

@ExtendWith(MockitoExtension.class)
@DisplayName("주문 취소 서비스 테스트")
class CancelOrderServiceTest {

	@Mock
	private OrderCommandPort orderCommandPort;

	@Mock
	private StockEventPort stockEventPort;

	@Mock
	private OutboxPort outboxPort;

	@Mock
	private ObjectMapper objectMapper;

	@InjectMocks
	private CancelOrderService cancelOrderService;

	private Order order;
	private Long userId;
	private UUID timeDealStockId;

	@BeforeEach
	void setUp() {
		userId = 1L;
		timeDealStockId = UUID.randomUUID();

		ShippingInfo shippingInfo = ShippingInfo.create(
			"홍길동",
			"01012345678",
			"12345",
			"서울시 강남구",
			"101동 101호",
			"문 앞에 놔주세요"
		);

		ProductSnapshot snapshot = ProductSnapshot.builder()
			.timeDealStockId(timeDealStockId)
			.productId(UUID.randomUUID())
			.productName("테스트 상품")
			.productDescription("테스트 설명")
			.optionName("옵션1")
			.timeDealId(UUID.randomUUID())
			.timeDealTitle("타임딜 제목")
			.discountRate(20)
			.build();

		OrderItem orderItem = OrderItem.create(
			timeDealStockId,
			2,
			BigDecimal.valueOf(10000),
			BigDecimal.valueOf(8000),
			snapshot
		);

		order = Order.create(
			userId,
			List.of(orderItem),
			BigDecimal.valueOf(1000),
			shippingInfo
		);

		// OrderReservation 수동으로 추가
		addReservationToOrder(order, timeDealStockId, 2);
	}

	private void addReservationToOrder(Order order, UUID timeDealStockId, int quantity) {
		try {
			// OrderReservation 생성
			OrderReservation reservation = OrderReservation.create(
				timeDealStockId,
				quantity
			);

			// Order와의 양방향 연관관계 설정
			reservation.assignOrder(order);

			// Reflection을 사용하여 Order의 reservations 필드에 접근 **
			Field reservationsField = Order.class.getDeclaredField("reservations");
			reservationsField.setAccessible(true);

			@SuppressWarnings("unchecked")
			List<OrderReservation> reservations = (List<OrderReservation>) reservationsField.get(order);
			if (reservations == null) {
				reservations = new ArrayList<>();
				reservationsField.set(order, reservations);
			}
			reservations.add(reservation);
		} catch (Exception e) {
			throw new RuntimeException("Failed to add reservation to order", e);
		}
	}

	@Test
	@DisplayName("주문 취소 성공")
	void testCancelOrder_Success() throws Exception {
		// given
		UUID actualOrderId = order.getOrderId();
		when(orderCommandPort.findById(actualOrderId)).thenReturn(Optional.of(order));
		when(orderCommandPort.save(any(Order.class))).thenAnswer(invocation -> invocation.getArgument(0));
		when(objectMapper.writeValueAsString(any())).thenReturn("{\"orderId\":\"" + actualOrderId + "\"}");

		CancelOrderCommand command = CancelOrderCommand.builder()
			.orderId(actualOrderId)
			.userId(userId)
			.build();

		// when
		CancelOrderResult result = cancelOrderService.cancelOrder(command);

		// then
		assertThat(result).isNotNull();
		assertThat(result.orderId()).isEqualTo(actualOrderId);
		assertThat(result.orderStatus()).isEqualTo("CANCELLED");
		assertThat(result.cancelledAt()).isNotNull();
		assertThat(order.getStatus()).isEqualTo(OrderStatus.CANCELLED);

		// verify
		verify(orderCommandPort).findById(actualOrderId);
		verify(orderCommandPort).save(order);
		verify(stockEventPort).publishStockReservationCancelled(
			eq(actualOrderId),
			eq(timeDealStockId),
			eq(2),
			eq("주문 취소에 의한 재고 예약 해제"),
			any(Instant.class)
		);
		verify(outboxPort).createAndSave(
			eq("ORDER"),
			eq(actualOrderId),
			eq("ORDER_CANCELLED"),
			anyString()
		);
	}

	@Test
	@DisplayName("주문 취소 실패 - 주문을 찾을 수 없음")
	void testCancelOrder_OrderNotFound() {
		// given
		UUID nonExistentOrderId = UUID.randomUUID();
		when(orderCommandPort.findById(nonExistentOrderId)).thenReturn(Optional.empty());

		CancelOrderCommand command = CancelOrderCommand.builder()
			.orderId(nonExistentOrderId)
			.userId(userId)
			.build();

		// when & then
		assertThatThrownBy(() -> cancelOrderService.cancelOrder(command))
			.isInstanceOf(BusinessException.class)
			.extracting("errorCode")
			.isEqualTo(OrderErrorCode.ORDER_NOT_FOUND);

		verify(orderCommandPort).findById(nonExistentOrderId);
		verify(orderCommandPort, never()).save(any());
		verify(stockEventPort, never()).publishStockReservationCancelled(any(), any(), any(), any(), any());
		verify(outboxPort, never()).createAndSave(any(), any(), any(), any());
	}

	@Test
	@DisplayName("주문 취소 실패 - 주문 소유자 불일치")
	void testCancelOrder_AccessDenied() {
		// given
		UUID actualOrderId = order.getOrderId();
		Long otherUserId = 999L;
		when(orderCommandPort.findById(actualOrderId)).thenReturn(Optional.of(order));

		CancelOrderCommand command = CancelOrderCommand.builder()
			.orderId(actualOrderId)
			.userId(otherUserId)
			.build();

		// when & then
		assertThatThrownBy(() -> cancelOrderService.cancelOrder(command))
			.isInstanceOf(BusinessException.class)
			.extracting("errorCode")
			.isEqualTo(OrderErrorCode.ORDER_ACCESS_DENIED);

		verify(orderCommandPort).findById(actualOrderId);
		verify(orderCommandPort, never()).save(any());
		verify(stockEventPort, never()).publishStockReservationCancelled(any(), any(), any(), any(), any());
		verify(outboxPort, never()).createAndSave(any(), any(), any(), any());
	}

	@Test
	@DisplayName("주문 취소 실패 - PENDING 상태가 아님")
	void testCancelOrder_CannotCancel() {
		// given
		order.completePayment(); // PAID 상태로 변경
		UUID actualOrderId = order.getOrderId();
		when(orderCommandPort.findById(actualOrderId)).thenReturn(Optional.of(order));

		CancelOrderCommand command = CancelOrderCommand.builder()
			.orderId(actualOrderId)
			.userId(userId)
			.build();

		// when & then
		assertThatThrownBy(() -> cancelOrderService.cancelOrder(command))
			.isInstanceOf(BusinessException.class)
			.extracting("errorCode")
			.isEqualTo(OrderErrorCode.ORDER_CANNOT_CANCEL);

		verify(orderCommandPort).findById(actualOrderId);
		verify(orderCommandPort, never()).save(any());
		verify(stockEventPort, never()).publishStockReservationCancelled(any(), any(), any(), any(), any());
		verify(outboxPort, never()).createAndSave(any(), any(), any(), any());
	}

	@Test
	@DisplayName("주문 취소 성공 - 예약 상태가 CANCELLED로 변경됨")
	void testCancelOrder_ReservationCancelled() throws Exception {
		// given
		UUID actualOrderId = order.getOrderId();
		when(orderCommandPort.findById(actualOrderId)).thenReturn(Optional.of(order));
		when(orderCommandPort.save(any(Order.class))).thenAnswer(invocation -> invocation.getArgument(0));
		when(objectMapper.writeValueAsString(any())).thenReturn("{\"orderId\":\"" + actualOrderId + "\"}");

		CancelOrderCommand command = CancelOrderCommand.builder()
			.orderId(actualOrderId)
			.userId(userId)
			.build();

		// when
		cancelOrderService.cancelOrder(command);

		// then - 예약 상태가 CANCELLED로 변경되었는지 확인
		List<OrderReservation> reservations = order.getReservations();
		assertThat(reservations).isNotEmpty();
		assertThat(reservations.get(0).getStatus()).isEqualTo(ReservationStatus.CANCELLED);
	}

	@Test
	@DisplayName("주문 취소 성공 - Outbox 저장 실패해도 취소는 성공")
	void testCancelOrder_OutboxFailure() throws Exception {
		// given
		UUID actualOrderId = order.getOrderId();
		when(orderCommandPort.findById(actualOrderId)).thenReturn(Optional.of(order));
		when(orderCommandPort.save(any(Order.class))).thenAnswer(invocation -> invocation.getArgument(0));
		when(objectMapper.writeValueAsString(any())).thenThrow(new RuntimeException("JSON 직렬화 실패"));

		CancelOrderCommand command = CancelOrderCommand.builder()
			.orderId(actualOrderId)
			.userId(userId)
			.build();

		// when
		CancelOrderResult result = cancelOrderService.cancelOrder(command);

		// then - Outbox 실패해도 취소는 성공
		assertThat(result).isNotNull();
		assertThat(result.orderId()).isEqualTo(actualOrderId);
		assertThat(result.orderStatus()).isEqualTo("CANCELLED");

		verify(orderCommandPort).save(order);
		verify(stockEventPort).publishStockReservationCancelled(
			eq(actualOrderId),
			eq(timeDealStockId),
			eq(2),
			eq("주문 취소에 의한 재고 예약 해제"),
			any(Instant.class)
		);
		verify(outboxPort, never()).createAndSave(any(), any(), any(), any());
	}
}
