package com.rushcrew.order_service.infrastructure.dto.point;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PointRefundRequest {
	private Long userId;
	private Long pointAmount;
	private String sagaId;
}
