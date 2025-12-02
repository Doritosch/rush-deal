package com.rushcrew.product.application.command;

import com.rushcrew.product.presentation.dto.request.CreateOptionRequest;

public record CreateOptionCommand(
    String size,
    String color
) {

    public static CreateOptionCommand from(CreateOptionRequest request) {
        return new CreateOptionCommand(request.size(), request.color());
    }
}
