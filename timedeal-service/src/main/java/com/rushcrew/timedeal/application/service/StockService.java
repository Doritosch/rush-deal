package com.rushcrew.timedeal.application.service;

import com.rushcrew.timedeal.application.command.CreateStockCommand;
import com.rushcrew.timedeal.application.command.UpdateStockCountCommand;
import com.rushcrew.timedeal.application.result.CreateStockResult;
import com.rushcrew.timedeal.application.result.StockResult;
import com.rushcrew.timedeal.application.result.UpdateStockCountResult;
import com.rushcrew.timedeal.domain.vo.TimeDealStockStatus;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface StockService {

    CreateStockResult createStock(CreateStockCommand command);

    UpdateStockCountResult changeStockCount(UUID stockId, UpdateStockCountCommand command);

    void deleteStock(UUID stockId);

    Page<StockResult> getStocks(
        String keyword, UUID productId, TimeDealStockStatus status, Pageable pageable);

    StockResult getStock(UUID stockId);
}
