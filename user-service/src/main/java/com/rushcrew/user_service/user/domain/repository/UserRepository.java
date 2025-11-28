package com.rushcrew.user_service.user.domain.repository;

import com.rushcrew.user_service.user.domain.entity.User;
import java.util.Optional;

public interface UserRepository {

    boolean existsByEmail(String email);

    Optional<User> findByEmail(String email);

    User save(User user);
}
