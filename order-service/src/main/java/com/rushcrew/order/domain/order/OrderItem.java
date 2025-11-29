package com.rushcrew.order.domain.order;

import java.math.BigDecimal;
import java.util.UUID;

import org.hibernate.annotations.Type;

import com.rushcrew.order.domain.vo.ProductSnapshot;
import com.vladmihalcea.hibernate.type.json.JsonBinaryType;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "p_order_item", schema = "order_schema")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class OrderItem {
	@Id
	private UUID orderItemId;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "order_id", nullable = false)
	private Order order;

	@Column(nullable = false)
	private UUID timeDealStockId;

	@Column(nullable = false)
	private Integer quantity;

	@Column(nullable = false, precision = 12, scale = 2)
	private BigDecimal unitPrice; // 상품 원가

	// @Column(nullable = false, precision = 12, scale = 2)
	// private BigDecimal discountPrice; // 타임딜 할인가 (실제 판매가) --> 타임딜 재고에서 할인된 가격으로 넘겨주면 제거할 예정

	@Column(nullable = false, precision = 12, scale = 2)
	private BigDecimal subtotal;

	@Type(JsonBinaryType.class)
	@Column(columnDefinition = "jsonb", nullable = false)
	private ProductSnapshot productSnapshot;
}
