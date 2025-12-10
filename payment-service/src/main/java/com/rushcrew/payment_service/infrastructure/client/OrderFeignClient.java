package com.rushcrew.payment_service.infrastructure.client;


import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.UUID;

@FeignClient(name = "order-service")
public interface OrderFeignClient {

//    @GetMapping("/api/v1/orders/{orderId}")
//    OrderResponse getOrder(@PathVariable("orderId") UUID orderId);
}
