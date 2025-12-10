package com.rushcrew.timedeal.infrastructure.repository;

import com.rushcrew.timedeal.domain.entity.TimeDealStock;
import com.rushcrew.timedeal.domain.repository.StockRepository;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class StockRepositoryAdapter implements StockRepository {

    private final StockJpaRepository stockJpaRepository;

    @Override
    public void save(TimeDealStock newStock) {
        stockJpaRepository.save(newStock);
    }

    @Override
    public Optional<TimeDealStock> findNotDeletedById(UUID stockId) {
        return stockJpaRepository.findNotDeletedById(stockId);
    }
}
