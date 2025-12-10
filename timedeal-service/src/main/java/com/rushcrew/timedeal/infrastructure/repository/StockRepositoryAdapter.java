package com.rushcrew.timedeal.infrastructure.repository;

import com.rushcrew.timedeal.application.result.StockResult;
import com.rushcrew.timedeal.domain.entity.TimeDealStock;
import com.rushcrew.timedeal.domain.repository.StockRepository;
import com.rushcrew.timedeal.domain.vo.TimeDealStockStatus;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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

    @Override
    public Page<StockResult> findStockResults(
        String keyword, UUID productId, TimeDealStockStatus status, Pageable pageable
    ) {
        return stockJpaRepository.findStockResults(keyword, productId, status, pageable);
    }

    @Override
    public StockResult findStockResultById(UUID stockId) {
        return stockJpaRepository.findStockResultById(stockId);
    }
}
