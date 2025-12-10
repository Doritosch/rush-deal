package com.rushcrew.timedeal.application.service;

import com.rushcrew.timedeal.application.command.CreateStockCommand;
import com.rushcrew.timedeal.application.result.CreateStockResult;

public interface StockService {

    CreateStockResult createStock(CreateStockCommand command);

}
