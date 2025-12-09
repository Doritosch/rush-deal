package com.rushcrew.timedeal.application.service;

import com.rushcrew.timedeal.application.command.CreateTimeDealCommand;
import com.rushcrew.timedeal.application.command.UpdateTimeDealCommand;
import com.rushcrew.timedeal.application.result.UpdateTimeDealResult;
import java.util.UUID;

public interface TimeDealService {

    UUID createTimeDeal(CreateTimeDealCommand command);

    UpdateTimeDealResult updateTimeDeal(UUID timeDealId, UpdateTimeDealCommand command);

    void forceEndTimeDeal(UUID timeDealId);
}
