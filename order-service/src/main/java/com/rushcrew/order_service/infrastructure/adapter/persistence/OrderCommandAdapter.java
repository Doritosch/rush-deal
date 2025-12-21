package com.rushcrew.order_service.infrastructure.adapter.persistence;

import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Component;

import com.rushcrew.order_service.application.command.port.out.OrderCommandPort;
import com.rushcrew.order_service.domain.model.order.Order;
import com.rushcrew.order_service.infrastructure.persistence.repository.OrderJpaRepository;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class OrderCommandAdapter implements OrderCommandPort {
	private final OrderJpaRepository orderJpaRepository;

	@Override
	public Integer getTotalPurchasedQuantity(Long userId, UUID productId) {
		return orderJpaRepository.getTotalPurchasedQuantity(userId, productId);
	}

	@Override
	public Order save(Order order) {
		return orderJpaRepository.save(order);
	}

	@Override
	public Optional<Order> findById(UUID orderId) {
		return orderJpaRepository.findById(orderId);
	}
}
