package com.rushcrew.order_service.order.domain.enums;

import lombok.Getter;

@Getter
public enum OrderStatus {
	PENDING("결제 대기"),
	PAID("결제 완료"),
	PURCHASE_CONFIRMED("구매 확정"),
	CANCELLED("취소"),
	REFUNDED("환불");

	private final String description;

	OrderStatus(String description) {
		this.description = description;
	}

	// ============================================
	//              상태 전이 검증 로직
	// ============================================

	public void validateCanPay() {
		if (this != PENDING) {
			throw new IllegalStateException(
				"결제는 PENDING 상태에서만 가능합니다. (현재 상태: %s)".formatted(this)
			);
		}
	}

	public void validateCanCancelBeforePayment() {
		if (this != PENDING) {
			throw new IllegalStateException(
				"결제 전 주문 취소는 PENDING 상태에서만 가능합니다. (현재 상태: %s)".formatted(this)
			);
		}
	}

	public void validateCanCancelAfterPayment() {
		if (this != PAID) {
			throw new IllegalStateException(
				"결제 후 주문 취소는 PAID 상태에서만 가능합니다. (현재 상태: %s)".formatted(this)
			);
		}
	}

	public void validateCanRefund() {
		if (this != PURCHASE_CONFIRMED) {
			throw new IllegalStateException(
				"환불은 PURCHASE_CONFIRMED 상태에서만 가능합니다. (현재 상태: %s)".formatted(this)
			);
		}
	}

	public void validateCanConfirmPurchase() {
		if (this != PAID) {
			throw new IllegalStateException(
				"구매확정은 PAID 상태에서만 가능합니다. (현재 상태: %s)".formatted(this)
			);
		}
	}

	public void validateCanUpdateShippingInfo() {
		if (this != PENDING && this != PAID) {
			throw new IllegalStateException(
				"배송지 정보는 PENDING 또는 PAID 상태에서만 수정할 수 있습니다. (현재 상태: %s)".formatted(this)
			);
		}
	}

	public void validateCanUpdatePointUsed() {
		if (this != PENDING) {
			throw new IllegalStateException(
				"포인트 사용량은 PENDING 상태에서만 수정할 수 있습니다. (현재 상태: %s)".formatted(this)
			);
		}
	}

	public void validateTransition(OrderStatus targetStatus) {
		if (!isValidTransition(targetStatus)) {
			throw new IllegalStateException(
				"상태 전이 불가: %s → %s".formatted(this, targetStatus)
			);
		}
	}


	// ============================================
	//               상태 검증 boolean
	// ============================================

	public boolean canPay() {
		return this == PENDING;
	}

	public boolean canCancelBeforePayment() {
		return this == PENDING;
	}

	public boolean canCancelAfterPayment() {
		return this == PAID;
	}

	public boolean canRefund() {
		return this == PURCHASE_CONFIRMED;
	}

	public boolean canConfirmPurchase() {
		return this == PAID;
	}

	public boolean canUpdateShippingInfo() {
		return this == PENDING || this == PAID;
	}

	public boolean canUpdatePointUsed() {
		return this == PENDING;
	}

	private boolean isValidTransition(OrderStatus targetStatus) {
		return switch (this) {
			case PENDING -> targetStatus == PAID || targetStatus == CANCELLED;
			case PAID -> targetStatus == PURCHASE_CONFIRMED || targetStatus == CANCELLED;
			case PURCHASE_CONFIRMED -> targetStatus == REFUNDED;
			case CANCELLED, REFUNDED -> false;
		};
	}

}
