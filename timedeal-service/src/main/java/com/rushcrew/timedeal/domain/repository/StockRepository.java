package com.rushcrew.timedeal.domain.repository;

import com.rushcrew.timedeal.domain.entity.TimeDealStock;
import java.util.Optional;
import java.util.UUID;

public interface StockRepository {

    void save(TimeDealStock newStock);

    Optional<TimeDealStock> findNotDeletedById(UUID stockId);
}
