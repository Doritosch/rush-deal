package com.rushcrew.order_service.infrastructure.dto.point;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PointDeductResponse {
	private boolean success;
	private String message;
	private Long remainingPoint; // 차감 후 남은 포인트
}
