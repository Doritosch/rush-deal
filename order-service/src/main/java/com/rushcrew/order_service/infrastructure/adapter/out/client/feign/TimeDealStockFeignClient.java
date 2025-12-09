package com.rushcrew.order_service.infrastructure.adapter.out.client.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import com.rushcrew.order_service.infrastructure.dto.timedeal.StockReservationRequest;
import com.rushcrew.order_service.infrastructure.dto.timedeal.StockReservationResponse;
import com.rushcrew.order_service.infrastructure.dto.timedeal.StockRestoreRequest;
import com.rushcrew.order_service.infrastructure.dto.timedeal.TimeDealResponse;
import com.rushcrew.order_service.infrastructure.dto.timedeal.TimeDealStockDetailResponse;

// TODO: 민아님 API 확인 요청

@FeignClient(name = "timedeal-service")
public interface TimeDealStockFeignClient {

	@GetMapping("/api/v1/timedeals/{timeDealId}")
	TimeDealResponse getTimeDeal(@PathVariable("timeDealId") String timeDealId);

	@GetMapping("/api/v1/timedeal-stocks/{timeDealStockId}")
	TimeDealStockDetailResponse getTimeDealStockDetail(@PathVariable("timeDealStockId") String string);

	@PostMapping("/api/v1/timedeal-stocks/reserve")
	StockReservationResponse reserveStock(@RequestBody StockReservationRequest request);

	@PostMapping("/api/v1/timedeal-stocks/restore")
	void restoreStock(@RequestBody StockRestoreRequest request);
}
