package com.rushcrew.order_service.domain.model.order;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import com.rushcrew.common.entity.BaseEntity;
import com.rushcrew.order_service.domain.enums.OrderEventType;
import com.rushcrew.order_service.domain.enums.OrderStatus;
import com.rushcrew.order_service.domain.vo.OrderAmount;
import com.rushcrew.order_service.domain.vo.ShippingInfo;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "p_order", schema = "order_schema")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
public class Order extends BaseEntity {

	@Id
	private UUID orderId;

	@Column(nullable = false)
	private Long userId;

	@Embedded
	private OrderAmount amount;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false, length = 20)
	private OrderStatus status;

	@Embedded
	private ShippingInfo shippingInfo;

	@Column(nullable = false)
	private Instant orderedAt;

	private Instant paymentCompletedAt;

	private Instant purchaseConfirmedAt;

	private Instant autoConfirmScheduledAt;

	private Instant cancelledAt;

	private Instant refundedAt;

	@OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
	@Builder.Default
	private List<OrderItem> orderItems = new ArrayList<>();

	@OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
	@Builder.Default
	private List<OrderReservation> reservations = new ArrayList<>();

	@OneToMany(mappedBy = "order", cascade = {CascadeType.PERSIST, CascadeType.MERGE})
	@Builder.Default
	private List<OrderHistory> histories = new ArrayList<>();



	// ============================================
	//                 도메인 로직
	// ============================================

	public static Order create(
		Long userId,
		List<OrderItem> orderItems,
		Long pointUsed,
		ShippingInfo shippingInfo
	) {
		BigDecimal totalAmount = orderItems.stream()
			.map(OrderItem::getSubtotal)
			.reduce(BigDecimal.ZERO, BigDecimal::add);

		OrderAmount amount = OrderAmount.create(totalAmount, pointUsed);

		Order order = Order.builder()
			.orderId(UUID.randomUUID())
			.userId(userId)
			.amount(amount)
			.status(OrderStatus.PENDING)
			.shippingInfo(shippingInfo)
			.orderedAt(Instant.now())
			.build();

		orderItems.forEach(order::addOrderItem);
		order.addHistory(
			OrderEventType.ORDER_CREATED,
			null,
			OrderStatus.PENDING,
			OrderStatus.PENDING.getDescription());

		return order;
	}

	public void updateShippingInfo(ShippingInfo newShippingInfo) {
		this.status.validateCanUpdateShippingInfo();
		newShippingInfo.validate();
		this.shippingInfo = newShippingInfo;
		addHistory(
			OrderEventType.SHIPPING_INFO_UPDATED,
			status,
			status,
			OrderEventType.SHIPPING_INFO_UPDATED.getDescription()
		);
	}

	// 포인트 사용량 수정 (PENDING 상태에서만 가능 - 결제 전)
	public void updatePointUsed(Long newPointUsed) {
		this.status.validateCanUpdatePointUsed();
		Long oldPointUsed = this.amount.getPointUsed();
		this.amount = this.amount.updatePointUsed(newPointUsed);
		addHistory(
			OrderEventType.POINT_USAGE_UPDATED,
			status,
			status,
			"포인트 사용량 변경: %s --> %s".formatted(oldPointUsed, newPointUsed)
		);
	}

	// 포인트 차감 실패 이력 기록
	public void recordPointDeductionFailed(String reason) {
		addHistory(
			OrderEventType.POINT_DEDUCTION_FAILED,
			this.status,
			this.status,  // 상태는 PENDING 유지
			reason
		);
	}

	// 결제 전 주문 취소
	public void cancelBeforePayment(String reason) {
		this.status.validateCanCancelBeforePayment();
		this.status.validateTransition(OrderStatus.CANCELLED);
		OrderStatus previousStatus = this.status;
		this.status = OrderStatus.CANCELLED;
		this.cancelledAt = Instant.now();
		addHistory(
			OrderEventType.CANCELLED_BEFORE_PAYMENT,
			previousStatus,
			OrderStatus.CANCELLED,
			reason != null ? reason : OrderStatus.CANCELLED.getDescription()
		);
	}

	// 환불 (결제 완료 후)
	public void refund(String reason) {
		this.status.validateCanRefund();
		this.status.validateTransition(OrderStatus.REFUNDED);
		OrderStatus previousStatus = this.status;
		this.status = OrderStatus.REFUNDED;
		this.refundedAt = Instant.now();
		addHistory(
			OrderEventType.REFUNDED,
			previousStatus,
			OrderStatus.REFUNDED,
			reason != null ? reason : OrderStatus.REFUNDED.getDescription()
		);
	}

	public void completePayment() {
		this.status.validateCanPay();
		this.status.validateTransition(OrderStatus.PAID);
		OrderStatus previousStatus = this.status;
		this.status = OrderStatus.PAID;
		this.paymentCompletedAt = Instant.now();
		// 7일 후 자동 구매확정 예약
		this.autoConfirmScheduledAt = this.paymentCompletedAt.plus(7, ChronoUnit.DAYS);
		addHistory(
			OrderEventType.PAYMENT_COMPLETED,
			previousStatus,
			OrderStatus.PAID,
			OrderStatus.PAID.getDescription()
		);
	}

	public void confirmPurchase() {
		confirmPurchase(OrderStatus.PURCHASE_CONFIRMED.getDescription());
	}

	/**
	 * 구매확정 (이유 지정 가능 - 자동 구매확정용)
	 */
	public void confirmPurchase(String reason) {
		this.status.validateCanConfirmPurchase();
		this.status.validateTransition(OrderStatus.PURCHASE_CONFIRMED);
		OrderStatus previousStatus = this.status;
		this.status = OrderStatus.PURCHASE_CONFIRMED;
		this.purchaseConfirmedAt = Instant.now();
		addHistory(
			OrderEventType.PURCHASE_CONFIRMED,
			previousStatus,
			OrderStatus.PURCHASE_CONFIRMED,
			reason != null ? reason : OrderStatus.PURCHASE_CONFIRMED.getDescription()
		);
	}

	private void addOrderItem(OrderItem orderItem) {
		this.orderItems.add(orderItem);
		orderItem.assignOrder(this);
	}

	public void addReservation(OrderReservation reservation) {
		this.reservations.add(reservation);
		reservation.assignOrder(this);
	}

	private void addHistory(OrderEventType eventType, OrderStatus previousStatus, OrderStatus newStatus, String reason) {
		OrderHistory history = OrderHistory.create(
			this,
			eventType,
			previousStatus,
			newStatus,
			reason
		);
		this.histories.add(history);
	}

	public boolean isOwnedBy(Long userId) {
		return this.userId.equals(userId);
	}


	// ============================================
	//       주문 상태 검증 (행위 가능 여부 판단)
	// ============================================

	public boolean canPay() {
		return this.status.canPay();
	}

	public boolean canCancelBeforePayment() {
		return this.status.canCancelBeforePayment();
	}

	public boolean canRefund() {
		return this.status.canRefund();
	}

	public boolean canConfirmPurchase() {
		return this.status.canConfirmPurchase();
	}

	public boolean canUpdateShippingInfo() {
		return this.status.canUpdateShippingInfo();
	}

	public boolean canUpdatePointUsed() {
		return this.status.canUpdatePointUsed();
	}


	// ============================================
	//         편의 메서드 (OrderAmount 위임)
	// ============================================

	public BigDecimal getTotalAmount() {
		return amount.getTotalAmount();
	}

	public Long getPointUsed() {
		return amount.getPointUsed();
	}

	public BigDecimal getFinalAmount() {
		return amount.getFinalAmount();
	}

	public Map<String, Object> toEventPayload() {
		Map<String, Object> payload = new HashMap<>();
		payload.put("orderId", this.orderId);
		payload.put("userId", this.userId);
		payload.put("amount", Map.of(
			"totalAmount", this.amount.getTotalAmount(),
			"finalAmount", this.amount.getFinalAmount(),
			"pointUsed", this.amount.getPointUsed()
		));
		payload.put("status", this.status.name());
		payload.put("items", this.orderItems.stream()
			.map(item -> Map.of(
				"timeDealStockId", item.getTimeDealStockId(),
				"quantity", item.getQuantity(),
				"unitPrice", item.getUnitPrice(),
				"discountPrice", item.getDiscountPrice()
			))
			.toList()
		);
		return payload;
	}


}
