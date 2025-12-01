package com.rushcrew.order.infrastructure.client;

import java.util.List;
import java.util.UUID;

import com.rushcrew.order.infrastructure.client.dto.timedeal.ProductResponseDto;
import com.rushcrew.order.infrastructure.client.dto.timedeal.StockReservationResponseDto;
import com.rushcrew.order.infrastructure.client.dto.timedeal.StockResponseDto;
import com.rushcrew.order.infrastructure.client.dto.timedeal.TimeDealResponseDto;

public interface TimeDealStockServiceClient {
	// 타임딜 정보 조회
	TimeDealResponseDto getTimeDeal(String timeDealId);

	// 타임딜 상품 목록 조회
	List<ProductResponseDto> getTimeDealProducts(List<UUID> timeDealStockIds);

	// 타임딜 재고 상태 조회
	StockResponseDto getTimeDealStockStatus(UUID timeDealStockId);

	// 타임딜 재고 예약 요청
	StockReservationResponseDto reserveStock(StockReservationRequestDto request);
}
