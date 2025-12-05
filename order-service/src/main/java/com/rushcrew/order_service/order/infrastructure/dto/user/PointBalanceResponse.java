package com.rushcrew.order_service.order.infrastructure.dto.user;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class PointBalanceResponse {
	@NonNull private Long userId;
	@NonNull private BigDecimal balance;
	// @NonNull private BigDecimal availablePoints; // 사용 가능한 포인트
	// @NonNull private BigDecimal totalPoints; // 전체 포인트
}
