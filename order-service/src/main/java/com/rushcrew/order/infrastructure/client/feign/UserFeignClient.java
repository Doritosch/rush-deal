package com.rushcrew.order.infrastructure.client.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.rushcrew.order.infrastructure.client.UserServiceClient;
import com.rushcrew.order.infrastructure.client.dto.user.PointBalanceResponse;

@FeignClient(name = "user-service")
public interface UserFeignClient extends UserServiceClient {
	@GetMapping("/api/v1/points/wallet")
	@Override
	PointBalanceResponse getPointBalance(@PathVariable("userId") Long userId);

	// @GetMapping("/api/v1/users/{userId}/points/balance")
	// @Override
	// PointBalanceResponse getPointBalance(@PathVariable("userId") Long userId);
}
