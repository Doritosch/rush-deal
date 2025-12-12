package com.rushcrew.order_service.application.command.service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rushcrew.common.exception.BusinessException;
import com.rushcrew.order_service.application.command.dto.command.UpdateOrderCommand;
import com.rushcrew.order_service.application.command.dto.result.UpdateOrderResult;
import com.rushcrew.order_service.application.command.port.out.OrderCachePort;
import com.rushcrew.order_service.application.command.port.out.OrderCommandPort;
import com.rushcrew.order_service.application.command.usecase.UpdateOrderUseCase;
import com.rushcrew.order_service.application.port.out.OutboxPort;
import com.rushcrew.order_service.application.query.dto.OrderDetailDto;
import com.rushcrew.order_service.domain.enums.OrderStatus;
import com.rushcrew.order_service.domain.model.order.Order;
import com.rushcrew.order_service.global.error.OrderErrorCode;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class UpdateOrderService implements UpdateOrderUseCase {

	private final OrderCommandPort orderCommandPort;
	private final OrderCachePort orderCachePort;
	private final OutboxPort outboxPort;
	private final ObjectMapper objectMapper;

	private static final int PENDING_UPDATE_MAX_MINUTES = 15; // PENDING 상태 수정 가능 시간 (15분)
	private static final int PAID_UPDATE_MAX_DAYS = 7; // PAID 상태 수정 가능 시간 (7일)

	@Override
	@Transactional
	public UpdateOrderResult updateOrder(UpdateOrderCommand command) {
		log.info("주문 수정 시작: orderId={}, userId={}", command.orderId(), command.userId());

		Order order = orderCommandPort.findById(command.orderId())
			.orElseThrow(() -> new BusinessException(OrderErrorCode.ORDER_NOT_FOUND));

		if (!order.isOwnedBy(command.userId())) {
			throw new BusinessException(OrderErrorCode.ORDER_ACCESS_DENIED);
		}

		// 수정 가능 상태 및 시간 제한 검증
		validateUpdateConditions(order);

		// 수정 가능 항목 검증 및 수정
		boolean updated = false;

		// 배송지 정보 수정
		if (command.shippingInfo() != null) {
			order.updateShippingInfo(command.shippingInfo());
			updated = true;
			log.info("배송지 정보 수정 완료: orderId={}", order.getOrderId());
		}

		// 포인트 사용량 수정 (PENDING 상태에서만 가능)
		if (command.pointUsed() != null) {
			if (order.getStatus() != OrderStatus.PENDING) {
				throw new BusinessException(OrderErrorCode.ORDER_CANNOT_UPDATE_POINT);
			}
			order.updatePointUsed(command.pointUsed());
			updated = true;
			log.info("포인트 사용량 수정 완료: orderId={}, pointUsed={}", order.getOrderId(), command.pointUsed());
		}

		if (!updated) {
			throw new BusinessException(OrderErrorCode.ORDER_UPDATE_NO_CHANGES);
		}

		// 주문 저장
		Order savedOrder = orderCommandPort.save(order);

		// 캐시 업데이트
		try {
			OrderDetailDto orderDetailDto = OrderDetailDto.fromEntity(savedOrder);
			orderCachePort.updateOrderCache(savedOrder.getOrderId(), orderDetailDto);
			log.info("주문 캐시 업데이트 완료: orderId={}", savedOrder.getOrderId());
		} catch (Exception e) {
			log.error("주문 캐시 업데이트 실패: orderId={}", savedOrder.getOrderId(), e);
			// 캐시 업데이트 실패는 치명적이지 않으므로 예외를 던지지 않음
		}

		log.info("주문 수정 완료: orderId={}", savedOrder.getOrderId());

		// Outbox 이벤트 저장
		try {
			Map<String, Object> eventPayload = new HashMap<>();
			eventPayload.put("orderId", savedOrder.getOrderId());
			eventPayload.put("userId", savedOrder.getUserId());
			eventPayload.put("status", savedOrder.getStatus().name());
			eventPayload.put("updatedAt", Instant.now());
			eventPayload.put("shippingInfo", savedOrder.getShippingInfo());
			eventPayload.put("pointUsed", savedOrder.getPointUsed());

			outboxPort.createAndSave(
				"ORDER",
				savedOrder.getOrderId(),
				"ORDER_UPDATED",
				objectMapper.writeValueAsString(eventPayload)
			);

			log.info("ORDER_UPDATED 이벤트 Outbox 저장 완료: orderId={}", savedOrder.getOrderId());
		} catch (Exception e) {
			log.error("ORDER_UPDATED Outbox 이벤트 저장 실패: orderId={}", savedOrder.getOrderId(), e);
		}

		return UpdateOrderResult.builder()
			.orderId(savedOrder.getOrderId())
			.orderStatus(savedOrder.getStatus().name())
			.shippingInfo(savedOrder.getShippingInfo() != null ?
				UpdateOrderResult.ShippingInfoResult.builder()
					.recipientName(savedOrder.getShippingInfo().getRecipientName())
					.recipientPhone(savedOrder.getShippingInfo().getRecipientPhone())
					.zipCode(savedOrder.getShippingInfo().getZipCode())
					.addressBase(savedOrder.getShippingInfo().getAddressBase())
					.addressDetail(savedOrder.getShippingInfo().getAddressDetail())
					.deliveryMessage(savedOrder.getShippingInfo().getDeliveryMessage())
					.build() : null)
			.pointUsed(savedOrder.getPointUsed())
			.totalAmount(savedOrder.getTotalAmount())
			.finalAmount(savedOrder.getFinalAmount())
			.updatedAt(Instant.now())
			.build();
	}

	/* 수정 가능 조건 및 시간 제한 검증 */
	private void validateUpdateConditions(Order order) {
		OrderStatus status = order.getStatus();
		Instant now = Instant.now();

		// PURCHASE_CONFIRMED, CANCELLED, REFUNDED 상태는 수정 불가
		if (status == OrderStatus.PURCHASE_CONFIRMED
			|| status == OrderStatus.CANCELLED
			|| status == OrderStatus.REFUNDED) {
			throw new BusinessException(OrderErrorCode.ORDER_CANNOT_UPDATE);
		}

		// PENDING 상태: 주문 생성 후 15분 이내만 수정 가능
		if (status == OrderStatus.PENDING) {
			long minutesSinceOrdered = ChronoUnit.MINUTES.between(order.getOrderedAt(), now);
			if (minutesSinceOrdered > PENDING_UPDATE_MAX_MINUTES) {
				throw new BusinessException(OrderErrorCode.ORDER_UPDATE_TIME_EXPIRED);
			}
		}

		// PAID 상태: 결제 완료 후 7일 이내만 수정 가능 --> TODO: 추후 배송 상태 추가하거나 배송 기능 추가되면 이 부분 로직 변경해야 함
		if (status == OrderStatus.PAID) {
			if (order.getPaymentCompletedAt() == null) {
				throw new IllegalStateException("PAID 상태인데 결제 완료 시간이 없습니다.");
			}
			long daysSincePayment = ChronoUnit.DAYS.between(order.getPaymentCompletedAt(), now);
			if (daysSincePayment > PAID_UPDATE_MAX_DAYS) {
				throw new BusinessException(OrderErrorCode.ORDER_UPDATE_TIME_EXPIRED);
			}
		}
	}
}
