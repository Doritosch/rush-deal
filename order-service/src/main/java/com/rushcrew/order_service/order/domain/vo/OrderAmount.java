package com.rushcrew.order_service.order.domain.vo;

import java.math.BigDecimal;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * OrderAmount.class: 금액 관련 로직 (추후 할인, 쿠폰 등 금액 정책 생성 시 여기에 추가
 * totalAmount: 총 주문 금액
 * pointUsed: 사용한 포인트
 * finalAmount: 최종 결제 금액
 */
@Embeddable
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class OrderAmount {

	@Column(nullable = false, precision = 12, scale = 2)
	private BigDecimal totalAmount;

	@Column(nullable = false, precision = 12, scale = 2)
	private BigDecimal pointUsed;

	@Column(nullable = false, precision = 12, scale = 2)
	private BigDecimal finalAmount;


	// ============================================
	//                 도메인 로직
	// ============================================

	public static OrderAmount create(BigDecimal totalAmount, BigDecimal pointUsed) {
		validate(totalAmount, pointUsed);
		if (pointUsed.compareTo(totalAmount) > 0) {
			throw new IllegalArgumentException(
				"포인트 사용량은 주문 금액을 초과할 수 없습니다. (포인트: %s, 주문금액: %s)"
					.formatted(pointUsed, totalAmount)
			);
		}
		BigDecimal finalAmount = totalAmount.subtract(pointUsed);
		if (finalAmount.compareTo(BigDecimal.ZERO) < 0) {
			throw new IllegalArgumentException("최종 결제 금액은 0보다 작을 수 없습니다.");
		}
		return new OrderAmount(totalAmount, pointUsed, finalAmount);
	}

	public OrderAmount updatePointUsed(BigDecimal newPointUsed) {
		if (newPointUsed == null || newPointUsed.compareTo(BigDecimal.ZERO) < 0) {
			throw new IllegalArgumentException("포인트 사용량은 0 이상이어야 합니다.");
		}
		if (newPointUsed.compareTo(this.totalAmount) > 0) {
			throw new IllegalArgumentException(
				"포인트 사용량은 주문 금액을 초과할 수 없습니다. (포인트: %s, 주문금액: %s)"
					.formatted(newPointUsed, this.totalAmount)
			);
		}
		return create(this.totalAmount, newPointUsed);
	}

	private static void validate(BigDecimal totalAmount, BigDecimal pointUsed) {
		if (totalAmount == null || totalAmount.compareTo(BigDecimal.ZERO) < 0) {
			throw new IllegalArgumentException("주문 금액은 0 이상이어야 합니다.");
		}
		if (pointUsed == null || pointUsed.compareTo(BigDecimal.ZERO) < 0) {
			throw new IllegalArgumentException("포인트 사용량은 0 이상이어야 합니다.");
		}
	}

	// 포인트 사용 X
	public static OrderAmount withoutPoint(BigDecimal totalAmount) {
		return create(totalAmount, BigDecimal.ZERO);
	}
	
}
