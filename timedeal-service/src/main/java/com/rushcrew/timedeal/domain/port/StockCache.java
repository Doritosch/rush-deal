package com.rushcrew.timedeal.domain.port;

import java.util.UUID;

public interface StockCache {

    /**
     * Redis에 타임딜 재고 id, 구매가능한 quantity 캐시
     *
     * @param stockId   : Redis key에 사용되는 타임딜 재고 ID
     * @param available : 구매 가능한 재고 수량
     */
    void register(UUID stockId, Long available);

}
