package com.rushcrew.order.infrastructure.persistence;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.rushcrew.order.domain.order.Order;

public interface OrderJpaRepository extends JpaRepository<Order, UUID> {
}
