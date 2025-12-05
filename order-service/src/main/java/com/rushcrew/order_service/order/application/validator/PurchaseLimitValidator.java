package com.rushcrew.order_service.order.application.validator;

import java.util.List;

import org.springframework.stereotype.Component;

import com.rushcrew.common.exception.BusinessException;
import com.rushcrew.order_service.order.application.command.CreateOrderCommand;
import com.rushcrew.order_service.order.application.error.OrderErrorCode;
import com.rushcrew.order_service.order.application.port.dto.TimeDealInfo;
import com.rushcrew.order_service.order.domain.repository.OrderRepository;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class PurchaseLimitValidator {

	private final OrderRepository orderRepository;

	public void validate(
		Long userId,
		String timeDealId,
		List<CreateOrderCommand.OrderItemCommand> items,
		TimeDealInfo timeDeal
	) {
		// 요청 수량 합계
		int requestQuantity = items.stream()
			.mapToInt(CreateOrderCommand.OrderItemCommand::getQuantity).sum();

		// 기존 구매 수량
		Integer totalPurchased = orderRepository.getTotalPurchasedQuantity(userId, timeDealId);

		// 제한 수량 확인
		Integer limitQuantity = timeDeal.getLimitQuantity();

		if (limitQuantity != null && (totalPurchased + requestQuantity) > limitQuantity) {
			throw new BusinessException(OrderErrorCode.PURCHASE_LIMIT_EXCEEDED);
		}
	}
}
