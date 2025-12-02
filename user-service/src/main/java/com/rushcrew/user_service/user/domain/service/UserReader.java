package com.rushcrew.user_service.user.domain.service;

import com.rushcrew.user_service.user.domain.entity.User;
import com.rushcrew.user_service.user.domain.repository.UserRepository;

public class UserReader {

    private final UserRepository userRepository;

    public UserReader(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User getUserByEmail(String email) {
        return userRepository.findByEmail(email)
            .orElseThrow(() -> new IllegalArgumentException("유저가 존재하지 않습니다."));
    }
}
