package com.rushcrew.timedeal.application.command;

import java.time.Instant;

public record UpdateTimeDealCommand(
    String title,
    String description,
    Long discountPrice,
    Long limitQuantity,
    Instant startAt,
    Instant endAt
) {

}
