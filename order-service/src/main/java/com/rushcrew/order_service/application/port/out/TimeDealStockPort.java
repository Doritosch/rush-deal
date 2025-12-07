package com.rushcrew.order_service.application.port.out;

import java.util.UUID;

import com.rushcrew.order_service.application.port.dto.StockReservationResult;
import com.rushcrew.order_service.application.port.dto.TimeDealInfo;
import com.rushcrew.order_service.application.port.dto.TimeDealStockDetail;

import lombok.NonNull;

public interface TimeDealStockPort {
	/* 타임딜 정보 조회 */
	TimeDealInfo getTimeDeal(@NonNull UUID timeDealId);

	/* 타임딜 재고 상세 정보 조회 */
	TimeDealStockDetail getTimeDealStockDetail(@NonNull UUID timeDealStockId);

	/* 재고 예약 요청 */
	StockReservationResult reserveStock(@NonNull UUID timeDealStockId, @NonNull Integer quantity, @NonNull Long userId);

	/* 재고 복구 */
	void restoreStock(@NonNull UUID timeDealStockId, @NonNull Integer quantity, @NonNull UUID sagaId, @NonNull String reason);
}
