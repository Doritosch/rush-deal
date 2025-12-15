package com.rushcrew.timedeal.application.event;

import java.util.UUID;

public record StockChangedEvent(
    UUID stockId,
    Long quantity
) {

}
