package com.rushcrew.timedeal.infrastructure.repository;

import com.rushcrew.timedeal.domain.entity.TimeDeal;
import com.rushcrew.timedeal.domain.repository.TimeDealRepository;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class TimeDealRepositoryAdapter implements TimeDealRepository {

    private final TimeDealJpaRepository timeDealJpaRepository;

    @Override
    public void save(TimeDeal timeDeal) {
        timeDealJpaRepository.save(timeDeal);
    }

    @Override
    public Optional<TimeDeal> findById(UUID timeDealId) {
        return timeDealJpaRepository.findById(timeDealId);
    }
}
