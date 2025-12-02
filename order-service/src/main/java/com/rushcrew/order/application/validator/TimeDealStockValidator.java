package com.rushcrew.order.application.validator;

import org.springframework.stereotype.Component;

import com.rushcrew.order.application.exception.InvalidProductException;
import com.rushcrew.order.application.port.dto.TimeDealStockDetail;

@Component
public class TimeDealStockValidator {

	public void validate(TimeDealStockDetail stockDetail) {
		// 상품 활성 상태 확인
		if (!stockDetail.isActive()) {
			throw new InvalidProductException();
		}
		// 재고 상태 확인
		if ("SOLD_OUT".equals(stockDetail.getStatus())) {
			throw new IllegalArgumentException(
				"품절된 상품입니다: " + stockDetail.getProductId()
			);
		}
	}
}
