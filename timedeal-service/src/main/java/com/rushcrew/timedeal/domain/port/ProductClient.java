package com.rushcrew.timedeal.domain.port;

import com.rushcrew.timedeal.application.model.ProductInfo;
import java.util.UUID;

public interface ProductClient {

    /**
     * 생성할 타임딜에서 판매할 상품의 옵션ID들과 sellerId, price 등을 가져오는 메서드
     */
    ProductInfo getProductItemIds(UUID productId);
}
