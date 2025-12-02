package com.rushcrew.product.presentation.dto.request;

import jakarta.validation.constraints.NotBlank;

public record CreateOptionRequest(

    @NotBlank
    String size,

    @NotBlank
    String color
) {

}
