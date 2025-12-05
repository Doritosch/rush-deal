package com.rushcrew.user_service.user.domain.service;

import com.rushcrew.user_service.user.domain.entity.User;

public interface UserValidator {
    void validateEmailUniqueness(String email);

    void validatePassword(User user, String password);
}
