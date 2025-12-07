package com.rushcrew.order_service.infrastructure.adapter.out.client.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.rushcrew.order_service.infrastructure.dto.timedeal.TimeDealResponse;

@FeignClient(name = "timedeal-service")
public interface TimeDealStockFeignClient {
	@GetMapping("/api/v1/timedeals/{timeDealId}")
	TimeDealResponse getTimeDeal(@PathVariable("timeDealId") String timeDealId);
}
