package com.rushcrew.timedeal.presentation.dto.response;

import com.rushcrew.timedeal.application.result.TimeDealDetailResult;
import com.rushcrew.timedeal.application.result.TimeDealProductResult;
import com.rushcrew.timedeal.domain.entity.TimeDeal;
import java.util.List;

public record TimeDealDetailResponse(
    TimeDeal timeDeal,
    List<TimeDealProductResult> timeDealProdutResultList
) {

    public static TimeDealDetailResponse from(TimeDealDetailResult result) {
        return new TimeDealDetailResponse(result.timeDeal(), result.timeDealProdutResultList());
    }
}
