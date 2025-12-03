package com.rushcrew.user_service.user.infrastructure.repository;

import com.rushcrew.user_service.user.domain.entity.User;
import com.rushcrew.user_service.user.domain.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UserRepositoryImpl implements UserRepository {

    private final UserJpaRepository userJpaRepository;

    @Override
    public boolean existsByEmail(String email) {
        return userJpaRepository.existsByEmail(email);
    }

    @Override
    public User save(User user) {
        return userJpaRepository.save(user);
    }
}
