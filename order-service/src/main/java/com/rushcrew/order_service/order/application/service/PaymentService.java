package com.rushcrew.order_service.order.application.service;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.rushcrew.order_service.order.application.command.RequestPaymentCommand;
import com.rushcrew.order_service.order.application.command.RequestPaymentResult;
import com.rushcrew.order_service.order.application.exception.InvalidOrderStateException;
import com.rushcrew.order_service.order.application.exception.OrderNotFoundException;
import com.rushcrew.order_service.order.application.exception.UnauthorizedException;
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

	public RequestPaymentResult requestPayment(RequestPaymentCommand command) {
		// 1. 주문 조회
		Order order = orderRepository.findById(command.getOrderId())
			.orElseThrow(() -> new OrderNotFoundException());
		// 2. 본인 주문인지
		if (!order.isOwnedBy(command.getUserId())) {
			throw new UnauthorizedException();
		}
		// 3. 주문 상태 PENDING 인지
		if (!order.canPay()) {
			throw new InvalidOrderStateException();
		}
		// 4. 예약 재고 만료되지 않았는지
		for (OrderReservation reservation : order.getReservations()) {
			if (reservation.isExpired()) {
				throw new InvalidOrderStateException("재고 예약이 만료되었습니다. 주문을 다시 생성해주세요.");
			}
		}
		// 5. Saga ID 생성
		String sagaId = UUID.randomUUID().toString();
		// 6. 포인트 차감 요청 이벤트 발행
		if (order.getPointUsed().compareTo(BigDecimal.ZERO) > 0) {
			paymentEventPort.publishPointDeductionRequested(
				command.getUserId(),
				command.getOrderId().toString(),
				order.getPointUsed(),
				sagaId,
				Instant.now()
			);
			// 포인트 차감 대기중
			return RequestPaymentResult.builder()
				.orderId(command.getOrderId())
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
			command.getOrderId().toString(),
			command.getUserId(),
			order.getTotalAmount(),
			order.getPointUsed(),
			order.getFinalAmount(),
			command.getPaymentMethod(),
			sagaId,
			Instant.now()
		);

		return RequestPaymentResult.builder()
			.orderId(command.getOrderId())
			.orderStatus(order.getStatus().name())
			.finalAmount(order.getFinalAmount())
			.pointUsed(order.getPointUsed())
			.paymentStatus("PROCESSING")
			.sagaId(sagaId)
			.requestedAt(Instant.now())
			.build();
	}

}
