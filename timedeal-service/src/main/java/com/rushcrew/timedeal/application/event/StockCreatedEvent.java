package com.rushcrew.timedeal.application.event;

import java.util.UUID;

public record StockCreatedEvent(
    UUID stockId,
    Long totalStock
) {

}
