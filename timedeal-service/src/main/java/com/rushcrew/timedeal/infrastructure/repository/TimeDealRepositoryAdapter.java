package com.rushcrew.timedeal.infrastructure.repository;

import com.rushcrew.timedeal.domain.repository.TimeDealRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class TimeDealRepositoryAdapter implements TimeDealRepository {

    private final TimeDealJpaRepository timeDealJpaRepository;
}
