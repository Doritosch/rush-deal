package com.rushcrew.product.application;

import java.util.List;

public record ProductFilter(
    List<String> category,
    Long minPrice,
    Long maxPrice
) {

    public static ProductFilter of(List<String> category, Long minPrice, Long maxPrice) {
        return new ProductFilter(category, minPrice, maxPrice);
    }
}

