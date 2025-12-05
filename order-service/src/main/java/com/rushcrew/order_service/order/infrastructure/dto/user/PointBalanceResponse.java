package com.rushcrew.order_service.order.infrastructure.dto.user;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class PointBalanceResponse {
	private Long userId;
	private BigDecimal balance;
	// private BigDecimal availablePoints; // 사용 가능한 포인트
	// private BigDecimal totalPoints; // 전체 포인트
}
