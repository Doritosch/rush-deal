package com.rushcrew.timedeal.domain.repository;

import com.rushcrew.timedeal.application.result.StockResult;
import com.rushcrew.timedeal.domain.entity.TimeDealStock;
import com.rushcrew.timedeal.domain.vo.TimeDealStockStatus;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface StockRepository {

    void save(TimeDealStock newStock);

    Optional<TimeDealStock> findNotDeletedById(UUID stockId);

    Page<StockResult> findStockResults(
        String keyword, UUID productId, TimeDealStockStatus status, Pageable pageable);

}
