package com.rushcrew.product.presentation.dto.request;

import com.rushcrew.product.domain.vo.Category;
import jakarta.validation.constraints.PositiveOrZero;

public record UpdateProductRequest(
    String companyName,
    String productName,
    String description,
    @PositiveOrZero Long price,
    Category category
) {

}
