package com.rushcrew.product.presentation.dto.request;

import com.rushcrew.product.domain.vo.Category;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import java.util.List;

public record CreateProductRequest(

    @NotNull
    Long userId, // TODO: 추후에 토큰에서 받아오도록 변경할 예정

    @NotBlank
    String companyName,

    @NotBlank
    String productName,

    @NotBlank
    String description,

    @NotNull @PositiveOrZero
    Long price,

    @NotNull
    Category category,

    @NotEmpty @Valid
    List<CreateOptionRequest> optionRequests
) {

}
