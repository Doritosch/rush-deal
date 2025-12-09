package com.rushcrew.product.application.command;

import com.rushcrew.product.presentation.dto.request.UpdateOptionRequest;

public record UpdateOptionCommand(
    String size,
    String color
) {

    public static UpdateOptionCommand from(UpdateOptionRequest request) {
        return new UpdateOptionCommand(
            request.size(),
            request.color()
        );
    }
}


