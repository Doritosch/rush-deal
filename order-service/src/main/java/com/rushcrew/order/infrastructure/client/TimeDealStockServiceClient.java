package com.rushcrew.order.infrastructure.client;

import java.util.UUID;

import com.rushcrew.order.infrastructure.client.dto.timedeal.StockReservationRequest;
import com.rushcrew.order.infrastructure.client.dto.timedeal.StockReservationResponse;
import com.rushcrew.order.infrastructure.client.dto.timedeal.TimeDealResponse;
import com.rushcrew.order.infrastructure.client.dto.timedeal.TimeDealStockDetailResponse;

public interface TimeDealStockServiceClient {

	TimeDealResponse getTimeDeal(String timeDealId);

	// 타임딜 재고 정보 조회 (상품 ID, 옵션 ID 포함), 상품명, 상품설명, 판매자 정보 등도 함께 반환받아야 됨
	TimeDealStockDetailResponse getTimeDealStockDetail(UUID timeDealStockId);

	// 재고 예약 요청
	StockReservationResponse reserveStock(StockReservationRequest request);

	// 결제 완료 후 재고 예약 확정
	void confirmReservation(UUID timeDealStockId, Integer quantity);

	// 재고 예약 취소
	void cancelReservation(UUID timeDealStockId, Integer quantity);
}
