package com.rushcrew.product.presentation.dto.response;

import com.rushcrew.product.application.result.CreateProductResult;

public record CreateProductResponse(
    String productName,
    String description,
    Long price
) {

    public static CreateProductResponse from(CreateProductResult result) {
        return new CreateProductResponse(
            result.productName(),
            result.description(),
            result.price()
        );
    }
}
