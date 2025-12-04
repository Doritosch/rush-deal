package com.rushcrew.order_service.order.application.service;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.rushcrew.common.exception.BusinessException;
import com.rushcrew.order_service.order.application.command.RequestPaymentCommand;
import com.rushcrew.order_service.order.application.command.RequestPaymentResult;
import com.rushcrew.order_service.order.application.error.OrderErrorCode;
import com.rushcrew.order_service.order.application.port.out.PaymentEventPort;
import com.rushcrew.order_service.order.domain.entity.Order;
import com.rushcrew.order_service.order.domain.entity.OrderReservation;
import com.rushcrew.order_service.order.domain.repository.OrderRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PaymentService {

	private final OrderRepository orderRepository;
	private final PaymentEventPort paymentEventPort;

	/* 결제 요청 */
	public RequestPaymentResult requestPayment(RequestPaymentCommand command) {
		// 1. 주문 조회
		Order order = orderRepository.findById(command.orderId())
			.orElseThrow(() -> new BusinessException(OrderErrorCode.ORDER_NOT_FOUND));
		// 2. 본인 주문인지
		if (!order.isOwnedBy(command.userId())) {
			throw new BusinessException(OrderErrorCode.UNAUTHORIZED);
		}
		// 3. 주문 상태 PENDING 인지
		if (!order.canPay()) {
			throw new BusinessException(OrderErrorCode.INVALID_ORDER_STATE);
		}
		// 4. 예약 재고 만료되지 않았는지
		for (OrderReservation reservation : order.getReservations()) {
			if (reservation.isExpired()) {
				throw new BusinessException(OrderErrorCode.RESERVATION_EXPIRED);
			}
		}
		// 5. Saga ID 생성
		String sagaId = UUID.randomUUID().toString();
		// 6. 포인트 차감 요청 이벤트 발행
		if (order.getPointUsed().compareTo(BigDecimal.ZERO) > 0) {
			paymentEventPort.publishPointDeductionRequested(
				command.userId(),
				command.orderId().toString(),
				order.getPointUsed(),
				sagaId,
				Instant.now()
			);
			// 포인트 차감 대기중
			return RequestPaymentResult.builder()
				.orderId(command.orderId())
				.orderStatus(order.getStatus().name())
				.finalAmount(order.getFinalAmount())
				.pointUsed(order.getPointUsed())
				.paymentStatus("PENDING")
				.sagaId(sagaId)
				.requestedAt(Instant.now())
				.build();
				
		}
		// 7. 포인트 사용 없으면 바로 결제 요청
		paymentEventPort.publishPaymentRequested(
			command.orderId().toString(),
			command.userId(),
			order.getTotalAmount(),
			order.getPointUsed(),
			order.getFinalAmount(),
			command.paymentMethod(),
			sagaId,
			Instant.now()
		);

		return RequestPaymentResult.builder()
			.orderId(command.orderId())
			.orderStatus(order.getStatus().name())
			.finalAmount(order.getFinalAmount())
			.pointUsed(order.getPointUsed())
			.paymentStatus("PROCESSING")
			.sagaId(sagaId)
			.requestedAt(Instant.now())
			.build();
	}

}
