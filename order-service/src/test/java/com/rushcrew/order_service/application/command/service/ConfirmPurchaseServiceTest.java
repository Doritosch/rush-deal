package com.rushcrew.order_service.application.command.service;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.math.BigDecimal;
import java.time.Instant;
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
import com.rushcrew.order_service.application.command.dto.command.ConfirmPurchaseCommand;
import com.rushcrew.order_service.application.command.dto.result.ConfirmPurchaseResult;
import com.rushcrew.order_service.application.command.port.out.OrderCommandPort;
import com.rushcrew.order_service.application.port.out.OutboxPort;
import com.rushcrew.order_service.application.port.out.PointEventPort;
import com.rushcrew.order_service.domain.enums.OrderStatus;
import com.rushcrew.order_service.domain.model.order.Order;
import com.rushcrew.order_service.global.error.OrderErrorCode;

@ExtendWith(MockitoExtension.class)
@DisplayName("구매확정 서비스 테스트")
class ConfirmPurchaseServiceTest {

	@Mock
	private OrderCommandPort orderCommandPort;

	@Mock
	private PointEventPort pointEventPort;

	@Mock
	private OutboxPort outboxPort;

	@Mock
	private ObjectMapper objectMapper;

	@InjectMocks
	private ConfirmPurchaseService confirmPurchaseService;

	private Order order;
	private ConfirmPurchaseCommand command;
	private UUID orderId;

	@BeforeEach
	void setUp() {
		orderId = UUID.randomUUID();
		command = ConfirmPurchaseCommand.builder()
			.orderId(orderId)
			.userId(1L)
			.build();

		order = mock(Order.class);
	}

	@Test
	@DisplayName("구매확정 성공")
	void testConfirmPurchase_Success() throws Exception {
		// given
		Instant now = Instant.now();
		when(orderCommandPort.findById(orderId)).thenReturn(Optional.of(order));
		when(order.isOwnedBy(1L)).thenReturn(true);
		when(order.canConfirmPurchase()).thenReturn(true);
		when(order.getOrderId()).thenReturn(orderId);
		when(order.getUserId()).thenReturn(1L);
		when(order.getFinalAmount()).thenReturn(BigDecimal.valueOf(9000));
		when(order.getStatus()).thenReturn(OrderStatus.PURCHASE_CONFIRMED);
		when(order.getPurchaseConfirmedAt()).thenReturn(now);
		when(orderCommandPort.save(order)).thenReturn(order);
		when(objectMapper.writeValueAsString(any())).thenReturn("{\"orderId\":\"" + orderId + "\"}");

		// when
		ConfirmPurchaseResult result = confirmPurchaseService.confirmPurchase(command);

		// then
		assertThat(result).isNotNull();
		assertThat(result.orderId()).isEqualTo(orderId);
		assertThat(result.orderStatus()).isEqualTo("PURCHASE_CONFIRMED");
		assertThat(result.purchaseConfirmedAt()).isNotNull();

		// verify
		verify(orderCommandPort).findById(orderId);
		verify(order).isOwnedBy(1L);
		verify(order).canConfirmPurchase();
		verify(order).confirmPurchase();
		verify(orderCommandPort).save(order);
		verify(pointEventPort).publishPointEarnRequested(
			eq(1L),
			eq(orderId),
			eq(BigDecimal.valueOf(9000)),
			eq("구매확정"),
			any(Instant.class)
		);
		verify(outboxPort).createAndSave(
			eq("ORDER"),
			eq(orderId),
			eq("ORDER_PURCHASE_CONFIRMED"),
			anyString()
		);
	}

	@Test
	@DisplayName("구매확정 실패 - 주문을 찾을 수 없음")
	void testConfirmPurchase_OrderNotFound() {
		// given
		when(orderCommandPort.findById(orderId)).thenReturn(Optional.empty());

		// when & then
		assertThatThrownBy(() -> confirmPurchaseService.confirmPurchase(command))
			.isInstanceOf(BusinessException.class)
			.extracting("errorCode")
			.isEqualTo(OrderErrorCode.ORDER_NOT_FOUND);

		// verify
		verify(orderCommandPort).findById(orderId);
		verify(order, never()).isOwnedBy(anyLong());
		verify(orderCommandPort, never()).save(any());
		verify(pointEventPort, never()).publishPointEarnRequested(any(), any(), any(), any(), any());
		verify(outboxPort, never()).createAndSave(any(), any(), any(), any());
	}

	@Test
	@DisplayName("구매확정 실패 - 주문 소유자 불일치")
	void testConfirmPurchase_AccessDenied() {
		// given
		when(orderCommandPort.findById(orderId)).thenReturn(Optional.of(order));
		when(order.isOwnedBy(1L)).thenReturn(false);

		// when & then
		assertThatThrownBy(() -> confirmPurchaseService.confirmPurchase(command))
			.isInstanceOf(BusinessException.class)
			.extracting("errorCode")
			.isEqualTo(OrderErrorCode.ORDER_ACCESS_DENIED);

		// verify
		verify(orderCommandPort).findById(orderId);
		verify(order).isOwnedBy(1L);
		verify(order, never()).canConfirmPurchase();
		verify(order, never()).confirmPurchase();
		verify(orderCommandPort, never()).save(any());
	}

	@Test
	@DisplayName("구매확정 실패 - 구매확정 불가능한 상태")
	void testConfirmPurchase_CannotConfirm() {
		// given
		when(orderCommandPort.findById(orderId)).thenReturn(Optional.of(order));
		when(order.isOwnedBy(1L)).thenReturn(true);
		when(order.canConfirmPurchase()).thenReturn(false);

		// when & then
		assertThatThrownBy(() -> confirmPurchaseService.confirmPurchase(command))
			.isInstanceOf(BusinessException.class)
			.extracting("errorCode")
			.isEqualTo(OrderErrorCode.ORDER_CANNOT_CONFIRM_PURCHASE);

		// verify
		verify(orderCommandPort).findById(orderId);
		verify(order).isOwnedBy(1L);
		verify(order).canConfirmPurchase();
		verify(order, never()).confirmPurchase();
		verify(orderCommandPort, never()).save(any());
	}

	@Test
	@DisplayName("구매확정 성공 - Outbox 저장 실패해도 트랜잭션 롤백되지 않음")
	void testConfirmPurchase_OutboxFailureDoesNotRollback() throws Exception {
		// given
		Instant now = Instant.now();
		when(orderCommandPort.findById(orderId)).thenReturn(Optional.of(order));
		when(order.isOwnedBy(1L)).thenReturn(true);
		when(order.canConfirmPurchase()).thenReturn(true);
		when(order.getOrderId()).thenReturn(orderId);
		when(order.getUserId()).thenReturn(1L);
		when(order.getFinalAmount()).thenReturn(BigDecimal.valueOf(9000));
		when(order.getStatus()).thenReturn(OrderStatus.PURCHASE_CONFIRMED);
		when(order.getPurchaseConfirmedAt()).thenReturn(now);
		when(orderCommandPort.save(order)).thenReturn(order);
		when(objectMapper.writeValueAsString(any())).thenThrow(new RuntimeException("JSON 직렬화 실패"));

		// when
		ConfirmPurchaseResult result = confirmPurchaseService.confirmPurchase(command);

		// then
		assertThat(result).isNotNull();
		assertThat(result.orderId()).isEqualTo(orderId);
		assertThat(result.orderStatus()).isEqualTo("PURCHASE_CONFIRMED");

		// verify
		verify(order).confirmPurchase();
		verify(orderCommandPort).save(order);
		verify(pointEventPort).publishPointEarnRequested(any(), any(), any(), any(), any());
		verify(outboxPort, never()).createAndSave(any(), any(), any(), any());
	}
}
