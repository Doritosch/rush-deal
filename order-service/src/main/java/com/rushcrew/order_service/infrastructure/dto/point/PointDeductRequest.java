package com.rushcrew.order_service.infrastructure.dto.point;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PointDeductRequest {
	private Long userId;
	private BigDecimal amount;
	private String sagaId;
	private String reason;
}
