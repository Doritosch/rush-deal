package com.rushcrew.order_service.application.command.service;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

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
import com.rushcrew.order_service.application.command.dto.command.RefundOrderCommand;
import com.rushcrew.order_service.application.command.dto.result.RefundOrderResult;
import com.rushcrew.order_service.application.command.port.out.OrderCommandPort;
import com.rushcrew.order_service.application.port.out.OutboxPort;
import com.rushcrew.order_service.application.port.out.PaymentPort;
import com.rushcrew.order_service.application.port.out.PointEventPort;
import com.rushcrew.order_service.application.port.out.StockEventPort;
import com.rushcrew.order_service.domain.enums.OrderStatus;
import com.rushcrew.order_service.domain.model.order.Order;
import com.rushcrew.order_service.domain.model.order.OrderItem;
import com.rushcrew.order_service.global.error.OrderErrorCode;

@ExtendWith(MockitoExtension.class)
@DisplayName("주문 환불 서비스 테스트")
class RefundOrderServiceTest {

	@Mock
	private OrderCommandPort orderCommandPort;

	@Mock
	private PaymentPort paymentPort;

	@Mock
	private PointEventPort pointEventPort;

	@Mock
	private StockEventPort stockEventPort;

	@Mock
	private OutboxPort outboxPort;

	@Mock
	private ObjectMapper objectMapper;

	@InjectMocks
	private RefundOrderService refundOrderService;

	private UUID orderId;
	private Long userId;
	private RefundOrderCommand command;

	@BeforeEach
	void setUp() {
		orderId = UUID.randomUUID();
		userId = 1L;

		command = RefundOrderCommand.builder()
			.orderId(orderId)
			.userId(userId)
			.reason("고객 변심")
			.build();
	}

	@Test
	@DisplayName("환불 성공 - 모든 단계 정상 처리")
	void testRefundOrder_Success() throws Exception {
		// given
		Order order = createOrderForSuccessCase();
		when(orderCommandPort.findById(orderId)).thenReturn(Optional.of(order));
		when(orderCommandPort.save(any(Order.class))).thenReturn(order);
		when(objectMapper.writeValueAsString(any())).thenReturn("{\"orderId\":\"test\"}");

		// when
		RefundOrderResult result = refundOrderService.refundOrder(command);

		// then
		assertThat(result).isNotNull();
		assertThat(result.orderId()).isEqualTo(orderId);
		assertThat(result.message()).isEqualTo("환불이 완료되었습니다.");

		// verify
		verify(paymentPort).cancelPayment(orderId, userId, BigDecimal.valueOf(50000));
		verify(order).refund("고객 변심");
		verify(orderCommandPort).save(order);
		verify(pointEventPort).publishPointRefundRequested(
			eq(userId), eq(orderId), eq(BigDecimal.valueOf(5000)),
			eq("주문 환불에 의한 포인트 환불"), any(Instant.class)
		);
		verify(stockEventPort, times(2)).publishStockRollbackRequested(
			eq(orderId), any(UUID.class), anyInt(),
			eq("주문 환불에 의한 재고 복구"), any(Instant.class)
		);
		verify(outboxPort).createAndSave("ORDER", orderId, "ORDER_REFUNDED", "{\"orderId\":\"test\"}");
	}

	@Test
	@DisplayName("환불 성공 - 포인트 사용 없는 경우")
	void testRefundOrder_Success_NoPointUsed() throws Exception {
		// given
		Order order = createOrderForSuccessCase();
		when(order.getPointUsed()).thenReturn(BigDecimal.ZERO);
		when(orderCommandPort.findById(orderId)).thenReturn(Optional.of(order));
		when(orderCommandPort.save(any(Order.class))).thenReturn(order);
		when(objectMapper.writeValueAsString(any())).thenReturn("{\"orderId\":\"test\"}");

		// when
		RefundOrderResult result = refundOrderService.refundOrder(command);

		// then
		assertThat(result).isNotNull();
		verify(pointEventPort, never()).publishPointRefundRequested(any(), any(), any(), any(), any());
		verify(stockEventPort, times(2)).publishStockRollbackRequested(any(), any(), anyInt(), any(), any());
	}

	@Test
	@DisplayName("환불 성공 - reason이 null인 경우 기본 메시지 사용")
	void testRefundOrder_Success_NullReason() throws Exception {
		// given
		RefundOrderCommand commandWithoutReason = RefundOrderCommand.builder()
			.orderId(orderId)
			.userId(userId)
			.reason(null)
			.build();

		Order order = createOrderForSuccessCase();
		when(orderCommandPort.findById(orderId)).thenReturn(Optional.of(order));
		when(orderCommandPort.save(any(Order.class))).thenReturn(order);
		when(objectMapper.writeValueAsString(any())).thenReturn("{\"orderId\":\"test\"}");

		// when
		RefundOrderResult result = refundOrderService.refundOrder(commandWithoutReason);

		// then
		assertThat(result).isNotNull();
		verify(order).refund("사용자 요청에 의한 환불");
	}

	@Test
	@DisplayName("환불 실패 - 주문을 찾을 수 없음")
	void testRefundOrder_Fail_OrderNotFound() {
		// given
		when(orderCommandPort.findById(orderId)).thenReturn(Optional.empty());

		// when & then
		assertThatThrownBy(() -> refundOrderService.refundOrder(command))
			.isInstanceOf(BusinessException.class)
			.satisfies(e -> {
				BusinessException be = (BusinessException) e;
				assertThat(be.getErrorCode()).isEqualTo(OrderErrorCode.ORDER_NOT_FOUND);
			});

		verify(paymentPort, never()).cancelPayment(any(), any(), any());
	}

	@Test
	@DisplayName("환불 실패 - 본인 주문이 아님")
	void testRefundOrder_Fail_AccessDenied() {
		// given
		Order order = createOrderForFailureCase();
		when(order.isOwnedBy(userId)).thenReturn(false);
		when(orderCommandPort.findById(orderId)).thenReturn(Optional.of(order));

		// when & then
		assertThatThrownBy(() -> refundOrderService.refundOrder(command))
			.isInstanceOf(BusinessException.class)
			.satisfies(e -> {
				BusinessException be = (BusinessException) e;
				assertThat(be.getErrorCode()).isEqualTo(OrderErrorCode.ORDER_ACCESS_DENIED);
			});

		verify(paymentPort, never()).cancelPayment(any(), any(), any());
	}

	@Test
	@DisplayName("환불 실패 - 환불 불가능한 상태")
	void testRefundOrder_Fail_CannotRefund() {
		// given
		Order order = createOrderForFailureCase();
		when(order.isOwnedBy(userId)).thenReturn(true);
		when(order.canRefund()).thenReturn(false);
		when(orderCommandPort.findById(orderId)).thenReturn(Optional.of(order));

		// when & then
		assertThatThrownBy(() -> refundOrderService.refundOrder(command))
			.isInstanceOf(BusinessException.class)
			.satisfies(e -> {
				BusinessException be = (BusinessException) e;
				assertThat(be.getErrorCode()).isEqualTo(OrderErrorCode.ORDER_CANNOT_REFUND);
			});

		verify(paymentPort, never()).cancelPayment(any(), any(), any());
	}

	@Test
	@DisplayName("환불 실패 - 결제 취소 실패")
	void testRefundOrder_Fail_PaymentCancelFailed() {
		// given
		Order order = createOrderForFailureCase();
		when(order.isOwnedBy(userId)).thenReturn(true);
		when(order.canRefund()).thenReturn(true);
		when(order.getFinalAmount()).thenReturn(BigDecimal.valueOf(50000));
		when(orderCommandPort.findById(orderId)).thenReturn(Optional.of(order));
		doThrow(new RuntimeException("결제 취소 실패: 결제 서비스 장애"))
			.when(paymentPort).cancelPayment(any(), any(), any());

		// when & then
		assertThatThrownBy(() -> refundOrderService.refundOrder(command))
			.isInstanceOf(RuntimeException.class)
			.hasMessageContaining("결제 취소 실패");

		verify(order, never()).refund(any());
		verify(orderCommandPort, never()).save(any());
		verify(pointEventPort, never()).publishPointRefundRequested(any(), any(), any(), any(), any());
		verify(stockEventPort, never()).publishStockRollbackRequested(any(), any(), any(), any(), any());
	}

	@Test
	@DisplayName("환불 성공 - ORDER_REFUNDED 이벤트 저장 실패해도 환불 완료")
	void testRefundOrder_Success_EventSaveFailed() throws Exception {
		// given
		Order order = createOrderForSuccessCase();
		when(orderCommandPort.findById(orderId)).thenReturn(Optional.of(order));
		when(orderCommandPort.save(any(Order.class))).thenReturn(order);
		when(objectMapper.writeValueAsString(any()))
			.thenThrow(new RuntimeException("JSON 변환 실패"));

		// when
		RefundOrderResult result = refundOrderService.refundOrder(command);

		// then
		assertThat(result).isNotNull();
		assertThat(result.orderId()).isEqualTo(orderId);
		verify(paymentPort).cancelPayment(any(), any(), any());
		verify(orderCommandPort).save(order);
	}

	@Test
	@DisplayName("환불 처리 중 포인트 이벤트 발행 실패해도 계속 진행")
	void testRefundOrder_Success_PointEventPublishFailed() throws Exception {
		// given
		Order order = createOrderForSuccessCase();
		when(orderCommandPort.findById(orderId)).thenReturn(Optional.of(order));
		when(orderCommandPort.save(any(Order.class))).thenReturn(order);
		when(objectMapper.writeValueAsString(any())).thenReturn("{\"orderId\":\"test\"}");
		doThrow(new RuntimeException("포인트 이벤트 발행 실패"))
			.when(pointEventPort).publishPointRefundRequested(any(), any(), any(), any(), any());

		// when
		RefundOrderResult result = refundOrderService.refundOrder(command);

		// then
		assertThat(result).isNotNull();
		verify(stockEventPort, times(2)).publishStockRollbackRequested(any(), any(), anyInt(), any(), any());
	}

	@Test
	@DisplayName("환불 처리 중 재고 이벤트 발행 실패해도 계속 진행")
	void testRefundOrder_Success_StockEventPublishFailed() throws Exception {
		// given
		Order order = createOrderForSuccessCase();
		when(orderCommandPort.findById(orderId)).thenReturn(Optional.of(order));
		when(orderCommandPort.save(any(Order.class))).thenReturn(order);
		when(objectMapper.writeValueAsString(any())).thenReturn("{\"orderId\":\"test\"}");
		doThrow(new RuntimeException("재고 이벤트 발행 실패"))
			.when(stockEventPort).publishStockRollbackRequested(any(), any(), anyInt(), any(), any());

		// when
		RefundOrderResult result = refundOrderService.refundOrder(command);

		// then
		assertThat(result).isNotNull();
		verify(outboxPort).createAndSave(eq("ORDER"), eq(orderId), eq("ORDER_REFUNDED"), anyString());
	}

	// 성공 케이스용 Order 생성 (모든 필드 stubbing)
	private Order createOrderForSuccessCase() {
		Order order = mock(Order.class);

		List<OrderItem> orderItems = new ArrayList<>();
		for (int i = 0; i < 2; i++) {
			OrderItem item = mock(OrderItem.class);
			when(item.getTimeDealStockId()).thenReturn(UUID.randomUUID());
			when(item.getQuantity()).thenReturn(i + 1);
			orderItems.add(item);
		}

		when(order.getOrderId()).thenReturn(orderId);
		when(order.getUserId()).thenReturn(userId);
		when(order.getFinalAmount()).thenReturn(BigDecimal.valueOf(50000));
		when(order.getPointUsed()).thenReturn(BigDecimal.valueOf(5000));
		when(order.getStatus()).thenReturn(OrderStatus.REFUNDED);
		when(order.getRefundedAt()).thenReturn(Instant.now());
		when(order.getOrderItems()).thenReturn(orderItems);
		when(order.isOwnedBy(userId)).thenReturn(true);
		when(order.canRefund()).thenReturn(true);

		return order;
	}

	// 실패 케이스용 Order 생성 (최소한의 stubbing만)
	private Order createOrderForFailureCase() {
		return mock(Order.class);
	}
}
