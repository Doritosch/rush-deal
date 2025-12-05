package com.rushcrew.order_service.order.infrastructure.adapter.out.client.feign;

import java.util.UUID;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import com.rushcrew.order_service.order.infrastructure.dto.timedeal.StockConfirmRequest;
import com.rushcrew.order_service.order.infrastructure.dto.timedeal.StockReservationRequest;
import com.rushcrew.order_service.order.infrastructure.dto.timedeal.StockReservationResponse;
import com.rushcrew.order_service.order.infrastructure.dto.timedeal.StockRestoreRequest;
import com.rushcrew.order_service.order.infrastructure.dto.timedeal.TimeDealResponse;
import com.rushcrew.order_service.order.infrastructure.dto.timedeal.TimeDealStockDetailResponse;

@FeignClient(name = "timedeal-service")
public interface TimeDealStockFeignClient {

	@GetMapping("/api/v1/timedeals/{timeDealId}")
	TimeDealResponse getTimeDeal(@PathVariable("timeDealId") String timeDealId);

	@GetMapping("/api/v1/timedeal-stocks/{timeDealStockId}")
	TimeDealStockDetailResponse getTimeDealStockDetail(@PathVariable("timeDealStockId") UUID timeDealStockId);

	@PostMapping("/api/v1/timedeal-stocks/reserve")
	StockReservationResponse reserveStock(@RequestBody StockReservationRequest request);

	@PostMapping("/api/v1/timedeal-stocks/confirm")
	void confirmStock(StockConfirmRequest request);

	@PostMapping("/api/v1/timedeal-stocks/restore")
	void restoreStock(StockRestoreRequest request);
}
