// package com.rushcrew.order_service.application.command.service;
//
// import static org.assertj.core.api.Assertions.*;
// import static org.mockito.ArgumentMatchers.*;
// import static org.mockito.Mockito.*;
//
// import java.math.BigDecimal;
// import java.time.Instant;
// import java.util.UUID;
//
// import org.junit.jupiter.api.BeforeEach;
// import org.junit.jupiter.api.DisplayName;
// import org.junit.jupiter.api.Test;
// import org.junit.jupiter.api.extension.ExtendWith;
// import org.mockito.InjectMocks;
// import org.mockito.Mock;
// import org.mockito.junit.jupiter.MockitoExtension;
//
// import com.fasterxml.jackson.core.JsonProcessingException;
// import com.fasterxml.jackson.databind.ObjectMapper;
// import com.rushcrew.common.exception.BusinessException;
// import com.rushcrew.order_service.application.command.dto.command.RequestPaymentCommand;
// import com.rushcrew.order_service.application.command.dto.result.RequestPaymentResult;
// import com.rushcrew.order_service.application.command.port.out.OrderCommandPort;
// import com.rushcrew.order_service.application.port.out.OutboxPort;
// import com.rushcrew.order_service.application.port.out.PaymentEventPort;
// import com.rushcrew.order_service.application.port.out.PaymentPort;
// import com.rushcrew.order_service.domain.enums.OrderStatus;
// import com.rushcrew.order_service.domain.model.order.Order;
// import com.rushcrew.order_service.global.error.OrderErrorCode;
//
// @ExtendWith(MockitoExtension.class)
// @DisplayName("결제 요청 서비스 테스트")
// class RequestPaymentServiceTest {
//
// 	@Mock
// 	private OrderCommandPort orderCommandPort;
//
// 	@Mock
// 	private PaymentPort paymentPort;
//
// 	@Mock
// 	private PaymentEventPort paymentEventPort;
//
// 	@Mock
// 	private OutboxPort outboxPort;
//
// 	@Mock
// 	private ObjectMapper objectMapper;
//
// 	@InjectMocks
// 	private RequestPaymentService requestPaymentService;
//
// 	private Order order;
// 	private RequestPaymentCommand command;
//
// 	@BeforeEach
// 	void setUp() {
// 		UUID orderId = UUID.randomUUID();
// 		command = RequestPaymentCommand.builder()
// 			.orderId(orderId)
// 			.userId(1L)
// 			.build();
//
// 		// Order Mock만 생성 (stubbing은 각 테스트에서 설정)
// 		order = mock(Order.class);
// 	}
//
// 	@Test
// 	@DisplayName("결제 요청 성공")
// 	void testRequestPayment_Success() throws JsonProcessingException {
// 		// given
// 		// order의 stubbing 설정
// 		when(order.getOrderId()).thenReturn(command.orderId());
// 		when(order.getUserId()).thenReturn(1L);
// 		when(order.getFinalAmount()).thenReturn(BigDecimal.valueOf(9000));
// 		when(order.isOwnedBy(1L)).thenReturn(true);
// 		when(order.canPay()).thenReturn(true);
//
// 		Order savedOrder = mock(Order.class);
// 		when(savedOrder.getOrderId()).thenReturn(command.orderId());
// 		when(savedOrder.getUserId()).thenReturn(1L);
// 		when(savedOrder.getFinalAmount()).thenReturn(BigDecimal.valueOf(9000));
// 		when(savedOrder.getStatus()).thenReturn(OrderStatus.PAID);
// 		when(savedOrder.getPaymentCompletedAt()).thenReturn(Instant.now());
// 		when(savedOrder.getAutoConfirmScheduledAt()).thenReturn(Instant.now().plusSeconds(604800));
//
// 		when(orderCommandPort.findById(command.orderId()))
// 			.thenReturn(java.util.Optional.of(order));
// 		when(paymentPort.requestPayment(any(), any(), any()))
// 			.thenReturn(true);
// 		when(orderCommandPort.save(any(Order.class)))
// 			.thenReturn(savedOrder);
// 		when(objectMapper.writeValueAsString(any()))
// 			.thenReturn("{}");
//
// 		// when
// 		RequestPaymentResult result = requestPaymentService.requestPayment(command);
//
// 		// then
// 		assertThat(result).isNotNull();
// 		assertThat(result.orderId()).isEqualTo(command.orderId());
// 		assertThat(result.orderStatus()).isEqualTo("PAID");
// 		assertThat(result.paymentAmount()).isEqualByComparingTo(BigDecimal.valueOf(9000));
//
// 		// verify
// 		verify(order).completePayment();
// 		verify(paymentPort, times(1)).requestPayment(any(), any(), any());
// 		verify(paymentEventPort, times(1)).publishPaymentCompleted(any(), any(), any(), any());
// 		verify(outboxPort, times(1)).createAndSave(anyString(), any(), anyString(), anyString());
// 	}
//
// 	@Test
// 	@DisplayName("결제 요청 실패 - 주문을 찾을 수 없음")
// 	void testRequestPayment_OrderNotFound() {
// 		// given
// 		when(orderCommandPort.findById(command.orderId()))
// 			.thenReturn(java.util.Optional.empty());
//
// 		// when & then
// 		assertThatThrownBy(() -> requestPaymentService.requestPayment(command))
// 			.isInstanceOf(BusinessException.class)
// 			.extracting("errorCode")
// 			.isEqualTo(OrderErrorCode.ORDER_NOT_FOUND);
//
// 		verify(paymentPort, never()).requestPayment(any(), any(), any());
// 	}
//
// 	@Test
// 	@DisplayName("결제 요청 실패 - 권한 없음")
// 	void testRequestPayment_AccessDenied() {
// 		// given
// 		Order otherUserOrder = mock(Order.class);
// 		when(otherUserOrder.isOwnedBy(1L)).thenReturn(false);
//
// 		when(orderCommandPort.findById(command.orderId()))
// 			.thenReturn(java.util.Optional.of(otherUserOrder));
//
// 		// when & then
// 		assertThatThrownBy(() -> requestPaymentService.requestPayment(command))
// 			.isInstanceOf(BusinessException.class)
// 			.extracting("errorCode")
// 			.isEqualTo(OrderErrorCode.ORDER_ACCESS_DENIED);
// 	}
// }
