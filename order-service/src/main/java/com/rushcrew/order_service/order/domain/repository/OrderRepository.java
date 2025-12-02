package com.rushcrew.order_service.order.domain.repository;

import java.util.Optional;
import java.util.UUID;

import com.rushcrew.order_service.order.domain.entity.Order;

public interface OrderRepository {

	Order save(Order order);

	Optional<Order> findById(UUID orderId);

	/**
	 * 사용자의 특정 타임딜 누적 구매 수량 조회
	 * (1인당 구매 제한 검증용)
	 */
	Integer getTotalPurchasedQuantity(Long userId, String timeDealId);

	boolean existsById(UUID orderId);

}
