package com.rushcrew.order_service.application.saga.orchestrator;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.rushcrew.order_service.application.command.dto.command.CreateOrderCommand;
import com.rushcrew.order_service.application.command.dto.result.CreateOrderResult;
import com.rushcrew.order_service.application.port.out.MetricsPort;
import com.rushcrew.order_service.application.saga.dto.OrderCreationSagaData;
import com.rushcrew.order_service.application.saga.dto.SagaContext;
import com.rushcrew.order_service.application.saga.dto.SagaStepResult;
import com.rushcrew.order_service.application.saga.step.CreateOrderStep;
import com.rushcrew.order_service.application.saga.step.DeductPointStep;
import com.rushcrew.order_service.application.saga.step.ReserveStockStep;
import com.rushcrew.order_service.application.saga.step.ValidateStockStep;
import com.rushcrew.order_service.application.port.out.SagaInstancePort;
import com.rushcrew.order_service.domain.vo.ShippingInfo;
@ExtendWith(MockitoExtension.class)
@DisplayName("OrderCreationSagaOrchestrator 테스트")
class OrderCreationSagaOrchestratorTest {

	@Mock
	private ValidateStockStep validateStockStep;

	@Mock
	private ReserveStockStep reserveStockStep;

	@Mock
	private DeductPointStep deductPointStep;

	@Mock
	private CreateOrderStep createOrderStep;

	@Mock
	private SagaInstancePort sagaInstancePort;

	@Mock
	private MetricsPort metricsPort;

	@InjectMocks
	private OrderCreationSagaOrchestrator orchestrator;

	private CreateOrderCommand command;
	private UUID orderId;

	@BeforeEach
	void setUp() {
		when(sagaInstancePort.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

		orderId = UUID.randomUUID();

		ShippingInfo shippingInfo = ShippingInfo.create(
			"홍길동",
			"01012345678",
			"12345",
			"서울시 강남구",
			"101동 101호",
			"문 앞에 놔주세요"
		);

		command = CreateOrderCommand.builder()
			.userId(1L)
			.timeDealId(UUID.randomUUID())
			.orderItems(List.of(
				CreateOrderCommand.OrderItemCommand.builder()
					.timeDealStockId(UUID.randomUUID())
					.quantity(2)
					.build()
			))
			.pointUsed(1000L)
			.shippingInfo(shippingInfo)
			.build();
	}

	@Test
	@DisplayName("주문 생성 성공 - 모든 Step이 성공적으로 완료")
	void testCreateOrder_Success() {
		// given
		// Step 1: ValidateStock
		when(validateStockStep.execute(any(SagaContext.class), any(OrderCreationSagaData.class)))
			.thenReturn(SagaStepResult.success());

		// Step 2: ReserveStock - OrderItems 설정 후 성공 반환
		doAnswer(invocation -> {
			OrderCreationSagaData data = invocation.getArgument(1);

			// OrderItems 설정
			data.setOrderItems(List.of(
				CreateOrderResult.OrderItemResult.builder()
					.orderItemId(UUID.randomUUID())
					.timeDealStockId(UUID.randomUUID())
					.productId(UUID.randomUUID())
					.productName("테스트 상품")
					.optionName("옵션1")
					.quantity(2)
					.unitPrice(BigDecimal.valueOf(10000))
					.discountPrice(BigDecimal.valueOf(8000))
					.subtotal(BigDecimal.valueOf(16000))
					.build()
			));
			data.setTotalAmount(BigDecimal.valueOf(16000));
			data.setFinalAmount(BigDecimal.valueOf(15000));

			return SagaStepResult.success(); // SagaStepResult 반환 * --> execute()에서 반드시 SagaStepResult를 return 해야 함
		}).when(reserveStockStep).execute(any(SagaContext.class), any(OrderCreationSagaData.class));

		// Step 3: DeductPoint
		when(deductPointStep.execute(any(SagaContext.class), any(OrderCreationSagaData.class)))
			.thenReturn(SagaStepResult.success());

		// Step 4: CreateOrder - orderId와 시간 정보 설정 후 성공 반환
		doAnswer(invocation -> {
			SagaContext ctx = invocation.getArgument(0);
			OrderCreationSagaData data = invocation.getArgument(1);

			// Context에 데이터 저장
			ctx.setData("orderId", orderId);
			ctx.setData("reservationExpiresAt", Instant.now().plusSeconds(900));
			ctx.setData("orderedAt", Instant.now());
			ctx.setData("orderStatus", "PENDING");

			// SagaData에 orderId 설정
			data.setOrderId(orderId);

			return SagaStepResult.success(); // SagaStepResult 반환 * --> execute()에서 반드시 SagaStepResult를 return 해야 함
		}).when(createOrderStep).execute(any(SagaContext.class), any(OrderCreationSagaData.class));

		// when
		CreateOrderResult result = orchestrator.execute(command);

		// then
		assertThat(result).isNotNull();
		assertThat(result.orderId()).isEqualTo(orderId);
		assertThat(result.userId()).isEqualTo(1L);
		assertThat(result.orderStatus()).isEqualTo("PENDING");
		assertThat(result.totalAmount()).isEqualByComparingTo(BigDecimal.valueOf(16000));
		assertThat(result.pointUsed()).isEqualByComparingTo(1000L);
		assertThat(result.finalAmount()).isEqualByComparingTo(BigDecimal.valueOf(15000));
		assertThat(result.orderItems()).isNotEmpty();
		assertThat(result.orderItems()).hasSize(1);
		assertThat(result.orderItems().get(0).productName()).isEqualTo("테스트 상품");

		// verify - 각 Step이 정확히 1번씩 호출되었는지 확인
		verify(validateStockStep, times(1)).execute(any(SagaContext.class), any(OrderCreationSagaData.class));
		verify(reserveStockStep, times(1)).execute(any(SagaContext.class), any(OrderCreationSagaData.class));
		verify(deductPointStep, times(1)).execute(any(SagaContext.class), any(OrderCreationSagaData.class));
		verify(createOrderStep, times(1)).execute(any(SagaContext.class), any(OrderCreationSagaData.class));

		// 보상 트랜잭션이 호출되지 않았는지 확인
		verify(reserveStockStep, never()).compensate(any(), any());
		verify(deductPointStep, never()).compensate(any(), any());
	}

	@Test
	@DisplayName("Step 2 실패 - ReserveStock 실패 시 보상 트랜잭션 없음")
	void testCreateOrder_ReserveStockFails() {
		// given
		when(validateStockStep.execute(any(), any()))
			.thenReturn(SagaStepResult.success());

		when(reserveStockStep.execute(any(), any()))
			.thenReturn(SagaStepResult.failure("재고 부족"));

		// when & then
		assertThatThrownBy(() -> orchestrator.execute(command))
			.isInstanceOf(IllegalArgumentException.class)
			.hasMessageContaining("재고 부족");

		// verify
		verify(validateStockStep, times(1)).execute(any(), any());
		verify(reserveStockStep, times(1)).execute(any(), any());
		verify(deductPointStep, never()).execute(any(), any());
		verify(createOrderStep, never()).execute(any(), any());

		// Step 1은 조회만 하므로 보상 불필요
		verify(reserveStockStep, never()).compensate(any(), any());
	}

	@Test
	@DisplayName("Step 3 실패 - DeductPoint 실패 시 재고 복구")
	void testCreateOrder_DeductPointFails() {
		// given
		when(validateStockStep.execute(any(), any()))
			.thenReturn(SagaStepResult.success());

		doAnswer(invocation -> {
			OrderCreationSagaData data = invocation.getArgument(1);
			data.setOrderItems(List.of(
				CreateOrderResult.OrderItemResult.builder()
					.orderItemId(UUID.randomUUID())
					.productName("테스트 상품")
					.quantity(2)
					.unitPrice(BigDecimal.valueOf(10000))
					.discountPrice(BigDecimal.valueOf(8000))
					.subtotal(BigDecimal.valueOf(16000))
					.build()
			));
			data.setTotalAmount(BigDecimal.valueOf(16000));
			data.setFinalAmount(BigDecimal.valueOf(15000));
			return SagaStepResult.success();
		}).when(reserveStockStep).execute(any(), any());

		when(deductPointStep.execute(any(), any()))
			.thenReturn(SagaStepResult.failure("포인트 부족"));

		// when & then
		assertThatThrownBy(() -> orchestrator.execute(command))
			.isInstanceOf(IllegalArgumentException.class)
			.hasMessageContaining("포인트 부족");

		// verify
		verify(validateStockStep, times(1)).execute(any(), any());
		verify(reserveStockStep, times(1)).execute(any(), any());
		verify(deductPointStep, times(1)).execute(any(), any());
		verify(createOrderStep, never()).execute(any(), any());

		// 보상 트랜잭션: ReserveStock만 복구
		verify(reserveStockStep, times(1)).compensate(any(), any());
		verify(deductPointStep, never()).compensate(any(), any());
	}

	@Test
	@DisplayName("Step 4 실패 - CreateOrder 실패 시 포인트 환불 + 재고 복구")
	void testCreateOrder_CreateOrderFails() {
		// given
		when(validateStockStep.execute(any(), any()))
			.thenReturn(SagaStepResult.success());

		doAnswer(invocation -> {
			OrderCreationSagaData data = invocation.getArgument(1);
			data.setOrderItems(List.of(
				CreateOrderResult.OrderItemResult.builder()
					.orderItemId(UUID.randomUUID())
					.productName("테스트 상품")
					.quantity(2)
					.unitPrice(BigDecimal.valueOf(10000))
					.discountPrice(BigDecimal.valueOf(8000))
					.subtotal(BigDecimal.valueOf(16000))
					.build()
			));
			data.setTotalAmount(BigDecimal.valueOf(16000));
			data.setFinalAmount(BigDecimal.valueOf(15000));
			return SagaStepResult.success();
		}).when(reserveStockStep).execute(any(), any());

		when(deductPointStep.execute(any(), any()))
			.thenReturn(SagaStepResult.success());

		when(createOrderStep.execute(any(), any()))
			.thenReturn(SagaStepResult.failure("DB 저장 실패"));

		// when & then
		assertThatThrownBy(() -> orchestrator.execute(command))
			.isInstanceOf(IllegalArgumentException.class)
			.hasMessageContaining("DB 저장 실패");

		// verify
		verify(validateStockStep, times(1)).execute(any(), any());
		verify(reserveStockStep, times(1)).execute(any(), any());
		verify(deductPointStep, times(1)).execute(any(), any());
		verify(createOrderStep, times(1)).execute(any(), any());

		// 보상 트랜잭션: DeductPoint + ReserveStock 순서대로 복구
		verify(deductPointStep, times(1)).compensate(any(), any());
		verify(reserveStockStep, times(1)).compensate(any(), any());
	}

	@Test
	@DisplayName("Step 1 실패 - ValidateStock 실패 시 보상 트랜잭션 없음")
	void testCreateOrder_ValidateStockFails() {
		// given
		when(validateStockStep.execute(any(), any()))
			.thenReturn(SagaStepResult.failure("대기열 토큰 없음"));

		// when & then
		assertThatThrownBy(() -> orchestrator.execute(command))
			.isInstanceOf(IllegalArgumentException.class)
			.hasMessageContaining("대기열 토큰 없음");

		// verify
		verify(validateStockStep, times(1)).execute(any(), any());
		verify(reserveStockStep, never()).execute(any(), any());
		verify(deductPointStep, never()).execute(any(), any());
		verify(createOrderStep, never()).execute(any(), any());

		// Step 1은 조회만 하므로 보상 불필요
		verify(reserveStockStep, never()).compensate(any(), any());
		verify(deductPointStep, never()).compensate(any(), any());
	}
}
