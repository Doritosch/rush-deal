package com.rushcrew.order_service.order.application.port.dto;

import java.math.BigDecimal;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class PointInfo {
	private Long userId;
	private BigDecimal balance;
}
