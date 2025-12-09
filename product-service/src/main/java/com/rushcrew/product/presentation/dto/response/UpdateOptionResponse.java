package com.rushcrew.product.presentation.dto.response;

import com.rushcrew.product.application.result.UpdateOptionResult;
import java.util.UUID;

public record UpdateOptionResponse(
    UUID optionId,
    String size,
    String color
) {

    public static UpdateOptionResponse from(UpdateOptionResult result) {
        return new UpdateOptionResponse(result.optionId(), result.size(), result.color());
    }
}
