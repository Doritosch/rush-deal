package com.rushcrew.product.presentation.dto.response;

import com.rushcrew.product.application.result.ProductResult;

public record ProductResponse(
    String companyName,
    String productName,
    String description,
    Long price
) {

    public static ProductResponse from(ProductResult result) {
        return new ProductResponse(
            result.companyName(),
            result.productName(),
            result.description(),
            result.price()
        );
    }
}

