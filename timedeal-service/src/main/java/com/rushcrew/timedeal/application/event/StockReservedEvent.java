package com.rushcrew.timedeal.application.event;

import java.util.UUID;

public record StockReservedEvent(
    UUID stockId,
    Long quantity
) {

}
