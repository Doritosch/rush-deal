package com.rushcrew.order.order.infrastructure.adapter.out.persistence;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.rushcrew.order.order.domain.entity.Order;

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
}
