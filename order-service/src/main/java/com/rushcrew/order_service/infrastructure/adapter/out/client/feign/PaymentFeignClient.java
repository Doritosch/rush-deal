package com.rushcrew.order_service.infrastructure.adapter.out.client.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import com.rushcrew.order_service.infrastructure.dto.payment.PaymentRequest;
import com.rushcrew.order_service.infrastructure.dto.payment.PaymentResponse;

@FeignClient(name = "payment-service")
public interface PaymentFeignClient {
	@PostMapping("/api/v1/payments/request")
	PaymentResponse requestPayment(@RequestBody PaymentRequest request);
}
