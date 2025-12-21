package com.rushcrew.order_service.application.command.port.out;

import java.util.Optional;
import java.util.UUID;

import com.rushcrew.order_service.domain.model.order.Order;

public interface OrderCommandPort {
	/* 기존 구매 수량 */
	Integer getTotalPurchasedQuantity(Long userId, UUID productId);

	Order save(Order order);

	Optional<Order> findById(UUID uuid);
}
