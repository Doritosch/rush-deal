package com.rushcrew.order_service.order.application.validator;

import org.springframework.stereotype.Component;

import com.rushcrew.common.exception.BusinessException;
import com.rushcrew.order_service.order.application.error.OrderErrorCode;
import com.rushcrew.order_service.order.application.port.dto.TimeDealStatus;
import com.rushcrew.order_service.order.application.port.dto.TimeDealStockDetail;

@Component
public class TimeDealStockValidator {

	public void validate(TimeDealStockDetail stockDetail) {
		// 상품 활성 상태 확인
		if (!stockDetail.isActive()) {
			throw new BusinessException(OrderErrorCode.INVALID_PRODUCT);
		}
		// 재고 상태 확인
		if (stockDetail.getStatus() == TimeDealStatus.SOLD_OUT) {
			throw new BusinessException(OrderErrorCode.SOLD_OUT_PRODUCT);
		}
	}
}
