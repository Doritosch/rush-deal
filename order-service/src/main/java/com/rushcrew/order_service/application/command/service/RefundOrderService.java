package com.rushcrew.order_service.application.command.service;

import java.time.Instant;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.rushcrew.common.exception.BusinessException;
import com.rushcrew.order_service.application.command.dto.command.RefundOrderCommand;
import com.rushcrew.order_service.application.command.dto.result.RefundOrderResult;
import com.rushcrew.order_service.application.command.port.out.OrderCommandPort;
import com.rushcrew.order_service.application.command.usecase.RefundOrderUseCase;
import com.rushcrew.order_service.application.port.out.PaymentEventPort;
import com.rushcrew.order_service.domain.model.order.Order;
import com.rushcrew.order_service.global.error.OrderErrorCode;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class RefundOrderService implements RefundOrderUseCase {

	private final OrderCommandPort orderCommandPort;
	private final PaymentEventPort paymentEventPort;

	@Override
	@Transactional
	public RefundOrderResult refundOrder(RefundOrderCommand command) {
		log.info("환불 요청 처리 시작: orderId={}, userId={}", command.orderId(), command.userId());

		Order order = orderCommandPort.findById(command.orderId())
			.orElseThrow(() -> new BusinessException(OrderErrorCode.ORDER_NOT_FOUND));

		if (!order.isOwnedBy(command.userId())) {
			throw new BusinessException(OrderErrorCode.ORDER_ACCESS_DENIED);
		}

		// 환불 가능 상태 검증 (PAID 상태만 가능, PURCHASE_CONFIRMED 이후 불가)
		if (!order.canRefund()) {
			throw new BusinessException(OrderErrorCode.ORDER_CANNOT_REFUND);
		}

		// 결제 서비스에 환불 요청 이벤트 발행 (Kafka 비동기 통신 - Outbox 패턴)
		paymentEventPort.publishRefundRequested(
			order.getOrderId(),
			order.getUserId(),
			order.getFinalAmount(),
			command.reason() != null ? command.reason() : "사용자 요청에 의한 환불",
			Instant.now()
		);

		log.info("환불 요청 이벤트 발행 완료: orderId={}, refundAmount={}",
			order.getOrderId(), order.getFinalAmount());

		// 환불은 비동기로 처리 (요청만 받고 반환)
		return RefundOrderResult.builder()
			.orderId(order.getOrderId())
			.message("환불 요청이 접수되었습니다. 처리 완료까지 시간이 소요될 수 있습니다.")
			.build();
	}
}
