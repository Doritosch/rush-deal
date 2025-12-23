package com.rushcrew.product.presentation.dto.request;

import com.rushcrew.product.application.command.UpdateOptionCommand;

public record UpdateOptionRequest(
    String size,
    String color
) {

    public UpdateOptionCommand toCommand() {
        return new UpdateOptionCommand(
            this.size(),
            this.color()
        );
    }
}

