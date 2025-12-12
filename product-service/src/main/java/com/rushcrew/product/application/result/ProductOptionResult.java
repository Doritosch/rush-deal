package com.rushcrew.product.application.result;

import com.rushcrew.product.domain.entity.ProductOption;
import java.util.UUID;

public record ProductOptionResult(
    UUID optionId,
    String size,
    String color
) {

    public static ProductOptionResult from(ProductOption option) {
        return new ProductOptionResult(option.getId(), option.getSize(), option.getColor());
    }
}
