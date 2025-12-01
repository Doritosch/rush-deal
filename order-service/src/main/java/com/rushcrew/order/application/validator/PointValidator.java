package com.rushcrew.order.application.validator;

import java.math.BigDecimal;

import org.springframework.stereotype.Component;

import com.rushcrew.order.application.exception.NotEnoughPointException;
import com.rushcrew.order.infrastructure.client.UserServiceClient;
import com.rushcrew.order.infrastructure.client.dto.user.PointBalanceResponse;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class PointValidator {

	private final UserServiceClient userServiceClient;

	public void validate(Long userId, BigDecimal pointUsed) {
		if (pointUsed == null || pointUsed.compareTo(BigDecimal.ZERO) <= 0) {
			return; // 포인트 사용 X
		}
		PointBalanceResponse balance = userServiceClient.getPointBalance(userId);
		if (balance.getBalance().compareTo(pointUsed) < 0) {
			throw new NotEnoughPointException();
		}
	}
}
