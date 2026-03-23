package com.rushcrew.order_service.application.command.service;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.rushcrew.order_service.infrastructure.dto.payment.PaymentRequestMessage;
import com.rushcrew.order_service.infrastructure.messaging.producer.PaymentEventProducer;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rushcrew.common.exception.BusinessException;
import com.rushcrew.order_service.application.command.dto.command.RequestPaymentCommand;
import com.rushcrew.order_service.application.command.dto.result.RequestPaymentResult;
import com.rushcrew.order_service.application.command.port.out.OrderCommandPort;
import com.rushcrew.order_service.application.command.usecase.RequestPaymentUseCase;
import com.rushcrew.order_service.application.port.out.OutboxPort;
import com.rushcrew.order_service.domain.model.order.Order;
import com.rushcrew.order_service.global.error.OrderErrorCode;
import com.rushcrew.order_service.infrastructure.messaging.event.OutboxEventType;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class RequestPaymentService implements RequestPaymentUseCase {

	private final OrderCommandPort orderCommandPort;
	private final OutboxPort outboxPort;
	private final ObjectMapper objectMapper;
	private final PaymentEventProducer paymentEventProducer;

	@Override
	@Transactional
	public RequestPaymentResult requestPayment(RequestPaymentCommand command) {
		log.info("결제 요청 처리 시작: orderId={}, userId={}", command.orderId(), command.userId());

		Order order = orderCommandPort.findById(command.orderId())
			.orElseThrow(() -> new BusinessException(OrderErrorCode.ORDER_NOT_FOUND));

		if (!order.isOwnedBy(command.userId())) {
			throw new BusinessException(OrderErrorCode.ORDER_ACCESS_DENIED);
		}

		if (!order.canPay()) {
			throw new BusinessException(OrderErrorCode.ORDER_CANNOT_PAY);
		}

		try {
			PaymentRequestMessage paymentRequestMessage = new PaymentRequestMessage(order.getOrderId(),
					order.getUserId(),
					order.getFinalAmount().longValue());
			paymentEventProducer.sendPaymentRequest(paymentRequestMessage);
		} catch (JsonProcessingException e) {
			log.error("PAYMENT_REQUEST 이벤트 전송 실패: orderId={}", order.getOrderId(), e);
		}

		return RequestPaymentResult.builder()
			.orderId(order.getOrderId())
			.orderStatus(order.getStatus().name())
			.paymentAmount(order.getFinalAmount())
			.paymentCompletedAt(order.getPaymentCompletedAt())
			.autoConfirmScheduledAt(order.getAutoConfirmScheduledAt())
			.build();
	}

	public void paymentComplete(UUID orderId) {
		Order order = orderCommandPort.findById(orderId)
				.orElseThrow(() -> new BusinessException(OrderErrorCode.ORDER_NOT_FOUND));

		order.completePayment();
		orderCommandPort.save(order);

		try {
			Map<String, Object> eventPayload = new HashMap<>();
			eventPayload.put("orderId", order.getOrderId());
			eventPayload.put("userId", order.getUserId());
			eventPayload.put("status", order.getStatus().name());
			eventPayload.put("paymentAmount", order.getFinalAmount());
			eventPayload.put("paymentCompletedAt", order.getPaymentCompletedAt());
			eventPayload.put("autoConfirmScheduledAt", order.getAutoConfirmScheduledAt());

			outboxPort.createAndSave(
					"ORDER",
					order.getOrderId(),
					OutboxEventType.ORDER_PAID,
					objectMapper.writeValueAsString(eventPayload)
			);
		} catch (Exception e) {
			log.error("ORDER_PAID 이벤트 저장 실패: orderId={}", order.getOrderId(), e);
		}
	}
}
