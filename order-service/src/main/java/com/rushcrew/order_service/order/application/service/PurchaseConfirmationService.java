package com.rushcrew.order_service.order.application.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;

import org.springframework.stereotype.Service;

import com.rushcrew.common.exception.BusinessException;
import com.rushcrew.order_service.order.application.command.ConfirmPurchaseCommand;
import com.rushcrew.order_service.order.application.command.ConfirmPurchaseResult;
import com.rushcrew.order_service.order.application.error.OrderErrorCode;
import com.rushcrew.order_service.order.application.port.out.PointEventPort;
import com.rushcrew.order_service.order.domain.entity.Order;
import com.rushcrew.order_service.order.domain.repository.OrderRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PurchaseConfirmationService {
	private final OrderRepository orderRepository;
	private final PointEventPort pointEventPort;

	/* 구매 확정 */
	@Transactional
	public ConfirmPurchaseResult confirmPurchase(ConfirmPurchaseCommand command) {
		// 주문 조회
		Order order = orderRepository.findById(command.orderId())
			.orElseThrow(() -> new BusinessException(OrderErrorCode.ORDER_NOT_FOUND));
		// 본인 주문인지
		if (!order.isOwnedBy(command.userId())) {
			throw new BusinessException(OrderErrorCode.UNAUTHORIZED);
		}
		// 주문 상태 PAID 인지
		if (!order.canConfirmPurchase()) {
			// TODO: 에러 코드 생성
			throw new IllegalArgumentException("구매확정 가능한 주문 상태가 아닙니다.");
		}
		// 구매확정 처리
		order.confirmPurchase();
		orderRepository.save(order);
		// 포인트 적립 금액 계산 TODO: 주문쪽에서 계산해서 넘겨주는지 포인트쪽에서 계산하는지
		// 최종 결제 금액의 5% 적립되도록
		BigDecimal earnAmount = order.getFinalAmount()
			.multiply(new BigDecimal("0.05"))
			.setScale(0, RoundingMode.DOWN);
		// 포인트 적립 요청 이벤트 발행
		pointEventPort.publishPointEarnRequested(
			command.userId(),
			command.orderId().toString(),
			earnAmount, // 포인트적립금액을 주문 쪽에서 안 하면 finalAmount 넘김
			"구매확정",
			Instant.now()
		);
		return ConfirmPurchaseResult.builder()
			.orderId(command.orderId())
			.orderStatus(order.getStatus().name())
			.earnedPoints(earnAmount)
			.confirmedAt(order.getPurchaseConfirmedAt())
			.message("구매가 확정되었습니다. %s 포인트가 적립됩니다.".formatted(earnAmount))
			.build();
	}
}
