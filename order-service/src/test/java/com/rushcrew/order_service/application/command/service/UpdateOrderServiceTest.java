// package com.rushcrew.order_service.application.command.service;
//
// import static org.assertj.core.api.Assertions.*;
// import static org.mockito.ArgumentMatchers.*;
// import static org.mockito.Mockito.*;
//
// import java.math.BigDecimal;
// import java.time.Instant;
// import java.time.temporal.ChronoUnit;
// import java.util.Optional;
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
// import com.rushcrew.common.exception.BusinessException;
// import com.rushcrew.order_service.application.command.dto.command.UpdateOrderCommand;
// import com.rushcrew.order_service.application.command.dto.result.UpdateOrderResult;
// import com.rushcrew.order_service.application.command.port.out.OrderCachePort;
// import com.rushcrew.order_service.application.command.port.out.OrderCommandPort;
// import com.rushcrew.order_service.domain.enums.OrderStatus;
// import com.rushcrew.order_service.domain.model.order.Order;
// import com.rushcrew.order_service.domain.model.order.OrderItem;
// import com.rushcrew.order_service.domain.vo.ProductSnapshot;
// import com.rushcrew.order_service.domain.vo.ShippingInfo;
// import com.rushcrew.order_service.global.error.OrderErrorCode;
//
// @ExtendWith(MockitoExtension.class)
// @DisplayName("주문 수정 서비스 테스트")
// class UpdateOrderServiceTest {
//
// 	@Mock
// 	private OrderCommandPort orderCommandPort;
//
// 	@Mock
// 	private OrderCachePort orderCachePort;
//
// 	@InjectMocks
// 	private UpdateOrderService updateOrderService;
//
// 	private Order order;
// 	private Long userId;
// 	private ShippingInfo newShippingInfo;
//
// 	@BeforeEach
// 	void setUp() {
// 		userId = 1L;
//
// 		ShippingInfo originalShippingInfo = ShippingInfo.create(
// 			"홍길동",
// 			"01012345678",
// 			"12345",
// 			"서울시 강남구",
// 			"101동 101호",
// 			"문 앞에 놔주세요"
// 		);
//
// 		newShippingInfo = ShippingInfo.create(
// 			"김철수",
// 			"01098765432",
// 			"54321",
// 			"서울시 서초구",
// 			"202동 202호",
// 			"경비실에 맡겨주세요"
// 		);
//
// 		// OrderItem 생성
// 		ProductSnapshot snapshot = ProductSnapshot.builder()
// 			.timeDealStockId(String.valueOf(UUID.randomUUID()))
// 			.productId(String.valueOf(UUID.randomUUID()))
// 			.productName("테스트 상품")
// 			.productDescription("테스트 설명")
// 			.optionName("옵션1")
// 			.timeDealId(String.valueOf(UUID.randomUUID()))
// 			.timeDealTitle("타임딜 제목")
// 			.discountRate(20)
// 			.build();
//
// 		OrderItem orderItem = OrderItem.create(
// 			UUID.randomUUID(),
// 			2,
// 			BigDecimal.valueOf(10000),
// 			BigDecimal.valueOf(8000),
// 			snapshot
// 		);
//
// 		// Order 생성 (내부적으로 orderId가 자동 생성됨)
// 		order = Order.create(
// 			userId,
// 			java.util.List.of(orderItem),
// 			1000L,
// 			originalShippingInfo
// 		);
// 	}
//
// 	@Test
// 	@DisplayName("PENDING 상태 - 배송지 정보 수정 성공")
// 	void testUpdateShippingInfo_PENDING_Success() {
// 		// given
// 		UUID actualOrderId = order.getOrderId(); // 실제 Order의 orderId 가져오기 --> Order.create에서 orderId를 생성해주고 있어서
// 		when(orderCommandPort.findById(actualOrderId)).thenReturn(Optional.of(order));
// 		when(orderCommandPort.save(any(Order.class))).thenAnswer(invocation -> invocation.getArgument(0));
//
// 		UpdateOrderCommand command = UpdateOrderCommand.builder()
// 			.orderId(actualOrderId)
// 			.userId(userId)
// 			.shippingInfo(newShippingInfo)
// 			.pointUsed(null)
// 			.build();
//
// 		// when
// 		UpdateOrderResult result = updateOrderService.updateOrder(command);
//
// 		// then
// 		assertThat(result).isNotNull();
// 		assertThat(result.orderId()).isEqualTo(actualOrderId);
// 		assertThat(result.shippingInfo()).isNotNull();
// 		assertThat(result.shippingInfo().recipientName()).isEqualTo("김철수");
// 		assertThat(result.shippingInfo().recipientPhone()).isEqualTo("01098765432");
// 		assertThat(result.shippingInfo().zipCode()).isEqualTo("54321");
// 		assertThat(result.shippingInfo().addressBase()).isEqualTo("서울시 서초구");
//
// 		verify(orderCommandPort).findById(actualOrderId);
// 		verify(orderCommandPort).save(any(Order.class));
// 		verify(orderCachePort).updateOrderCache(eq(actualOrderId), any());
// 	}
//
// 	@Test
// 	@DisplayName("PENDING 상태 - 포인트 사용량 수정 성공")
// 	void testUpdatePointUsed_PENDING_Success() {
// 		// given
// 		UUID actualOrderId = order.getOrderId();
// 		when(orderCommandPort.findById(actualOrderId)).thenReturn(Optional.of(order));
// 		when(orderCommandPort.save(any(Order.class))).thenAnswer(invocation -> invocation.getArgument(0));
//
// 		Long newPointUsed = 2000L;
// 		UpdateOrderCommand command = UpdateOrderCommand.builder()
// 			.orderId(actualOrderId)
// 			.userId(userId)
// 			.shippingInfo(null)
// 			.pointUsed(newPointUsed)
// 			.build();
//
// 		// when
// 		UpdateOrderResult result = updateOrderService.updateOrder(command);
//
// 		// then
// 		assertThat(result).isNotNull();
// 		assertThat(result.orderId()).isEqualTo(actualOrderId);
// 		assertThat(result.pointUsed()).isEqualByComparingTo(newPointUsed);
// 		assertThat(result.finalAmount()).isEqualByComparingTo(BigDecimal.valueOf(14000)); // 16000 - 2000
//
// 		verify(orderCommandPort).findById(actualOrderId);
// 		verify(orderCommandPort).save(any(Order.class));
// 		verify(orderCachePort).updateOrderCache(eq(actualOrderId), any());
// 	}
//
// 	@Test
// 	@DisplayName("PENDING 상태 - 배송지 정보와 포인트 사용량 동시 수정 성공")
// 	void testUpdateBoth_PENDING_Success() {
// 		// given
// 		UUID actualOrderId = order.getOrderId();
// 		when(orderCommandPort.findById(actualOrderId)).thenReturn(Optional.of(order));
// 		when(orderCommandPort.save(any(Order.class))).thenAnswer(invocation -> invocation.getArgument(0));
//
// 		Long newPointUsed = 2000L;
// 		UpdateOrderCommand command = UpdateOrderCommand.builder()
// 			.orderId(actualOrderId)
// 			.userId(userId)
// 			.shippingInfo(newShippingInfo)
// 			.pointUsed(newPointUsed)
// 			.build();
//
// 		// when
// 		UpdateOrderResult result = updateOrderService.updateOrder(command);
//
// 		// then
// 		assertThat(result).isNotNull();
// 		assertThat(result.orderId()).isEqualTo(actualOrderId);
// 		assertThat(result.shippingInfo().recipientName()).isEqualTo("김철수");
// 		assertThat(result.pointUsed()).isEqualByComparingTo(newPointUsed);
//
// 		verify(orderCommandPort).findById(actualOrderId);
// 		verify(orderCommandPort).save(any(Order.class));
// 		verify(orderCachePort).updateOrderCache(eq(actualOrderId), any());
// 	}
//
// 	@Test
// 	@DisplayName("PAID 상태 - 배송지 정보 수정 성공")
// 	void testUpdateShippingInfo_PAID_Success() {
// 		// given
// 		order.completePayment();
// 		UUID actualOrderId = order.getOrderId();
// 		when(orderCommandPort.findById(actualOrderId)).thenReturn(Optional.of(order));
// 		when(orderCommandPort.save(any(Order.class))).thenAnswer(invocation -> invocation.getArgument(0));
//
// 		UpdateOrderCommand command = UpdateOrderCommand.builder()
// 			.orderId(actualOrderId)
// 			.userId(userId)
// 			.shippingInfo(newShippingInfo)
// 			.pointUsed(null)
// 			.build();
//
// 		// when
// 		UpdateOrderResult result = updateOrderService.updateOrder(command);
//
// 		// then
// 		assertThat(result).isNotNull();
// 		assertThat(result.orderStatus()).isEqualTo(OrderStatus.PAID.name());
// 		assertThat(result.shippingInfo().recipientName()).isEqualTo("김철수");
//
// 		verify(orderCommandPort).findById(actualOrderId);
// 		verify(orderCommandPort).save(any(Order.class));
// 		verify(orderCachePort).updateOrderCache(eq(actualOrderId), any());
// 	}
//
// 	@Test
// 	@DisplayName("PAID 상태 - 포인트 사용량 수정 실패 (수정 불가)")
// 	void testUpdatePointUsed_PAID_Failure() {
// 		// given
// 		order.completePayment();
// 		UUID actualOrderId = order.getOrderId();
// 		when(orderCommandPort.findById(actualOrderId)).thenReturn(Optional.of(order));
//
// 		UpdateOrderCommand command = UpdateOrderCommand.builder()
// 			.orderId(actualOrderId)
// 			.userId(userId)
// 			.shippingInfo(null)
// 			.pointUsed(2000L)
// 			.build();
//
// 		// when & then
// 		assertThatThrownBy(() -> updateOrderService.updateOrder(command))
// 			.isInstanceOf(BusinessException.class)
// 			.extracting("errorCode")
// 			.isEqualTo(OrderErrorCode.ORDER_CANNOT_UPDATE_POINT);
//
// 		verify(orderCommandPort).findById(actualOrderId);
// 		verify(orderCommandPort, never()).save(any(Order.class));
// 		verify(orderCachePort, never()).updateOrderCache(any(), any());
// 	}
//
// 	@Test
// 	@DisplayName("PENDING 상태 - 15분 초과 시 수정 실패")
// 	void testUpdate_PENDING_TimeExpired() {
// 		// given
// 		UUID actualOrderId = order.getOrderId();
// 		// orderedAt을 16분 전으로 설정
// 		try {
// 			java.lang.reflect.Field orderedAtField = Order.class.getDeclaredField("orderedAt");
// 			orderedAtField.setAccessible(true);
// 			orderedAtField.set(order, Instant.now().minus(16, ChronoUnit.MINUTES));
// 		} catch (Exception e) {
// 			throw new RuntimeException(e);
// 		}
//
// 		when(orderCommandPort.findById(actualOrderId)).thenReturn(Optional.of(order));
//
// 		UpdateOrderCommand command = UpdateOrderCommand.builder()
// 			.orderId(actualOrderId)
// 			.userId(userId)
// 			.shippingInfo(newShippingInfo)
// 			.pointUsed(null)
// 			.build();
//
// 		// when & then
// 		assertThatThrownBy(() -> updateOrderService.updateOrder(command))
// 			.isInstanceOf(BusinessException.class)
// 			.extracting("errorCode")
// 			.isEqualTo(OrderErrorCode.ORDER_UPDATE_TIME_EXPIRED);
//
// 		verify(orderCommandPort).findById(actualOrderId);
// 		verify(orderCommandPort, never()).save(any(Order.class));
// 		verify(orderCachePort, never()).updateOrderCache(any(), any());
// 	}
//
// 	@Test
// 	@DisplayName("PAID 상태 - 7일 초과 시 수정 실패")
// 	void testUpdate_PAID_TimeExpired() {
// 		// given
// 		order.completePayment();
// 		UUID actualOrderId = order.getOrderId();
// 		// paymentCompletedAt을 8일 전으로 설정
// 		try {
// 			java.lang.reflect.Field paymentCompletedAtField = Order.class.getDeclaredField("paymentCompletedAt");
// 			paymentCompletedAtField.setAccessible(true);
// 			paymentCompletedAtField.set(order, Instant.now().minus(8, ChronoUnit.DAYS));
// 		} catch (Exception e) {
// 			throw new RuntimeException(e);
// 		}
//
// 		when(orderCommandPort.findById(actualOrderId)).thenReturn(Optional.of(order));
//
// 		UpdateOrderCommand command = UpdateOrderCommand.builder()
// 			.orderId(actualOrderId)
// 			.userId(userId)
// 			.shippingInfo(newShippingInfo)
// 			.pointUsed(null)
// 			.build();
//
// 		// when & then
// 		assertThatThrownBy(() -> updateOrderService.updateOrder(command))
// 			.isInstanceOf(BusinessException.class)
// 			.extracting("errorCode")
// 			.isEqualTo(OrderErrorCode.ORDER_UPDATE_TIME_EXPIRED);
//
// 		verify(orderCommandPort).findById(actualOrderId);
// 		verify(orderCommandPort, never()).save(any(Order.class));
// 		verify(orderCachePort, never()).updateOrderCache(any(), any());
// 	}
//
// 	@Test
// 	@DisplayName("PURCHASE_CONFIRMED 상태 - 수정 불가")
// 	void testUpdate_PURCHASE_CONFIRMED_Failure() {
// 		// given
// 		order.completePayment();
// 		order.confirmPurchase();
// 		UUID actualOrderId = order.getOrderId();
// 		when(orderCommandPort.findById(actualOrderId)).thenReturn(Optional.of(order));
//
// 		UpdateOrderCommand command = UpdateOrderCommand.builder()
// 			.orderId(actualOrderId)
// 			.userId(userId)
// 			.shippingInfo(newShippingInfo)
// 			.pointUsed(null)
// 			.build();
//
// 		// when & then
// 		assertThatThrownBy(() -> updateOrderService.updateOrder(command))
// 			.isInstanceOf(BusinessException.class)
// 			.extracting("errorCode")
// 			.isEqualTo(OrderErrorCode.ORDER_CANNOT_UPDATE);
//
// 		verify(orderCommandPort).findById(actualOrderId);
// 		verify(orderCommandPort, never()).save(any(Order.class));
// 		verify(orderCachePort, never()).updateOrderCache(any(), any());
// 	}
//
// 	@Test
// 	@DisplayName("CANCELLED 상태 - 수정 불가")
// 	void testUpdate_CANCELLED_Failure() {
// 		// given
// 		order.cancelBeforePayment("취소");
// 		UUID actualOrderId = order.getOrderId();
// 		when(orderCommandPort.findById(actualOrderId)).thenReturn(Optional.of(order));
//
// 		UpdateOrderCommand command = UpdateOrderCommand.builder()
// 			.orderId(actualOrderId)
// 			.userId(userId)
// 			.shippingInfo(newShippingInfo)
// 			.pointUsed(null)
// 			.build();
//
// 		// when & then
// 		assertThatThrownBy(() -> updateOrderService.updateOrder(command))
// 			.isInstanceOf(BusinessException.class)
// 			.extracting("errorCode")
// 			.isEqualTo(OrderErrorCode.ORDER_CANNOT_UPDATE);
//
// 		verify(orderCommandPort).findById(actualOrderId);
// 		verify(orderCommandPort, never()).save(any(Order.class));
// 		verify(orderCachePort, never()).updateOrderCache(any(), any());
// 	}
//
// 	@Test
// 	@DisplayName("REFUNDED 상태 - 수정 불가")
// 	void testUpdate_REFUNDED_Failure() {
// 		// 상태 PAID에서 환불
// 		order.completePayment();
// 		order.refund("환불"); // 이제 정상 수행
// 		UUID actualOrderId = order.getOrderId();
// 		when(orderCommandPort.findById(actualOrderId)).thenReturn(Optional.of(order));
//
// 		UpdateOrderCommand command = UpdateOrderCommand.builder()
// 			.orderId(actualOrderId)
// 			.userId(userId)
// 			.shippingInfo(newShippingInfo)
// 			.pointUsed(null)
// 			.build();
//
// 		assertThatThrownBy(() -> updateOrderService.updateOrder(command))
// 			.isInstanceOf(BusinessException.class)
// 			.extracting("errorCode")
// 			.isEqualTo(OrderErrorCode.ORDER_CANNOT_UPDATE);
//
// 		verify(orderCommandPort).findById(actualOrderId);
// 		verify(orderCommandPort, never()).save(any(Order.class));
// 		verify(orderCachePort, never()).updateOrderCache(any(), any());
// 	}
//
//
// 	@Test
// 	@DisplayName("주문을 찾을 수 없음")
// 	void testUpdate_OrderNotFound() {
// 		// given
// 		UUID nonExistentOrderId = UUID.randomUUID();
// 		when(orderCommandPort.findById(nonExistentOrderId)).thenReturn(Optional.empty());
//
// 		UpdateOrderCommand command = UpdateOrderCommand.builder()
// 			.orderId(nonExistentOrderId)
// 			.userId(userId)
// 			.shippingInfo(newShippingInfo)
// 			.pointUsed(null)
// 			.build();
//
// 		// when & then
// 		assertThatThrownBy(() -> updateOrderService.updateOrder(command))
// 			.isInstanceOf(BusinessException.class)
// 			.extracting("errorCode")
// 			.isEqualTo(OrderErrorCode.ORDER_NOT_FOUND);
//
// 		verify(orderCommandPort).findById(nonExistentOrderId);
// 		verify(orderCommandPort, never()).save(any(Order.class));
// 		verify(orderCachePort, never()).updateOrderCache(any(), any());
// 	}
//
// 	@Test
// 	@DisplayName("다른 사용자의 주문 수정 시도 - 접근 거부")
// 	void testUpdate_AccessDenied() {
// 		// given
// 		Long otherUserId = 999L;
// 		UUID actualOrderId = order.getOrderId();
// 		when(orderCommandPort.findById(actualOrderId)).thenReturn(Optional.of(order));
//
// 		UpdateOrderCommand command = UpdateOrderCommand.builder()
// 			.orderId(actualOrderId)
// 			.userId(otherUserId)
// 			.shippingInfo(newShippingInfo)
// 			.pointUsed(null)
// 			.build();
//
// 		// when & then
// 		assertThatThrownBy(() -> updateOrderService.updateOrder(command))
// 			.isInstanceOf(BusinessException.class)
// 			.extracting("errorCode")
// 			.isEqualTo(OrderErrorCode.ORDER_ACCESS_DENIED);
//
// 		verify(orderCommandPort).findById(actualOrderId);
// 		verify(orderCommandPort, never()).save(any(Order.class));
// 		verify(orderCachePort, never()).updateOrderCache(any(), any());
// 	}
//
// 	@Test
// 	@DisplayName("수정할 항목이 없음")
// 	void testUpdate_NoChanges() {
// 		// given
// 		UUID actualOrderId = order.getOrderId();
// 		when(orderCommandPort.findById(actualOrderId)).thenReturn(Optional.of(order));
//
// 		UpdateOrderCommand command = UpdateOrderCommand.builder()
// 			.orderId(actualOrderId)
// 			.userId(userId)
// 			.shippingInfo(null)
// 			.pointUsed(null)
// 			.build();
//
// 		// when & then
// 		assertThatThrownBy(() -> updateOrderService.updateOrder(command))
// 			.isInstanceOf(BusinessException.class)
// 			.extracting("errorCode")
// 			.isEqualTo(OrderErrorCode.ORDER_UPDATE_NO_CHANGES);
//
// 		verify(orderCommandPort).findById(actualOrderId);
// 		verify(orderCommandPort, never()).save(any(Order.class));
// 		verify(orderCachePort, never()).updateOrderCache(any(), any());
// 	}
//
// 	@Test
// 	@DisplayName("캐시 업데이트 실패해도 주문 수정은 성공")
// 	void testUpdate_CacheUpdateFailure() {
// 		// given
// 		UUID actualOrderId = order.getOrderId();
// 		when(orderCommandPort.findById(actualOrderId)).thenReturn(Optional.of(order));
// 		when(orderCommandPort.save(any(Order.class))).thenAnswer(invocation -> invocation.getArgument(0));
// 		doThrow(new RuntimeException("캐시 업데이트 실패"))
// 			.when(orderCachePort).updateOrderCache(any(), any());
//
// 		UpdateOrderCommand command = UpdateOrderCommand.builder()
// 			.orderId(actualOrderId)
// 			.userId(userId)
// 			.shippingInfo(newShippingInfo)
// 			.pointUsed(null)
// 			.build();
//
// 		// when
// 		UpdateOrderResult result = updateOrderService.updateOrder(command);
//
// 		// then - 캐시 실패해도 주문 수정은 성공
// 		assertThat(result).isNotNull();
// 		assertThat(result.orderId()).isEqualTo(actualOrderId);
// 		assertThat(result.shippingInfo().recipientName()).isEqualTo("김철수");
//
// 		verify(orderCommandPort).findById(actualOrderId);
// 		verify(orderCommandPort).save(any(Order.class));
// 		verify(orderCachePort).updateOrderCache(eq(actualOrderId), any());
// 	}
// }
