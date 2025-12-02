package com.rushcrew.order_service.order.infrastructure.adapter.out.messaging;

import java.time.Instant;
import java.util.UUID;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.rushcrew.order_service.order.application.exception.OrderNotFoundException;
import com.rushcrew.order_service.order.application.port.out.OrderEventPort;
import com.rushcrew.order_service.order.application.port.out.PaymentEventPort;
import com.rushcrew.order_service.order.application.port.out.TimeDealStockPort;
import com.rushcrew.order_service.order.domain.entity.Order;
import com.rushcrew.order_service.order.domain.entity.OrderReservation;
import com.rushcrew.order_service.order.domain.repository.OrderRepository;
import com.rushcrew.order_service.order.infrastructure.adapter.out.messaging.event.PaymentCompletedEvent;
import com.rushcrew.order_service.order.infrastructure.adapter.out.messaging.event.PaymentFailedEvent;
import com.rushcrew.order_service.order.infrastructure.adapter.out.messaging.event.PointDeductedEvent;
import com.rushcrew.order_service.order.infrastructure.adapter.out.messaging.event.PointDeductionFailedEvent;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class PaymentEventConsumer {

	private final OrderRepository orderRepository;
	private final PaymentEventPort paymentEventPort;
	private final TimeDealStockPort timeDealStockPort;
	private final OrderEventPort orderEventPort;

	// 포인트 차감 성공 이벤트 처리: 포인트 차감 완료 후 결제 진행
	@KafkaListener(topics = "point.deducted", groupId = "order-service")
	@Transactional
	public void handlePointDeducted(PointDeductedEvent event) {
		Order order = orderRepository.findById(UUID.fromString(event.getOrderId()))
			.orElseThrow(() -> new OrderNotFoundException());
		// 결제 요청 이벤트 발행
		paymentEventPort.publishPaymentRequested(
			event.getOrderId(),
			event.getUserId(), 
			order.getTotalAmount(),
			event.getDeductedAmount(),
			order.getFinalAmount(),
			"CARD",	// TODO: 실제 결제 수단
			event.getSagaId(), 
			Instant.now()
		);
	}

	// 포인트 차감 실패 이벤트 처리: 주문 취소 + 재고 복구
	@KafkaListener(topics = "point.deduction.failed", groupId = "order-service")
	@Transactional
	public void handlePointDeductionFailed(PointDeductionFailedEvent event) {
		Order order = orderRepository.findById(UUID.fromString(event.getOrderId()))
			.orElseThrow(() -> new OrderNotFoundException());

		// 결제 전 주문 취소
		order.cancelBeforePayment("포인트 차감 실패: " + event.getReason());

		// 각 예약에 대한 재고 복구 요청
		for (OrderReservation reservation : order.getReservations()) {
			// TODO: 재구 복구 로직 (Port 추가)
		}
	}

	// 결제 완료 이벤트 처리: 주문 상태 변경 + 재고 확정 요청
	@KafkaListener(topics = "payment.completed", groupId = "order-service")
	@Transactional
	public void handlePaymentCompleted(PaymentCompletedEvent event) {
		Order order = orderRepository.findById(UUID.fromString(event.getOrderId()))
			.orElseThrow(() -> new OrderNotFoundException());

		// 주문 상태 변경: PENDING → PAID
		order.completePayment();
		orderRepository.save(order);

		// 각 예약에 대해 재고 예약 확정 요청
		for (OrderReservation reservation : order.getReservations()) {
			reservation.confirm();
			// TODO: 타임딜 서비스에 재고 확정 요청 (Port 추가 필요)
		}
		orderRepository.save(order);
	}

	// 결제 실패 이벤트 처리: 포인트 환불 + 주문 취소 + 재고 복구
	@KafkaListener(topics = "payment.failed", groupId = "order-service")
	@Transactional
	public void handlePaymentFailed(PaymentFailedEvent event) {
		Order order = orderRepository.findById(UUID.fromString(event.getOrderId()))
			.orElseThrow(() -> new OrderNotFoundException());

		// 포인트 환불 요청
		if (order.getPointUsed().compareTo(java.math.BigDecimal.ZERO) > 0) {
			// TODO: 포인트 환불 이벤트 발행 (Port 추가 필요)
		}

		// 재고 복구 요청
		for (OrderReservation reservation : order.getReservations()) {
			// TODO: 재고 복구 이벤트 발행
		}

		// 주문 취소
		order.cancelBeforePayment("결제 실패: " + event.getFailureReason());
		orderRepository.save(order);
	}

}
