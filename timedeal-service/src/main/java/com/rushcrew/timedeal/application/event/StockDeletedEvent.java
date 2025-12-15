package com.rushcrew.timedeal.application.event;

import java.util.UUID;

public record StockDeletedEvent(
    UUID stockId
) {

}
