package com.rushcrew.product.application.result;

import com.rushcrew.product.domain.entity.ProductOption;
import java.util.UUID;

public record UpdateOptionResult(
    UUID optionId,
    String size,
    String color
) {

    public static UpdateOptionResult from(ProductOption option) {
        return new UpdateOptionResult(option.getId(), option.getSize(), option.getColor());
    }
}
