package com.rushcrew.order.infrastructure.client;

import com.rushcrew.order.infrastructure.client.dto.user.PointBalanceResponse;

public interface UserServiceClient {
	// 포인트 잔액 조회
	PointBalanceResponse getPointBalance(Long userId);
}
