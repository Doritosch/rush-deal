package com.rushcrew.order.domain.order;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

import com.rushcrew.order.domain.order.enums.ReservationStatus;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
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
@Table(name = "p_order_reservation", schema = "order_schema")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class OrderReservation {
	@Id
	private UUID orderReservationId;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "order_id", nullable = false)
	private Order order;

	@Column(name = "time_deal_stock_id", nullable = false)
	private UUID timeDealStockId;

	@Column(nullable = false)
	private Integer quantity;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false, length = 20)
	private ReservationStatus status;

	@Column(nullable = false)
	private Instant reservedAt;

	@Column(nullable = false)
	private Instant expiresAt;

	private Instant confirmedAt;

	private Instant releasedAt;
}
