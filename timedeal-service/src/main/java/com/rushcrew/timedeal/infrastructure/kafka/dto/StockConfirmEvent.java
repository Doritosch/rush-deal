package com.rushcrew.timedeal.infrastructure.kafka.dto;

import jakarta.validation.constraints.NotNull;
import java.util.UUID;

public record StockConfirmEvent(
    @NotNull
    UUID orderId,

    @NotNull
    UUID stockId,

    @NotNull
    Long quantity
) {

}
