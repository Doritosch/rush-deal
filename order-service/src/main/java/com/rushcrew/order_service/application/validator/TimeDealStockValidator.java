package com.rushcrew.order_service.application.validator;

import org.springframework.stereotype.Component;

import com.rushcrew.order_service.application.port.dto.TimeDealStockDetail;
import com.rushcrew.order_service.application.port.dto.TimeDealStockStatus;

@Component
public class TimeDealStockValidator {

	public void validate(TimeDealStockDetail stockDetail) {
		// 재고 상태: AVAILABLE 만 허용
		if (stockDetail.getStatus() != TimeDealStockStatus.AVAILABLE) {
			throw new IllegalArgumentException("재고가 판매 가능한 상태가 아닙니다.");
		}
	}
}
