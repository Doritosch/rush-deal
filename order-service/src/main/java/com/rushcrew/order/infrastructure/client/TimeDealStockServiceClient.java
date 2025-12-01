package com.rushcrew.order.infrastructure.client;

import java.util.List;
import java.util.UUID;

import com.rushcrew.order.infrastructure.client.dto.timedeal.TimeDealProductResponse;
import com.rushcrew.order.infrastructure.client.dto.timedeal.StockReservationRequest;
import com.rushcrew.order.infrastructure.client.dto.timedeal.StockReservationResponse;
import com.rushcrew.order.infrastructure.client.dto.timedeal.StockStatusResponse;
import com.rushcrew.order.infrastructure.client.dto.timedeal.TimeDealResponse;

/**
 * 타임딜 재고 서비스 클라이언트
 * 주문 서비스는 이 클라이언트만 사용하여 타임딜/재고 정보 조회
 */
public interface TimeDealStockServiceClient {

	TimeDealResponse getTimeDeal(String timeDealId);

	/**
	 * 타임딜 재고 목록 조회
	 * @param timeDealStockIds 타임딜 재고 ID 목록
	 * @return productId, optionId 포함된 재고 정보
	 */
	List<TimeDealProductResponse> getTimeDealProducts(List<UUID> timeDealStockIds);

	StockStatusResponse getStockStatus(UUID timeDealStockId);

	// 재고 예약 요청
	StockReservationResponse reserveStock(StockReservationRequest request);

	// 결제 완료 후 재고 예약 확정
	void confirmReservation(UUID timeDealStockId, Integer quantity);

	// 재고 예약 취소
	void cancelReservation(UUID timeDealStockId, Integer quantity);
}
