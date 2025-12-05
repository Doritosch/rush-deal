package com.rushcrew.order_service.order.infrastructure.adapter.out.persistence;

import java.time.Instant;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.rushcrew.order_service.order.domain.entity.Order;
import com.rushcrew.order_service.order.domain.enums.OrderStatus;

public interface OrderJpaRepository extends JpaRepository<Order, UUID> {

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
		@Param("timeDealId") String timeDealId
	);

	/* 자동 구매확정 대상 조회 */
	Page<Order> findAllByStatusAndAutoConfirmScheduledAtBefore(OrderStatus status, Instant scheduledAt, Pageable pageable);
}
