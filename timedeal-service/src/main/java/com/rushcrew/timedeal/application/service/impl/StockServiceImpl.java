package com.rushcrew.timedeal.application.service.impl;

import com.rushcrew.common.exception.BusinessException;
import com.rushcrew.timedeal.application.command.CreateStockCommand;
import com.rushcrew.timedeal.application.result.CreateStockResult;
import com.rushcrew.timedeal.application.service.StockService;
import com.rushcrew.timedeal.domain.entity.TimeDealProduct;
import com.rushcrew.timedeal.domain.entity.TimeDealStock;
import com.rushcrew.timedeal.domain.exception.TimeDealErrorCode;
import com.rushcrew.timedeal.domain.port.StockCache;
import com.rushcrew.timedeal.domain.repository.StockRepository;
import com.rushcrew.timedeal.domain.repository.TimeDealRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class StockServiceImpl implements StockService {

    private final TimeDealRepository timeDealRepository;
    private final StockRepository stockRepository;
    private final StockCache stockCache;

    @Override
    @Transactional
    public CreateStockResult createStock(CreateStockCommand command) {
        // TODO: 요청한 사용자가 MASTER 권한 가지고 있는지 체크

        TimeDealProduct timeDealProduct =
            timeDealRepository.findProductByProductId(command.productId())
                .orElseThrow(() -> new BusinessException(TimeDealErrorCode.NOT_FOUND_PRODUCT));

        TimeDealStock newStock = TimeDealStock.create(command, timeDealProduct);
        stockRepository.save(newStock);
        stockCache.register(newStock.getId(), command.totalStock());

        return CreateStockResult.of(newStock, command.totalStock());
    }
}
