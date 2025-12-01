package com.rushcrew.user_service.user.application;

import com.rushcrew.user_service.user.application.command.UserCreateCommand;
import com.rushcrew.user_service.user.application.result.UserCreateResult;
import com.rushcrew.user_service.user.domain.entity.User;
import com.rushcrew.user_service.user.domain.enums.UserRole;
import com.rushcrew.user_service.user.domain.repository.UserRepository;
import com.rushcrew.user_service.user.domain.service.UserValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserValidator userValidator;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserCreateResult createUser(UserCreateCommand command) {
        userValidator.validateEmailUniqueness(command.email());

        UserRole userRole = UserRole.from(command.role());

        String encodedPassword = passwordEncoder.encode(command.password());

        User user = User.create(
            command.email(),
            encodedPassword,
            command.name(),
            userRole
        );

        User savedUser = userRepository.save(user);

        // TODO point 지갑 생성

        return new UserCreateResult(
            savedUser.getUserId(),
            savedUser.getEmail(),
            savedUser.getName(),
            savedUser.getRole().name()
        );
    }
}
