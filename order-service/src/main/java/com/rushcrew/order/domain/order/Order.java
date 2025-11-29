package com.rushcrew.order.domain.order;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import com.rushcrew.order.domain.order.enums.OrderEventType;
import com.rushcrew.order.domain.order.enums.OrderStatus;
import com.rushcrew.order.domain.vo.ShippingInfo;

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
@AllArgsConstructor
@Builder
public class Order {

	@Id
	private UUID orderId;

	@Column(nullable = false)
	private Long userId;

	@Column(nullable = false, precision = 12, scale = 2)
	private BigDecimal totalAmount;

	@Column(nullable = false, precision = 12, scale = 2)
	@Builder.Default
	private BigDecimal pointUsed = BigDecimal.ZERO;

	@Column(nullable = false, precision = 12, scale = 2)
	private BigDecimal finalAmount;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false, length = 20)
	private OrderStatus status;

	@Embedded
	private ShippingInfo shippingInfo; // 현재 배송 서비스가 없어서 Embeddable 사용 --> 추후 배송 서비스를 독립적으로 개발하게 되면, 그때 ShippingInfo 테이블 분리 + Order에서 deliveryId 참조로 리팩토링 가능

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

	@OneToMany(mappedBy = "order")
	@Builder.Default
	private List<OrderHistory> histories = new ArrayList<>();


	// ============================================
	//                 도메인 로직
	// ============================================

	public static Order create(Long userId, List<OrderItem> orderItems, BigDecimal pointUsed, ShippingInfo shippingInfo) {
		BigDecimal totalAmount = orderItems.stream()
			.map(OrderItem::getSubtotal)
			.reduce(BigDecimal.ZERO, BigDecimal::add);

		BigDecimal finalAmount = totalAmount.subtract(pointUsed);

		if (finalAmount.compareTo(BigDecimal.ZERO) < 0) {
			throw new IllegalArgumentException("최종 결제 금액은 0보다 작을 수 없습니다.");
		}

		Order order = Order.builder()
			.orderId(UUID.randomUUID())
			.userId(userId)
			.totalAmount(totalAmount)
			.pointUsed(pointUsed)
			.finalAmount(finalAmount)
			.status(OrderStatus.PENDING)
			.shippingInfo(shippingInfo)
			.orderedAt(Instant.now())
			.build();

		// 주문 아이템 연관 관계 설정
		orderItems.forEach(order::addOrderItem);
		// 주문 생성 이력 기록
		order.addHistory(
			OrderEventType.ORDER_CREATED,
			null,
			OrderStatus.PENDING,
			OrderStatus.PENDING.getDescription());

		return order;
	}

	public void updateShippingInfo(ShippingInfo newShippingInfo) {
		validateStatus(OrderStatus.PENDING, "배송지 정보는 PENDING 상태에서만 수정할 수 있습니다.");
		this.shippingInfo = newShippingInfo;
		addHistory(OrderEventType.SHIPPING_INFO_UPDATED, status, status, "배송지 정보 수정");
	}

	public void completePayment() {
		validateStatus(OrderStatus.PENDING, "결제 완료 처리는 PENDING 상태에서만 가능합니다.");
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

	// 결제 전 주문 취소
	public void cancelBeforePayment(String reason) {
		validateStatus(OrderStatus.PENDING, "결제 전 주문 취소는 PENDING 상태에서만 가능합니다.");
		OrderStatus previousStatus = this.status;
		this.status = OrderStatus.CANCELLED;
		this.cancelledAt = Instant.now();
		addHistory(
			OrderEventType.CANCELLED_BEFORE_PAYMENT,
			previousStatus,
			OrderStatus.CANCELLED,
			OrderStatus.CANCELLED.getDescription()
		);
	}

	// 결제 후 주문 취소 (구매확정 전)
	public void cancelAfterPayment(String reason) {
		validateStatus(OrderStatus.PAID, "결제 후 주문 취소는 PAID 상태에서만 가능합니다.");
		OrderStatus previousStatus = this.status;
		this.status = OrderStatus.CANCELLED;
		this.cancelledAt = Instant.now();
		addHistory(
			OrderEventType.CANCELLED_AFTER_PAYMENT,
			previousStatus,
			OrderStatus.CANCELLED,
			OrderStatus.CANCELLED.getDescription()
		);
	}

	// 환불 (구매확정 후)
	public void refund(String reason) {
		validateStatus(OrderStatus.PAID, "환불은 PURCHASE_CONFIRMED 상태에서만 가능합니다.");
		OrderStatus previousStatus = this.status;
		this.status = OrderStatus.REFUNDED;
		this.refundedAt = Instant.now();
		addHistory(
			OrderEventType.REFUNDED,
			previousStatus,
			OrderStatus.REFUNDED,
			OrderStatus.REFUNDED.getDescription()
		);
	}

	public void confirmPurchase() {
		validateStatus(OrderStatus.PAID, "구매 확정은 PAID 상태에서만 가능합니다.");
		OrderStatus previousStatus = this.status;
		this.status = OrderStatus.PURCHASE_CONFIRMED;
		this.purchaseConfirmedAt = Instant.now();
		addHistory(
			OrderEventType.PURCHASE_CONFIRMED,
			previousStatus,
			OrderStatus.PURCHASE_CONFIRMED,
			OrderStatus.PURCHASE_CONFIRMED.getDescription()
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
		OrderHistory history = OrderHistory.builder()
			.orderHistoryId(UUID.randomUUID())
			.order(this)
			.eventType(eventType)
			.previousStatus(previousStatus)
			.newStatus(newStatus)
			.reason(reason)
			.build();
		this.histories.add(history);
	}

	private void validateStatus(OrderStatus expectedStatus, String errorMessage) {
		if (this.status != expectedStatus) {
			throw new IllegalStateException("%s 현재 상태 : %s".formatted(errorMessage, this.status));
		}
	}

	public boolean isOwnedBy(Long userId) {
		return this.userId.equals(userId);
	}


	// ============================================
	//       주문 상태 검증 (행위 가능 여부 판단)
	// ============================================

	public boolean canPay() {
		return this.status == OrderStatus.PENDING;
	}

	public boolean canCancelBeforePayment() {
		return this.status == OrderStatus.PENDING;
	}

	public boolean canCancelAfterPayment() {
		return this.status == OrderStatus.PAID;
	}

	public boolean canRefund() {
		return this.status == OrderStatus.PURCHASE_CONFIRMED;
	}

	public boolean canConfirmPurchase() {
		return this.status == OrderStatus.PAID;
	}

	public boolean canUpdateShippingInfo() {
		return this.status == OrderStatus.PENDING;
	}

}
