package com.rushcrew.timedeal.application.service;

import com.rushcrew.timedeal.application.command.CreateTimeDealCommand;
import com.rushcrew.timedeal.application.command.UpdateTimeDealCommand;
import com.rushcrew.timedeal.application.result.TimeDealDetailResult;
import com.rushcrew.timedeal.application.result.TimeDealResult;
import com.rushcrew.timedeal.application.result.UpdateTimeDealResult;
import com.rushcrew.timedeal.domain.vo.TimeDealStatus;
import java.util.List;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface TimeDealService {

    UUID createTimeDeal(Long userId, String role, CreateTimeDealCommand command);

    UpdateTimeDealResult updateTimeDeal(
        Long userId, String role, UUID timeDealId, UpdateTimeDealCommand command);

    void forceEndTimeDeal(UUID timeDealId);

    Page<TimeDealResult> getTimeDeals(TimeDealStatus status, Pageable pageable);

    TimeDealDetailResult getTimeDealDetail(UUID timeDealId);

    List<String> startTimeDeals(List<String> timeDealIds);

    List<String> endTimeDeals(List<String> timeDealIds);
}
