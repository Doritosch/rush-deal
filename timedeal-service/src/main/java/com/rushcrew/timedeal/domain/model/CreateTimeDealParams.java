package com.rushcrew.timedeal.domain.model;

import com.rushcrew.timedeal.domain.vo.LimitQuantity;
import com.rushcrew.timedeal.domain.vo.Period;
import com.rushcrew.timedeal.domain.vo.Price;
import com.rushcrew.timedeal.domain.vo.TimeDealInfo;
import com.rushcrew.timedeal.domain.vo.TimeDealStatus;
import java.util.List;
import java.util.UUID;

public record CreateTimeDealParams(

    TimeDealInfo timeDealInfo,
    Price discountPrice,
    LimitQuantity limitQuantity,
    Period period,
    TimeDealStatus status,
    UUID productId,
    List<UUID> optionIds
) {

}
