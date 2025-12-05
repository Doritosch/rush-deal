package com.rushcrew.order_service.order.domain.entity;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.UUID;

import org.hibernate.annotations.Type;

import com.rushcrew.common.entity.BaseEntity;
import com.rushcrew.order_service.order.domain.vo.ProductSnapshot;
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
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder(access = AccessLevel.PRIVATE)
public class OrderItem extends BaseEntity {

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

	@Column(nullable = false, precision = 12, scale = 2)
	private BigDecimal discountPrice; // 타임딜 할인가 (실제 판매가)

	@Column(nullable = false, precision = 12, scale = 2)
	private BigDecimal subtotal;

	@Type(JsonBinaryType.class)
	@Column(columnDefinition = "jsonb", nullable = false)
	private ProductSnapshot productSnapshot;


	// ============================================
	//                 도메인 로직
	// ============================================

	public static OrderItem create(
		UUID timeDealStockId,
		Integer quantity,
		BigDecimal unitPrice,
		BigDecimal discountPrice,
		ProductSnapshot productSnapshot
	) {
		if (quantity == null || quantity <= 0) {
			throw new IllegalArgumentException("수량은 1개 이상이어야 합니다.");
		}
		if (unitPrice == null || unitPrice.compareTo(BigDecimal.ZERO) <= 0) {
			throw new IllegalArgumentException("상품 가격은 0보다 커야 합니다.");
		}
		if (discountPrice == null || discountPrice.compareTo(BigDecimal.ZERO) < 0) {
			throw new IllegalArgumentException("할인가는 0 이상이어야 합니다.");
		}
		if (discountPrice.compareTo(unitPrice) > 0) {
			throw new IllegalArgumentException(
				"할인가[%S] 는 원가[%s] 보다 클 수 없습니다.".formatted(discountPrice, unitPrice)
			);
		}

		BigDecimal subtotal = discountPrice.multiply(BigDecimal.valueOf(quantity));

		return OrderItem.builder()
			.orderItemId(UUID.randomUUID())
			.timeDealStockId(timeDealStockId)
			.quantity(quantity)
			.unitPrice(unitPrice)
			.discountPrice(discountPrice)
			.subtotal(subtotal)
			.productSnapshot(productSnapshot)
			.build();
	}

	// 주문 연관 관계 설정
	void assignOrder(Order order) {
		this.order = order;
	}

	public String getProductName() {
		return productSnapshot != null ? productSnapshot.getProductName() : null;
	}

	public String getProductDescription() {
		return productSnapshot != null ? productSnapshot.getProductDescription() : null;
	}

	// 할인율
	public BigDecimal getDiscountRate() {
		if (unitPrice.compareTo(BigDecimal.ZERO) == 0) {
			return BigDecimal.ZERO;
		}
		// (원가 - 할인가) / 원가 x 100
		BigDecimal discount = unitPrice.subtract(discountPrice);
		return discount
			.divide(unitPrice, 2, RoundingMode.HALF_UP)
			.multiply(BigDecimal.valueOf(100));
	}

	// 할인 금액
	public BigDecimal getTotalDiscount() {
		// (원가 - 할인가) x 수량
		return unitPrice.subtract(discountPrice).multiply(BigDecimal.valueOf(quantity));
	}

}
