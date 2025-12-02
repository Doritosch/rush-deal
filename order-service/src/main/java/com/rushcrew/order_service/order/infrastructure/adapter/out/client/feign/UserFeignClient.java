package com.rushcrew.order_service.order.infrastructure.adapter.out.client.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.rushcrew.order_service.order.infrastructure.dto.user.PointBalanceResponse;

@FeignClient(name = "user-service")
public interface UserFeignClient {
	@GetMapping("/api/v1/points/wallet")
	PointBalanceResponse getPointBalance(@PathVariable("userId") Long userId);

	// @GetMapping("/api/v1/users/{userId}/points/balance")
	// @Override
	// PointBalanceResponse getPointBalance(@PathVariable("userId") Long userId);
}
