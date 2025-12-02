package com.rushcrew.product.application.result;

import com.rushcrew.product.domain.entity.Product;

public record CreateProductResult(
    String productName,
    String description,
    Long price
) {

    public static CreateProductResult from(Product product) {
        return new CreateProductResult(
            product.getProductInfo().getName(),
            product.getProductInfo().getDescription(),
            product.getPrice().getAmount()
        );
    }
}
