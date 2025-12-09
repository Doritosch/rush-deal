package com.rushcrew.payment_service.infrastructure.client;

import org.springframework.cloud.openfeign.FeignClient;

@FeignClient(name = "order-service")
public interface OrderFeignClient {
    //
}
