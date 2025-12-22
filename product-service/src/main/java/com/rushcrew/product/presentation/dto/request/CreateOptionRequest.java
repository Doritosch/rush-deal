package com.rushcrew.product.presentation.dto.request;

import com.rushcrew.product.application.command.CreateOptionCommand;
import jakarta.validation.constraints.NotBlank;

public record CreateOptionRequest(

    @NotBlank
    String size,

    @NotBlank
    String color
) {

    public CreateOptionCommand toCommand() {
        return new CreateOptionCommand(size, color);
    }
}
