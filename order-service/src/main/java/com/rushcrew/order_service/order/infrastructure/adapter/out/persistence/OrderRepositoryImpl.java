package com.rushcrew.order_service.order.infrastructure.adapter.out.persistence;

import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Repository;

import com.rushcrew.order_service.order.domain.entity.Order;
import com.rushcrew.order_service.order.domain.repository.OrderRepository;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class OrderRepositoryImpl implements OrderRepository {

	private final OrderJpaRepository orderJpaRepository;

	@Override
	public Order save(Order order) {
		return orderJpaRepository.save(order);
	}

	@Override
	public Optional<Order> findById(UUID orderId) {
		return orderJpaRepository.findById(orderId);
	}

	@Override
	public Integer getTotalPurchasedQuantity(Long userId, String timeDealId) {
		return orderJpaRepository.getTotalPurchasedQuantity(userId, timeDealId);
	}

	@Override
	public boolean existsById(UUID orderId) {
		return orderJpaRepository.existsById(orderId);
	}
}
