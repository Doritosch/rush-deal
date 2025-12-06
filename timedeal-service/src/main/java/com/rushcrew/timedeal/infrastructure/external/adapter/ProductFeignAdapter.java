package com.rushcrew.timedeal.infrastructure.external.adapter;

import com.rushcrew.timedeal.application.model.ProductInfo;
import com.rushcrew.timedeal.domain.port.ProductClient;
import com.rushcrew.timedeal.infrastructure.external.client.ProductFeignClient;
import com.rushcrew.timedeal.infrastructure.external.dto.ProductInfoDTO;
import com.rushcrew.timedeal.infrastructure.external.dto.request.ProductItemIdsRequest;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ProductFeignAdapter implements ProductClient {

    private final ProductFeignClient productFeignClient;

    @Override
    public ProductInfo getProductItemIds(UUID productId) {
        ProductItemIdsRequest productItemIdsRequest = ProductItemIdsRequest.of(productId);
        ProductInfoDTO productInfo = productFeignClient.getProductItemIds(productItemIdsRequest);
        return ProductInfo.of(productInfo.optionIds(), productInfo.sellerId(), productInfo.price());
    }
}
