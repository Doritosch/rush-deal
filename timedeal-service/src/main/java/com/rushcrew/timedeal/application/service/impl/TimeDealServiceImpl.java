package com.rushcrew.timedeal.application.service.impl;

import com.rushcrew.common.exception.BusinessException;
import com.rushcrew.timedeal.application.command.CreateTimeDealCommand;
import com.rushcrew.timedeal.application.command.UpdateTimeDealCommand;
import com.rushcrew.timedeal.application.event.TimeDealScheduledEvent;
import com.rushcrew.timedeal.application.model.ProductInfo;
import com.rushcrew.timedeal.application.result.TimeDealDetailResult;
import com.rushcrew.timedeal.application.result.TimeDealProductResult;
import com.rushcrew.timedeal.application.result.TimeDealResult;
import com.rushcrew.timedeal.application.result.UpdateTimeDealResult;
import com.rushcrew.timedeal.application.service.TimeDealService;
import com.rushcrew.timedeal.domain.entity.TimeDeal;
import com.rushcrew.timedeal.domain.exception.TimeDealErrorCode;
import com.rushcrew.timedeal.domain.model.CreateTimeDealParams;
import com.rushcrew.timedeal.domain.model.UpdateTimeDealParams;
import com.rushcrew.timedeal.domain.port.ProductClient;
import com.rushcrew.timedeal.domain.port.TimeDealQueueKey;
import com.rushcrew.timedeal.domain.repository.TimeDealRepository;
import com.rushcrew.timedeal.domain.vo.TimeDealStatus;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TimeDealServiceImpl implements TimeDealService {

    private final TimeDealRepository timeDealRepository;
    private final ProductClient productClient;
    private final ApplicationEventPublisher eventPublisher;

    @Override
    @Transactional
    public UUID createTimeDeal(CreateTimeDealCommand command) {
        ProductInfo productInfo = productClient.getProductItemIds(command.productId());

        // TODO: 요청사용자가 SELLER(productInfo.sellerId()) or MASTER 인지 확인하는 로직 추가 예정
        if (command.discountPrice().isMoreExpensiveThan(productInfo.price())) {
            throw new BusinessException(TimeDealErrorCode.MUST_BE_CHEAPER);
        }

        CreateTimeDealParams params = new CreateTimeDealParams(
            command.timeDealInfo(), command.discountPrice(),
            command.limitQuantity(), command.period(), command.status(),
            command.productId(), productInfo.optionIds()
        );
        TimeDeal newTimeDeal = TimeDeal.create(params);
        timeDealRepository.save(newTimeDeal);

        Instant startAt = command.period().getStartAt();
        Instant endAt = command.period().getEndAt();
        eventPublisher.publishEvent(
            new TimeDealScheduledEvent(newTimeDeal.getId(), startAt, TimeDealQueueKey.START));
        eventPublisher.publishEvent(
            new TimeDealScheduledEvent(newTimeDeal.getId(), endAt, TimeDealQueueKey.END));

        return newTimeDeal.getId();
    }

    @Override
    @Transactional
    public UpdateTimeDealResult updateTimeDeal(UUID timeDealId, UpdateTimeDealCommand command) {
        // TODO: 요청사용자가 SELLER(productInfo.sellerId()) or MASTER 인지 확인하는 로직 추가 예정

        TimeDeal timeDeal = timeDealRepository.findById(timeDealId)
            .orElseThrow(() -> new BusinessException(TimeDealErrorCode.NOT_FOUND_TIME_DEAL));

        UpdateTimeDealParams params = new UpdateTimeDealParams(
            command.title(), command.description(), command.discountPrice(),
            command.limitQuantity(), command.startAt(), command.endAt()
        );
        timeDeal.update(params);

        if (command.startAt() != null) {
            eventPublisher.publishEvent(
                new TimeDealScheduledEvent(
                    timeDeal.getId(), command.startAt(), TimeDealQueueKey.START)
            );
        }
        if (command.endAt() != null) {
            eventPublisher.publishEvent(
                new TimeDealScheduledEvent(
                    timeDeal.getId(), command.endAt(), TimeDealQueueKey.END)
            );
        }

        return UpdateTimeDealResult.from(timeDeal);
    }

    @Override
    @Transactional
    public void forceEndTimeDeal(UUID timeDealId) {
        // TODO: 요청사용자가 MASTER 인지 확인하는 로직 추가 예정

        TimeDeal timeDeal = timeDealRepository.findById(timeDealId)
            .orElseThrow(() -> new BusinessException(TimeDealErrorCode.NOT_FOUND_TIME_DEAL));
        timeDeal.forceEnd();
    }

    @Override
    public Page<TimeDealResult> getTimeDeals(TimeDealStatus status, Pageable pageable) {
        return timeDealRepository.findNotEndedByStatus(status, pageable);
    }

    @Override
    public TimeDealDetailResult getTimeDealDetail(UUID timeDealId) {
        TimeDeal timeDeal = timeDealRepository.findByIdAndStatusNot(timeDealId,
                TimeDealStatus.ENDED)
            .orElseThrow(() -> new BusinessException(TimeDealErrorCode.NOT_FOUND_TIME_DEAL));

        List<TimeDealProductResult> timeDealProdutResultList =
            timeDeal.getTimeDealProducts().stream().map(TimeDealProductResult::from).toList();

        return TimeDealDetailResult.of(timeDeal, timeDealProdutResultList);
    }

    @Override
    @Transactional
    public List<String> startTimeDeals(List<String> timeDealIds) {
        return executeStatusUpdate(timeDealIds, TimeDealStatus.IN_PROGRESS);
    }

    @Override
    @Transactional
    public List<String> endTimeDeals(List<String> timeDealIds) {
        return executeStatusUpdate(timeDealIds, TimeDealStatus.ENDED);
    }

    private List<String> executeStatusUpdate(
        List<String> timeDealIds,
        TimeDealStatus newStatus
    ) {

        List<UUID> idList = timeDealIds.stream().map(UUID::fromString).toList();
        List<TimeDeal> timeDealList = timeDealRepository.findAllById(idList);

        List<String> updatedIds = new ArrayList<>();

        for (TimeDeal timeDeal : timeDealList) {
            TimeDealStatus previousStatus = timeDeal.getStatus();

            switch (newStatus) {
                case IN_PROGRESS -> {
                    if (previousStatus == TimeDealStatus.SCHEDULED) {
                        timeDeal.updateStatus(newStatus);
                        updatedIds.add(timeDeal.getId().toString());
                    }
                }
                case ENDED -> {
                    if (previousStatus == TimeDealStatus.IN_PROGRESS
                        || previousStatus == TimeDealStatus.SOLD_OUT
                    ) {
                        timeDeal.updateStatus(newStatus);
                        updatedIds.add(timeDeal.getId().toString());
                    }
                }
            }
        }

        return updatedIds;
    }
}
