package com.rushcrew.timedeal.application.service.impl;

import com.rushcrew.common.exception.BusinessException;
import com.rushcrew.timedeal.application.command.CreateTimeDealCommand;
import com.rushcrew.timedeal.application.model.ProductInfo;
import com.rushcrew.timedeal.application.service.TimeDealService;
import com.rushcrew.timedeal.domain.entity.TimeDeal;
import com.rushcrew.timedeal.domain.exception.TimeDealErrorCode;
import com.rushcrew.timedeal.domain.model.CreateTimeDealParams;
import com.rushcrew.timedeal.domain.port.ProductClient;
import com.rushcrew.timedeal.domain.repository.TimeDealRepository;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class TimeDealServiceImpl implements TimeDealService {

    private final TimeDealRepository timeDealRepository;
    private final ProductClient productClient;

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

        return newTimeDeal.getId();
    }

    @Override
    @Transactional
    public void forceEndTimeDeal(UUID timeDealId) {
        // TODO: 요청사용자가 MASTER 인지 확인하는 로직 추가 예정

        TimeDeal timeDeal = timeDealRepository.findById(timeDealId)
            .orElseThrow(() -> new BusinessException(TimeDealErrorCode.NOT_FOUND_TIME_DEAL));
        timeDeal.forceEnd();
    }
}
