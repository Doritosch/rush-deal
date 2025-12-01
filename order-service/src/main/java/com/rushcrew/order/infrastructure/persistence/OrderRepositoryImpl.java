package com.rushcrew.order.infrastructure.persistence;

import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Repository;

import com.rushcrew.order.domain.order.Order;
import com.rushcrew.order.domain.repository.OrderRepository;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class OrderRepositoryImpl implements OrderRepository {

	private final OrderJpaRepository orderJpaRepository;

	@Override
	public Order save(Order order) {
		return null;
	}

	@Override
	public Optional<Order> findById(UUID orderId) {
		return Optional.empty();
	}

	@Override
	public Integer getTotalPurchasedQuantityByUserAndTimeDeal(Long userId, String timeDealId) {
		return 0;
	}

	@Override
	public boolean existsById(UUID orderId) {
		return false;
	}
}
