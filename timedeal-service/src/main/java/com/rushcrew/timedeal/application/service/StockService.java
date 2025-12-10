package com.rushcrew.timedeal.application.service;

import com.rushcrew.timedeal.application.command.CreateStockCommand;
import com.rushcrew.timedeal.application.command.UpdateStockCountCommand;
import com.rushcrew.timedeal.application.result.CreateStockResult;
import com.rushcrew.timedeal.application.result.UpdateStockCountResult;
import java.util.UUID;

public interface StockService {

    CreateStockResult createStock(CreateStockCommand command);

    UpdateStockCountResult changeStockCount(UUID stockId, UpdateStockCountCommand command);

    void deleteStock(UUID stockId);
}
