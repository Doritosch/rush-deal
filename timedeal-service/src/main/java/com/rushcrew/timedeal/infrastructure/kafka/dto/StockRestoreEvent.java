package com.rushcrew.timedeal.infrastructure.kafka.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.UUID;

public record StockRestoreEvent(
    @NotNull
    UUID stockId,

    @NotNull
    Long quantity,

    @NotNull
    UUID orderId,

    @NotBlank
    String reason
) {

}
