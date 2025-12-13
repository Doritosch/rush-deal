package com.rushcrew.timedeal.application.service.impl;

import com.rushcrew.common.exception.BusinessException;
import com.rushcrew.timedeal.application.command.ConfirmStockCommand;
import com.rushcrew.timedeal.application.command.CreateStockCommand;
import com.rushcrew.timedeal.application.command.ReserveStockCommand;
import com.rushcrew.timedeal.application.command.UpdateStockCountCommand;
import com.rushcrew.timedeal.application.event.StockReservedEvent;
import com.rushcrew.timedeal.application.result.ConfirmStockResult;
import com.rushcrew.timedeal.application.result.CreateStockResult;
import com.rushcrew.timedeal.application.result.ReserveStockResult;
import com.rushcrew.timedeal.application.result.StockResult;
import com.rushcrew.timedeal.application.result.UpdateStockCountResult;
import com.rushcrew.timedeal.application.service.StockService;
import com.rushcrew.timedeal.domain.entity.StockLog;
import com.rushcrew.timedeal.domain.entity.TimeDealProduct;
import com.rushcrew.timedeal.domain.entity.TimeDealStock;
import com.rushcrew.timedeal.domain.exception.TimeDealErrorCode;
import com.rushcrew.timedeal.domain.port.StockCache;
import com.rushcrew.timedeal.domain.repository.StockRepository;
import com.rushcrew.timedeal.domain.repository.TimeDealRepository;
import com.rushcrew.timedeal.domain.vo.OrderId;
import com.rushcrew.timedeal.domain.vo.TimeDealProductStatus;
import com.rushcrew.timedeal.domain.vo.TimeDealStockStatus;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class StockServiceImpl implements StockService {

    private final TimeDealRepository timeDealRepository;
    private final StockRepository stockRepository;
    private final StockCache stockCache;
    private final ApplicationEventPublisher eventPublisher;

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

    @Override
    @Transactional
    public UpdateStockCountResult changeStockCount(UUID stockId, UpdateStockCountCommand command) {
        // TODO: 요청한 사용자가 MASTER 권한 가지고 있는지 체크

        TimeDealStock stock = getStockOrThrow(stockId);
        Long previousStock = stock.getStockCounts().getAvailable();

        Long quantity = command.quantity().getQuantity();
        validQuantity(stock, quantity);

        stock.changeAvailable(quantity, command.reason());
        stockCache.changeCount(stockId, quantity);

        return UpdateStockCountResult.of(
            stock.getId(), stock.getTimeDealProduct().getId(),
            previousStock, stock.getStockCounts().getAvailable(), quantity
        );
    }

    @Override
    public Page<StockResult> getStocks(
        String keyword, UUID productId, TimeDealStockStatus status, Pageable pageable
    ) {
        // TODO: 요청한 사용자가 MASTER 권한 가지고 있는지 체크
        String pattern = (keyword == null || keyword.isBlank()) ? null : "%" + keyword + "%";
        return stockRepository.findStockResults(pattern, productId, status, pageable);
    }

    @Override
    public StockResult getStock(UUID stockId) {
        // TODO: 요청한 사용자가 MASTER or SELLER 권한 가지고 있는지 체크
        return stockRepository.findStockResultById(stockId);
    }

    @Override
    @Transactional
    public void deleteStock(UUID stockId) {
        // TODO: 요청한 사용자가 MASTER 권한 가지고 있는지 체크

        TimeDealStock stock = getStockOrThrow(stockId);

        // TODO: 추후에 사용자 정보 가지고 오면 주석처리 풀 예정
//        stock.delete(userId);
        stockCache.evict(stockId);
    }

    @Override
    @Transactional
    @Retryable(
        retryFor = {ObjectOptimisticLockingFailureException.class},
        maxAttempts = 5,
        backoff = @Backoff(delay = 5, maxDelay = 20, multiplier = 2)
    )
    public ReserveStockResult reserveStock(ReserveStockCommand command) {
        // TODO: 요청한 사용자가 ORDER 권한을 가지고 있는지 체크

        TimeDealStock stock = getStockOrThrow(command.stockId());

        stock.reserve(command.quantity(), command.orderId());
        eventPublisher.publishEvent(
            new StockReservedEvent(command.stockId(), command.quantity().getQuantity())
        );

        return ReserveStockResult
            .of(stock.getStockCounts().getAvailable(), "재고가 예약되었습니다.");
    }

    @Override
    @Transactional
    public ConfirmStockResult confirmStock(ConfirmStockCommand command) {
        // TODO: 요청한 사용자가 ORDER 권한을 가지고 있는지 체크

        UUID orderId = command.orderId().getOrderId();
        TimeDealStock stock = getStockOrThrow(command.stockId());
        StockLog log = getLastLogOrThrow(command.stockId(), orderId);

        // 주문 당시 기록된 수량과 현재 처리하려는 수량이 동일한지 검증
        log.validateOrderQuantity(command.quantity());

        stock.confirm(OrderId.of(orderId), command.quantity());

        return ConfirmStockResult.of(orderId);
    }

    // ------------------------------------------------------------------------------------

    private TimeDealStock getStockOrThrow(UUID stockId) {
        return stockRepository.findNotDeletedById(stockId)
            .orElseThrow(() -> new BusinessException(TimeDealErrorCode.NOT_FOUND_STOCK));
    }

    private StockLog getLastLogOrThrow(UUID stockId, UUID orderId) {
        return stockRepository.findLastByStockIdAndOrderId(stockId, orderId)
            .orElseThrow(() -> new BusinessException(TimeDealErrorCode.NOT_FOUND_ORDER));
    }

    private void validQuantity(TimeDealStock stock, Long quantity) {
        // 변화할 재고 수량이 음수일 때, 품절인지 아닌지 체크
        if (quantity < 0 &&
            TimeDealProductStatus.OUT_OF_STOCK.equals(stock.getTimeDealProduct().getStatus())) {
            throw new BusinessException(TimeDealErrorCode.CAN_NOT_DECREASE_STOCK);
        }

        // 남은 재고 수량이 감소할 수량보다 적은지 체크
        if (stock.getStockCounts().getAvailable() + quantity < 0) {
            throw new BusinessException(TimeDealErrorCode.CAN_NOT_DECREASE_BELOW_ZERO);
        }
    }
}
