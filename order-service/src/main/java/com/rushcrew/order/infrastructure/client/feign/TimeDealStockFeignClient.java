package com.rushcrew.order.infrastructure.client.feign;

import java.util.List;
import java.util.UUID;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import com.rushcrew.order.infrastructure.client.TimeDealStockServiceClient;
import com.rushcrew.order.infrastructure.client.dto.timedeal.StockStatusResponse;
import com.rushcrew.order.infrastructure.client.dto.timedeal.StockReservationRequest;
import com.rushcrew.order.infrastructure.client.dto.timedeal.StockReservationResponse;
import com.rushcrew.order.infrastructure.client.dto.timedeal.TimeDealResponse;
import com.rushcrew.order.infrastructure.client.dto.timedeal.TimeDealStockResponse;

@FeignClient(name = "timedeal-service")
public interface TimeDealStockFeignClient extends TimeDealStockServiceClient {

	@GetMapping("/api/v1/timedeals/{timeDealId}")
	@Override
	TimeDealResponse getTimeDeal(@PathVariable("timeDealId") String timeDealId);

	@GetMapping("/api/v1/timedeal-stocks/{timeDealStockId}")
	@Override
	TimeDealStockResponse getTimeDealStock(@PathVariable("timeDealStockId") UUID timeDealStockId);

	@PostMapping("/api/v1/timedeal-stocks/reserve")
	@Override
	StockReservationResponse reserveStock(@RequestBody StockReservationRequest request);

	@PostMapping("/api/v1/timedeal-stocks/{timeDealStockId}/confirm")
	@Override
	void confirmReservation(
		@PathVariable("timeDealStockId") UUID timeDealStockId,
		@RequestParam("quantity") Integer quantity
	);

	@PostMapping("/api/v1/timedeal-stocks/{timeDealStockId}/cancel")
	@Override
	void cancelReservation(
		@PathVariable("timeDealStockId") UUID timeDealStockId,
		@RequestParam("quantity") Integer quantity
	);

}
