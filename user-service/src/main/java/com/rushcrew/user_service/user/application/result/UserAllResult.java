package com.rushcrew.user_service.user.application.result;

import com.rushcrew.user_service.user.domain.entity.User;

public record UserAllResult(
    Long userId,
    String email,
    String name,
    String role
) {
    public static UserAllResult fromDomain(User user) {
        return new UserAllResult(
            user.getUserId(),
            user.getEmail(),
            user.getName(),
            user.getRole().name()
        );
    }
}
