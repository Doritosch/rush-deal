package com.rushcrew.timedeal.infrastructure.external.client;

import com.rushcrew.timedeal.infrastructure.external.dto.ProductInfoDTO;
import com.rushcrew.timedeal.infrastructure.external.dto.request.ProductItemIdsRequest;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "product-service", path = "/internal/v1/products")
public interface ProductFeignClient {

    @PostMapping("/item-ids")
    ProductInfoDTO getProductItemIds(
        @RequestBody ProductItemIdsRequest productItemIdsRequest
    );
}
