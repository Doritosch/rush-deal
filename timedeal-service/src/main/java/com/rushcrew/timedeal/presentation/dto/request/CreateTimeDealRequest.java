package com.rushcrew.timedeal.presentation.dto.request;

import com.rushcrew.timedeal.domain.vo.TimeDealStatus;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.Instant;
import java.util.UUID;

public record CreateTimeDealRequest(

    @NotBlank
    String title,

    @NotBlank
    String description,

    @NotNull
    Long discountPrice,

    @Min(value = 1, message = "인당 구매 제한 수량은 0 또는 음수일 수 없습니다.")
    Long limitQuantity,

    @Future(message = "이후의 시간만 입력 가능합니다.")
    Instant startAt,

    @Future(message = "이후의 시간만 입력 가능합니다.")
    Instant endAt,

    @NotNull
    TimeDealStatus status,

    @NotNull(message = "타임딜에서 판매할 상품ID는 필수입니다.")
    UUID productId
) {

}
