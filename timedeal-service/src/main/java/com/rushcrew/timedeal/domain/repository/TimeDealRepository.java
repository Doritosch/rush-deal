package com.rushcrew.timedeal.domain.repository;

import com.rushcrew.timedeal.domain.entity.TimeDeal;
import java.util.Optional;
import java.util.UUID;

public interface TimeDealRepository {

    void save(TimeDeal timeDeal);

    Optional<TimeDeal> findById(UUID timeDealId);
}
