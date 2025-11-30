package com.rushcrew.user_service.user.domain.service;

public interface UserValidator {
    void validateEmailUniqueness(String email);
}
