package com.rushcrew.timedeal.application.command;

import com.rushcrew.timedeal.domain.vo.LimitQuantity;
import com.rushcrew.timedeal.domain.vo.Period;
import com.rushcrew.timedeal.domain.vo.Price;
import com.rushcrew.timedeal.domain.vo.TimeDealInfo;
import com.rushcrew.timedeal.domain.vo.TimeDealStatus;
import com.rushcrew.timedeal.presentation.dto.request.CreateTimeDealRequest;
import java.util.UUID;

public record CreateTimeDealCommand(

    TimeDealInfo timeDealInfo,
    Price discountPrice,
    LimitQuantity limitQuantity,
    Period period,
    TimeDealStatus status,
    UUID productId
) {

    public static CreateTimeDealCommand from(CreateTimeDealRequest request) {
        return new CreateTimeDealCommand(
            TimeDealInfo.of(request.title(), request.description()),
            Price.of(request.discountPrice()),
            LimitQuantity.of(request.limitQuantity()),
            Period.of(request.startAt(), request.endAt()),
            request.status(),
            request.productId()
        );
    }
}
