package com.rushcrew.timedeal.application.service;

import com.rushcrew.timedeal.application.command.CreateTimeDealCommand;
import java.util.UUID;

public interface TimeDealService {

    UUID createTimeDeal(CreateTimeDealCommand command);

    void forceEndTimeDeal(UUID timeDealId);
}
