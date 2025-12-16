package com.rushcrew.timedeal.domain.model;

import java.time.Instant;

public record UpdateTimeDealParams(
    String title,
    String description,
    Long discountPrice,
    Long limitQuantity,
    Instant startAt,
    Instant endAt
) {

}
