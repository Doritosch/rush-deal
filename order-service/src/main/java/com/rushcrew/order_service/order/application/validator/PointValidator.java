package com.rushcrew.order_service.order.application.validator;

import java.math.BigDecimal;

import org.springframework.stereotype.Component;

import com.rushcrew.common.exception.BusinessException;
import com.rushcrew.order_service.order.application.error.OrderErrorCode;
import com.rushcrew.order_service.order.application.port.dto.PointInfo;
import com.rushcrew.order_service.order.application.port.out.UserPort;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class PointValidator {

	private final UserPort userPort;

	public void validate(Long userId, BigDecimal pointUsed) {
		if (pointUsed == null || pointUsed.compareTo(BigDecimal.ZERO) <= 0) {
			return; // 포인트 사용 X
		}
		PointInfo balance = userPort.getPointBalance(userId);
		if (balance.getBalance().compareTo(pointUsed) < 0) {
			throw new BusinessException(OrderErrorCode.NOT_ENOUGH_POINTS);
		}
	}
}
