package com.rushcrew.product.application.command;

import com.rushcrew.product.domain.vo.Category;
import com.rushcrew.product.presentation.dto.request.UpdateProductRequest;

public record UpdateProductCommand(
    String companyName,
    String productName,
    String description,
    Long price,
    Category category
) {

    public static UpdateProductCommand from(UpdateProductRequest request) {
        return new UpdateProductCommand(
            request.companyName(),
            request.productName(),
            request.description(),
            request.price(),
            request.category()
        );
    }
}
