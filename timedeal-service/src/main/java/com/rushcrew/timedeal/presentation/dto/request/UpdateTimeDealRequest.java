package com.rushcrew.timedeal.presentation.dto.request;

import com.rushcrew.timedeal.application.command.UpdateTimeDealCommand;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.Positive;
import java.time.Instant;

public record UpdateTimeDealRequest(
    String title,
    String description,

    @Positive
    Long discountPrice,

    @Positive
    Long limitQuantity,

    @Future(message = "이후의 시간만 입력 가능합니다.")
    Instant startAt,

    @Future(message = "이후의 시간만 입력 가능합니다.")
    Instant endAt
) {

    public UpdateTimeDealCommand toCommand() {
        return new UpdateTimeDealCommand(
            this.title,
            this.description,
            this.discountPrice,
            this.limitQuantity,
            this.startAt,
            this.endAt
        );
    }
}
