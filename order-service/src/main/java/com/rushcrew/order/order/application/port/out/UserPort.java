package com.rushcrew.order.order.application.port.out;

import com.rushcrew.order.order.application.port.dto.PointInfo;

public interface UserPort {
	// 포인트 잔액 조회
	PointInfo getPointBalance(Long userId);
}
