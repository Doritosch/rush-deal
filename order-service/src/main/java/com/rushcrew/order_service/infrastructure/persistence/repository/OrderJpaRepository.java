package com.rushcrew.order_service.infrastructure.persistence.repository;

import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.rushcrew.order_service.domain.model.order.Order;

public interface OrderJpaRepository extends JpaRepository<Order, UUID> {

	/* 사용자의 특정 타임딜 누적 구매 수량 조회 */
	@Query(value = """
        SELECT COALESCE(SUM(oi.quantity), 0)
        FROM order_schema.p_order_item oi
        JOIN order_schema.p_order o ON oi.order_id = o.order_id
        WHERE o.user_id = :userId
          AND oi.product_snapshot ->> 'timeDealId' = :timeDealId
          AND o.status IN ('PAID', 'PURCHASE_CONFIRMED')
    """, nativeQuery = true)
	Integer getTotalPurchasedQuantity(
		@Param("userId") Long userId,
		@Param("timeDealId") UUID timeDealId);

	Page<Order> findByUserId(Long userId, Pageable pageable);
}
