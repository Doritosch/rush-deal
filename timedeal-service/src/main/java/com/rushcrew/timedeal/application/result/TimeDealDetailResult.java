package com.rushcrew.timedeal.application.result;

import com.rushcrew.timedeal.domain.entity.TimeDeal;
import java.util.List;

public record TimeDealDetailResult(
    TimeDeal timeDeal,
    List<TimeDealProductResult> timeDealProdutResultList
) {

    public static TimeDealDetailResult of(
        TimeDeal timeDeal,
        List<TimeDealProductResult> timeDealProdutResultList
    ) {
        return new TimeDealDetailResult(timeDeal, timeDealProdutResultList);
    }
}
