package com.rushcrew.product.application.result;

import com.rushcrew.product.domain.entity.Product;

public record ProductResult(
    String companyName,
    String productName,
    String description,
    Long price
) {

    public static ProductResult from(Product product) {
        return new ProductResult(
            product.getCompanyName(),
            product.getProductInfo().getName(),
            product.getProductInfo().getDescription(),
            product.getPrice().getAmount()
        );
    }
}
