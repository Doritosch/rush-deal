package com.rushcrew.timedeal.application.event;

import java.util.UUID;

public record StockRestoredEvent(
    UUID stockId,
    Long quantity
) {

}
